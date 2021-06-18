package com.firstappjava.client;

public class Task extends Job {
    @Override
    public String toString() {
        return  "ts|"+
                taskID +"|"+
                date +"|"+
                startTime +"|"+
                durationMinute +"|"+
                (title.trim().isEmpty()?"&":title) + "|" +
                (detail.trim().isEmpty()?"&":detail) + "|"+
                isDone + "|"+
                user_Fk;
    }

    public Task(int taskID, String _date, int durationMinute, String title, String detail, boolean isDone, int user_Fk) {
        this.taskID = taskID;


        String []s=_date.split(" "),
                s1=s[0].split("-"),
                s2=s[1].split(":");

        this.date=new NewDate( Integer.parseInt(s1[2]),
                Integer.parseInt(s1[1]),
                Integer.parseInt(s1[0]) );

        this.startTime=new NewTime(Integer.parseInt(s2[0]),
                Integer.parseInt(s2[1]),
                Integer.parseInt(s2[2]) );

        this.durationMinute = durationMinute;
        this.title = title;
        this.detail = detail;
        this.isDone=isDone;
        this.user_Fk = user_Fk;
    }

    public int getTaskID() {
        return taskID;
    }
    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }
    @Override
    public NewDate getDate() {
        return date;
    }
    public void setDate(NewDate date) {
        this.date = date;
    }
    @Override
    public NewTime getStartTime() {
        return startTime;
    }
    public void setStartTime(NewTime startTime) {
        this.startTime = startTime;
    }
    public int getDurationMinute() {
        return durationMinute;
    }
    public void setDurationMinute(int durationMinute) {
        this.durationMinute = durationMinute;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
    public boolean isDone() {
        return isDone;
    }
    public void setDone(boolean done) {
        isDone = done;
    }
    public int getUser_Fk() {
        return user_Fk;
    }
    public void setUser_Fk(int user_Fk) {
        this.user_Fk = user_Fk;
    }

    @Override
    public String Typeof() {
        return "Task";
    }

    private int taskID;
    private NewDate date;
    private NewTime startTime;
    private int durationMinute;
    private String title;
    private String detail;
    boolean isDone;
    private int user_Fk;
}
