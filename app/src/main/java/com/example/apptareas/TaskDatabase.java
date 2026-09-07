package com.example.apptareas;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabase extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "tasks.db";
    private static final int DATABASE_VERSION = 2; // CAMBIADO A 2

    private static final String TABLE_TASKS = "tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PRIORITY = "priority"; // NUEVO
    private static final String KEY_DUE_DATE = "due_date";
    private static final String KEY_CREATED_DATE = "created_date";
    private static final String KEY_ASSIGNED_USER = "assigned_user";

    public TaskDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_TASKS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_TITLE + " TEXT NOT NULL,"
                + KEY_DESCRIPTION + " TEXT,"
                + KEY_STATUS + " TEXT DEFAULT 'pendiente',"
                + KEY_PRIORITY + " TEXT DEFAULT 'media'," // NUEVO
                + KEY_DUE_DATE + " TEXT,"
                + KEY_CREATED_DATE + " TEXT,"
                + KEY_ASSIGNED_USER + " TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + KEY_PRIORITY + " TEXT DEFAULT 'media'");
        }
    }

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_STATUS, task.getStatus());
        values.put(KEY_PRIORITY, task.getPriority()); // NUEVO
        values.put(KEY_DUE_DATE, task.getDueDate());
        values.put(KEY_CREATED_DATE, task.getCreatedDate());
        values.put(KEY_ASSIGNED_USER, task.getAssignedUser());
        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result;
    }

    public Task getTask(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_STATUS, KEY_PRIORITY,
                        KEY_DUE_DATE, KEY_CREATED_DATE, KEY_ASSIGNED_USER},
                KEY_ID + "=?", new String[]{String.valueOf(id)},
                null, null, null);

        Task task = new Task();
        if (cursor != null && cursor.moveToFirst()) {
            task.setId(cursor.getInt(0));
            task.setTitle(cursor.getString(1));
            task.setDescription(cursor.getString(2));
            task.setStatus(cursor.getString(3));
            task.setPriority(cursor.getString(4)); // NUEVO
            task.setDueDate(cursor.getString(5));
            task.setCreatedDate(cursor.getString(6));
            task.setAssignedUser(cursor.getString(7));
        }
        if (cursor != null) cursor.close();
        db.close();
        return task;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_STATUS, KEY_PRIORITY,
                        KEY_DUE_DATE, KEY_CREATED_DATE, KEY_ASSIGNED_USER},
                null, null, null, null, KEY_CREATED_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                task.setTitle(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                task.setStatus(cursor.getString(3));
                task.setPriority(cursor.getString(4)); // NUEVO
                task.setDueDate(cursor.getString(5));
                task.setCreatedDate(cursor.getString(6));
                task.setAssignedUser(cursor.getString(7));
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return taskList;
    }

    public List<Task> getTasksByStatus(String status) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_STATUS, KEY_PRIORITY,
                        KEY_DUE_DATE, KEY_CREATED_DATE, KEY_ASSIGNED_USER},
                KEY_STATUS + "=?", new String[]{status},
                null, null, KEY_CREATED_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                task.setTitle(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                task.setStatus(cursor.getString(3));
                task.setPriority(cursor.getString(4)); // NUEVO
                task.setDueDate(cursor.getString(5));
                task.setCreatedDate(cursor.getString(6));
                task.setAssignedUser(cursor.getString(7));
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return taskList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_STATUS, task.getStatus());
        values.put(KEY_PRIORITY, task.getPriority()); // NUEVO
        values.put(KEY_DUE_DATE, task.getDueDate());
        values.put(KEY_ASSIGNED_USER, task.getAssignedUser());
        int result = db.update(TABLE_TASKS, values, KEY_ID + "=?",
                new String[]{String.valueOf(task.getId())});
        db.close();
        return result;
    }

    public void deleteTask(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, KEY_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Task> searchTasks(String query) {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String searchQuery = "%" + query + "%";
        Cursor cursor = db.query(TABLE_TASKS,
                new String[]{KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_STATUS, KEY_PRIORITY,
                        KEY_DUE_DATE, KEY_CREATED_DATE, KEY_ASSIGNED_USER},
                KEY_TITLE + " LIKE ? OR " + KEY_DESCRIPTION + " LIKE ?",
                new String[]{searchQuery, searchQuery},
                null, null, KEY_CREATED_DATE + " DESC");

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(0));
                task.setTitle(cursor.getString(1));
                task.setDescription(cursor.getString(2));
                task.setStatus(cursor.getString(3));
                task.setPriority(cursor.getString(4)); // NUEVO
                task.setDueDate(cursor.getString(5));
                task.setCreatedDate(cursor.getString(6));
                task.setAssignedUser(cursor.getString(7));
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.close();
        return taskList;
    }
}