package com.codepath.simpletodo.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.codepath.simpletodo.TodoItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i_enpinghsieh on 2016/11/10.
 */

public class TodoItemDAO {
    public static final String TABLE_NAME = "todo_list";

    public static final String KEY_ID = "_id";

    public static final String TITLE_COLUMN = "title";
    public static final String CONTENT_COLUMN = "content";
    public static final String DUE_DATE_COLUMN = "due_date";
    public static final String PRIORITY_COLUMN = "priority";

    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    CONTENT_COLUMN + " TEXT, " +
                    DUE_DATE_COLUMN + " INTEGER, " +
                    PRIORITY_COLUMN + " INTEGER)";

    private SQLiteDatabase db;

    public TodoItemDAO(Context context) {
        db = DBHelper.getDatabase(context);
    }

    public void close() {
        db.close();
    }

    public TodoItem insert(TodoItem item) {

        ContentValues cv = new ContentValues();
        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(CONTENT_COLUMN, item.getContent());
        cv.put(DUE_DATE_COLUMN,item.getDueDate().getTime());
        cv.put(PRIORITY_COLUMN,item.getPriority());

        long id = db.insert(TABLE_NAME, null, cv);
        item.setId(id);

        return item;
    }

    public boolean update(TodoItem item) {
        ContentValues cv = new ContentValues();

        cv.put(TITLE_COLUMN, item.getTitle());
        cv.put(CONTENT_COLUMN, item.getContent());
        cv.put(DUE_DATE_COLUMN,item.getDueDate().getTime());
        cv.put(PRIORITY_COLUMN,item.getPriority());

        String where = KEY_ID + "=" + item.getId();

        return db.update(TABLE_NAME, cv, where, null) > 0;
    }

    public boolean delete(long id){

        String where = KEY_ID + "=" + id;

        return db.delete(TABLE_NAME, where , null) > 0;
    }

    public List<TodoItem> getAll() {
        List<TodoItem> result = new ArrayList<>();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    public TodoItem get(long id) {
        TodoItem item = null;
        String where = KEY_ID + "=" + id;
        Cursor result = db.query(
                TABLE_NAME, null, where, null, null, null, null, null);

        if (result.moveToFirst()) {
            item = getRecord(result);
        }

        result.close();
        return item;
    }

    private TodoItem getRecord(Cursor cursor) {
        TodoItem result = new TodoItem();

        result.setId(cursor.getLong(0));
        result.setTitle(cursor.getString(1));
        result.setContent(cursor.getString(2));
        result.setDueDate(new Date(cursor.getLong(3)));
        result.setPriority(cursor.getInt(4));

        return result;
    }

    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        cursor.close();

        return result;
    }

}
