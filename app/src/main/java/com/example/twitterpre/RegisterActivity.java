package com.example.twitterpre;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.twitterpre.DB.UserDao;
import com.example.twitterpre.DB.UserDatabase;
import com.example.twitterpre.DB.UserEntity;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText name = findViewById(R.id.name);
        EditText mail = findViewById(R.id.Id);
        EditText pass = findViewById(R.id.pass);

        Button register = findViewById(R.id.register_button);
        Button login = findViewById(R.id.login_button);
        register.setOnClickListener(v -> {
            isUser(name, mail, pass);
        });
        login.setOnClickListener(v -> {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        });
    }

    private boolean valDataName(EditText name) {
        String valName = name.getText().toString().trim();

        if (valName.isEmpty()) {
            name.setError("名前を入力してください。");
            return false;
        }else if (valName.length() < 4) {
            name.setError("最低でも３文字入力してください。");
            return false;
        }else {
            name.setError(null);
            return true;
        }
    }

    private boolean valDataMail(EditText mail) {
        String valMail = mail.getText().toString().trim();
        String email_matches = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if (valMail.isEmpty()) {
            mail.setError("メールアドレスを入力してください。");
            return false;
        }else if (valMail.matches(email_matches)) {
            mail.setError("正しく入力してください。");
            return false;
        }
        else {
            mail.setError(null);
            return true;
        }
    }

    private boolean valDataPass(EditText pass) {
        String valPass = pass.getText().toString().trim();

        if (valPass.isEmpty()) {
            pass.setError("パスワードを入力してください。");
            return false;
        }else if (valPass.length() < 4) {
            pass.setError("最低でも８文字入力してください。");
            return false;
        }else {
            pass.setError(null);
            return true;
        }
    }

    private void isUser(EditText name, EditText mail, EditText pass) {
        if (!valDataName(name) | !valDataMail(mail) | !valDataPass(pass)) {
            return;
        }else {
            asyncTask(name, mail, pass);
        }
    }

    private void asyncTask(EditText name, EditText mail, EditText pass) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Handler handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                try {
                    if (msg.obj != null) {
                        Toast.makeText(getApplicationContext(), "ユーザーの新規登録に失敗しました。", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "ユーザーの新規登録に成功しました。", Toast.LENGTH_LONG).show();
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
        UserEntity user = new UserEntity();
        user.setName(name.getText().toString());
        user.setMail(mail.getText().toString());
        user.setPass(mail.getText().toString());
        UserDatabase db = UserDatabase.getInstance(getApplicationContext());
        UserDao userDao = db.userDao();
        executorService.execute(new AsyncWrite(handler, userDao, user));
    }

    private static class AsyncWrite implements Runnable{
        Handler handler;
        UserDao userDao;
        UserEntity user;
        public AsyncWrite(Handler handler, UserDao userDao, UserEntity user) {
            this.handler = handler;
            this.userDao = userDao;
            this.user = user;
        }
        @Override
        public void run() {
            Message msg = new Message();
            try {
                userDao.registerUser(user);
                msg.obj = null;
            }catch (Exception e) {
                msg.obj = 1;
            }finally {
                handler.sendMessage(Message.obtain(msg));
            }
        }
    }
}