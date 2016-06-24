package com.codepath.simpletodo;

/**
 * Created by Alicia P on 15-Jun-16.
 */


public class Todo {

    public long id;
    public String title;
    //public Date date;
    public boolean urgent;//true=todo is urgent


    //CHECK WHICH CONSTRUCTORS AND METHODS I ACTUALLY USE


    public Todo() {

    }


    public Todo(String newTitle, boolean newUrgent) {//public Todo(long newId, String newTitle, boolean newUrgent) {
        //id = newId;
        title = newTitle;
        urgent = newUrgent;
    }

    public Todo(Todo t) {
        id = t.getId();
        title = t.getTitle();
        urgent = t.isUrgent();
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
