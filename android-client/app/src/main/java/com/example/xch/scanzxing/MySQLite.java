package com.example.xch.scanzxing;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class MySQLite extends SQLiteOpenHelper{

    public MySQLite(@Nullable Context context) {
        super(context, "db.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Response(ResponseID nvarchar(450) primary key,TimeStamp bigint not null,Longitude bigint not null, Latitude bigint not null, IMEI nvarchar(17),RefSurveyID nvarchar(450))");
        db.execSQL("create table Answer(AnswerKey nvarchar(450) primary key,QuestionNum int not null, Content nvarchar(1024), RefResponseID nvarchar(450))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
