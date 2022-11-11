package com.example.taskorg.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TaskModel extends TaskId implements Serializable {

    private String task,
            deadline_date,
            deadline_time,
            address,
            category,
            keywords,
            description,
            create_date,
            create_time;
    private int status;
    private boolean important;
    private ArrayList<String> tasksBefore;

    public ArrayList<String> getTasksBefore() {
        return tasksBefore;
    }

    public void setTasksBefore(ArrayList<String> tasksBefore) {
        this.tasksBefore = tasksBefore;
    }

    public String getCreate_date() {
        return create_date;
    }

    public String getCreate_time() {
        return create_time;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean getImportant() {
        return important;
    }

    public void setImportant(boolean important) {
        this.important = important;
    }

    public String getDeadline_time() {
        return deadline_time;
    }

    public void setDeadline_time(String deadline_time) {
        this.deadline_time = deadline_time;
    }

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