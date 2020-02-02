package com.gxd.demo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by guoxiaodong on 2019-06-17 16:03
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public MySQLiteHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /**
     * 验证:
     * adb shell
     * su(#)
     * sqlite3 /data/data/com.gxd.demo/databases/person.db
     * .tables
     * .header on
     * .mode column
     * select * from person;
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**
         * NULL     值是一个 NULL 值。
         * INTEGER	值是一个带符号的整数，根据值的大小存储在 1、2、3、4、6 或 8 字节中。
         * REAL     值是一个浮点值，存储为 8 字节的 IEEE 浮点数字。
         * TEXT     值是一个文本字符串，使用数据库编码（UTF-8、UTF-16BE 或 UTF-16LE）存储。
         * BLOB     值是一个 blob 数据，完全根据它的输入存储。
         */
        db.execSQL("create table person(id INTEGER primary key autoincrement,name TEXT,age INTEGER,bmi REAL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion == 2 && oldVersion == 1) {
            // 第一步将旧表改为临时表
            db.execSQL("alter table person rename to _temp_person");
            // 第二步创建新表(添加字段或去掉字段)
            db.execSQL("create table person(id INTEGER primary key autoincrement,name TEXT,age INTEGER)");
            // 第三步将旧表中的原始数据保存到新表中以防遗失
            db.execSQL("insert into person (name,age) select name,age from _temp_person");
            // 第四步删除临时备份表
            db.execSQL("drop table _temp_person");
        }

    }
}
