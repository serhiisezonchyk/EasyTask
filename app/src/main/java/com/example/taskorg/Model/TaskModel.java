package com.example.taskorg.Model;

public class TaskModel extends TaskId {

    private String task , deadline_date;
    private int status;


    public String getTask() {
        return task;
    }

    public String getDeadline_date() {
        return deadline_date;
    }

    public int getStatus() {
        return status;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public void setDeadline_date(String due) {
        this.deadline_date = due;
    }

    public void setStatus(int status) {
        this.status = status;
    }

}