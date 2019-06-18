package com.gxd.demo;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.gxd.demo.database.MySQLiteHelper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends Activity {
    private EditText mEditText;
    private BufferedWriter mBufferedWriter;
    private TextView mTextView;
    private BufferedReader mBufferedReader;
    private MySQLiteHelper mMySQLiteHelper;
    private SQLiteDatabase mReadableDatabase;
    private ContentValues mContentValues;
    private TextView queryDatabaseTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditText = findViewById(R.id.edit_text);
        mTextView = findViewById(R.id.file_content_tv);
        queryDatabaseTv = findViewById(R.id.query_database_tv);

        mMySQLiteHelper = new MySQLiteHelper(MainActivity.this, "person.db", null, 1);
        mContentValues = new ContentValues();
    }

    public void onWriteClick(View view) {
        String text = mEditText.getText().toString().trim();
        if (!TextUtils.isEmpty(text)) {
            try {
                /**
                 * MODE_PRIVATE:默认操作模式，代表该文件是似有数据，只能被引用本身访问，在该模式下，写入的内容会覆盖原文件的内容。
                 * MODE_APPEND:模式会检查文件是否存在，存在就往文件追加内容，否则就创建文件。
                 * MODE_WORLD_READABLE、MODE_WORLD_WRITEABLE:用来控制其他应用是否有权限读写该文件
                 */
                FileOutputStream fileOutputStream = openFileOutput("data.txt", Context.MODE_PRIVATE);
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
                mBufferedWriter = new BufferedWriter(outputStreamWriter);
                mBufferedWriter.write(text);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (mBufferedWriter != null) {
                    try {
                        mBufferedWriter.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void onReadClick(View view) {
        try {
            FileInputStream fileInputStream = openFileInput("data.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            mBufferedReader = new BufferedReader(inputStreamReader);
            String readLine;
            while ((readLine = mBufferedReader.readLine()) != null) {
                mTextView.append(readLine);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mBufferedReader != null) {
                try {
                    mBufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * getReadableDatabase()中有调用getWritableDatabase()
     * 当磁盘已满时,getWritableDatabase()会抛异常,getReadableDatabase()只返回一个可读不可写的数据库对象
     */
    public void onCreateTableClick(View view) {
        mReadableDatabase = mMySQLiteHelper.getReadableDatabase();
    }

    /**
     * 验证:
     * select * from person;
     */
    public void onInsertTableClick(View view) {
        if (mReadableDatabase == null) {
            mReadableDatabase = mMySQLiteHelper.getReadableDatabase();
        }
        mContentValues.put("name", "Lucy");
        mContentValues.put("age", "23");
        mContentValues.put("bmi", 20.0);
        mReadableDatabase.insert("person", null, mContentValues);
        mContentValues.clear();

        mContentValues.put("name", "Jerry");
        mContentValues.put("age", "25");
        mContentValues.put("bmi", 23.0);
        mReadableDatabase.insert("person", null, mContentValues);
        mContentValues.clear();
    }

    public void onDeleteTableClick(View view) {
        if (mReadableDatabase == null) {
            mReadableDatabase = mMySQLiteHelper.getReadableDatabase();
        }
        mReadableDatabase.delete("person", "age>?", new String[]{"23"});
    }

    public void onUpdateTableClick(View view) {
        if (mReadableDatabase == null) {
            mReadableDatabase = mMySQLiteHelper.getReadableDatabase();
        }
        mContentValues.put("age", "27");
        mReadableDatabase.update("person", mContentValues, "name=?", new String[]{"Jerry"});
        mContentValues.clear();
    }

    public void onQueryTableClick(View view) {
        if (mReadableDatabase == null) {
            mReadableDatabase = mMySQLiteHelper.getReadableDatabase();
        }
        Cursor cursor = mReadableDatabase.query("person", null, null, null, null, null, null);
        if (cursor != null) {
            queryDatabaseTv.setText("");
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                int age = cursor.getInt(cursor.getColumnIndex("age"));
                queryDatabaseTv.append("姓名:" + name);
                queryDatabaseTv.append("年龄:" + age + "\n");
            }
            cursor.close();
        }
    }
}
