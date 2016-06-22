package com.codepath.simpletodo;

/**
 * Created by Alicia P on 15-Jun-16.
 */


public class Todo {
    //public class Todo extends SugarRecord{
    public long id;
    public String title;
    //public Date date;
    public boolean urgent;


    public Todo() {

    }


    public Todo(long newId, String newTitle, boolean newUrgent) {
        id = newId;
        title = newTitle;
        urgent = newUrgent;
    }


    public long getId() {
        return id;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        title = newTitle;
    }

    public boolean isUrgent() {
        return urgent;
    }
}
