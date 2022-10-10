package com.example.twitterpre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.twitterpre.DB.UserDao;
import com.example.twitterpre.DB.UserDatabase;
import com.example.twitterpre.DB.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText id = findViewById(R.id.Id);
        EditText pass = findViewById(R.id.pass);

        Button login = findViewById(R.id.login_button);
        Button register = findViewById(R.id.register_button);
        login.setOnClickListener(v -> {
            isUser(id, pass);
        });
        register.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean valDataId(EditText id) {
        String valMail = id.getText().toString().trim();

        if (valMail.isEmpty()) {
            id.setError("ユーザーIDを入力してください。");
            return false;
        }else {
            id.setError(null);
            return true;
        }
    }

    private boolean valDataPass(EditText pass) {
        String valPass = pass.getText().toString().trim();

        if (valPass.isEmpty()){
            pass.setError("パスワードを入力してください。");
            return false;
        }else if (valPass.length() < 8){
            pass.setError("最低でも８文字入力してください。");
            return false;
        } else {
            pass.setError(null);
            return true;
        }
    }

    private void isUser(EditText id, EditText pass) {
        if (!valDataId(id) | !valDataPass(pass)) {
            return;
        }else {
            asyncTask(id, pass);
        }
    }

    private void asyncTask(EditText mail, EditText pass) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Handler handler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg != null) {
                        Toast.makeText(getApplicationContext(), "ユーザー情報に誤りがあるか、すでに登録されている可能性があります。", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "ログインに成功しました。", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                }catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "エラーが発生しました。", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }finally {
                    executorService.shutdown();
                }
            }
        };
        UserDatabase db = UserDatabase.getInstance(getApplicationContext());
        UserDao userDao = db.userDao();
        executorService.execute(new AsyncRead(handler, userDao, mail, pass));
    }
    private static class AsyncRead implements Runnable{
        Handler handler;
        UserDao userDao;
        EditText id,pass;
        public AsyncRead(Handler handler, UserDao userDao, EditText id, EditText pass) {
            this.handler = handler;
            this.userDao = userDao;
            this.id = id;
            this.pass = pass;
        }
        public void run() {
            UserEntity user = userDao.loadLogin(Integer.parseInt(id.getText().toString()), pass.getText().toString());
            Message msg = new Message();
            msg.obj =user;
            handler.sendMessage(Message.obtain(msg));
        }
    }
}