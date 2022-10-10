package com.example.twitterpre.DB;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert
    void registerUser(UserEntity userEntity);

    @Query("SELECT * from users where id and pass=(:pass)")
    UserEntity loadLogin(int id, String pass);
}
