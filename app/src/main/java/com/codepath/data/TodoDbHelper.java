/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codepath.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.codepath.data.TodoContract.TodoEntry;
import com.codepath.simpletodo.Todo;

import java.util.ArrayList;

/**
 * Manages a local database for todo data.
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "todos.db";
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TODO_TABLE = "CREATE TABLE " + TodoEntry.TABLE_NAME + " (" +

                TodoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +

                TodoEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                //TodoEntry.COLUMN_DATE + " INTEGER NOT NULL, " +
                TodoEntry.COLUMN_URGENT + " INTEGER NOT NULL);";


        //MAKE SURE TABLE EXISTS BEFORE ATTEMPTING TO CREATE IT!!!
        sqLiteDatabase.execSQL(SQL_CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }


    public ArrayList<Todo> readAllItems() {//read from database:

        //create db instance:
        //TodosDbHelper helper = TodosDbHelper.getInstance(this);
        //just pass the context and use the singleton method

        ArrayList<Todo> records = new ArrayList<Todo>();

        SQLiteDatabase db = getReadableDatabase();

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(
                TodoEntry.TABLE_NAME,// Table to query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                TodoEntry.COLUMN_ID + " ASC" // sort order
        );


        try {
            if (cursor.moveToFirst()) {
                do {
                    Todo newTodo = new Todo();

                    newTodo.id = cursor.getLong(cursor.getColumnIndex(TodoEntry.COLUMN_ID));
                    newTodo.title = cursor.getString(cursor.getColumnIndex(TodoEntry.COLUMN_TITLE));
                    //newTodo.date = cursor.getDate(cursor.getColumnIndex("COLUMN_DATE"));

                    //boolean b = (i != 0);
                    //int i = (b) ? 1 : 0;
                    newTodo.urgent = (cursor.getInt(cursor.getColumnIndex(TodoEntry.COLUMN_URGENT)) != 0);

                    records.add(newTodo);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            //Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return records;
    }


    public void cleanDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TodoEntry.TABLE_NAME);
        onCreate(db);
    }

    public long createDummyTodo(String title) {

        Todo t = new Todo();

        t.setTitle(title);
        t.urgent = true;
        return insertTodo(t);
    }

    public long insertTodo(Todo t) {
        // Create and/or open the database for writing
        SQLiteDatabase db = getWritableDatabase();

        long insertionResult = -1L;

        ContentValues values = new ContentValues();

        values.put(TodoEntry.COLUMN_TITLE, t.title);
        //values.put(TodoEntry.COLUMN_DATE, t.date);
        values.put(TodoEntry.COLUMN_URGENT, t.urgent);

        // It's a good idea to wrap our insert in a transaction. This helps with performance and ensures
        // consistency of the database.
        db.beginTransaction();
        try {
            insertionResult = db.insertOrThrow(TodoEntry.TABLE_NAME, null, values);//SQLite auto increments the primary key column

            db.setTransactionSuccessful();
        } catch (Exception e) {
            //Log.d(TAG, "Error while trying to add todo to database");
            Log.d("", "Error while trying to add todo to database");
            //Toast.makeText(this, "Error while trying to add todo to database", Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }

        return insertionResult;
    }


    public long updateTodo(Todo t) {
        SQLiteDatabase db = getWritableDatabase();

        long insertionResult = -1L;

        ContentValues values = new ContentValues();

        values.put(TodoEntry.COLUMN_TITLE, t.title);
        //values.put(TodoEntry.COLUMN_DATE, t.date);
        values.put(TodoEntry.COLUMN_URGENT, t.urgent);

        db.beginTransaction();
        try {
            insertionResult = db.update(TodoEntry.TABLE_NAME, values, TodoEntry.COLUMN_ID + "=" + t.getId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            //Log.d(TAG, "Error while trying to edit todo in database");
            Log.d("", "Error while trying to edit todo in database");
            //Toast.makeText(this, "Error while trying to add todo to database", Toast.LENGTH_LONG).show();
        } finally {
            db.endTransaction();
        }

        return insertionResult;
    }


    public long deleteTodo(Todo t) {
        SQLiteDatabase db = getWritableDatabase();

        long insertionResult = -1L;


        db.beginTransaction();
        try {
            insertionResult = db.delete(TodoEntry.TABLE_NAME, TodoEntry.COLUMN_ID + "=" + t.getId(), null);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            //Log.d(TAG, "Error while trying to edit todo in database");
            //Log.d("", "Error while trying to delete todo from database");
        } finally {
            db.endTransaction();
        }

        return insertionResult;
    }

}
