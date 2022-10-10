package com.example.twitterpre.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {UserEntity.class}, version = 1)
public abstract class UserDatabase extends RoomDatabase {
    public abstract UserDao userDao();

    private static UserDatabase INSTANCE;

    public static UserDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), UserDatabase.class, "DB")
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}
