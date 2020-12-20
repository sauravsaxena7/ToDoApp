package com.jlcsoftware.todoapp.Model;

public class TodoModel {

    private String id, title, description;

    boolean finished;


    public TodoModel(String id, String title, String description, boolean finished) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.finished = finished;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }



    public boolean isFinished() {
        return finished;
    }
}
