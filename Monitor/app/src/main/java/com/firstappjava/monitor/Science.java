package com.firstappjava.monitor;

public class Science extends Job {
    public Science(int scienceId, int scienceGrpId, int clientId, int monitorId, String _date, int durationMinute, boolean isDone, int paymentId, String comment) {
        this.scienceId = scienceId;
        this.scienceGrpId = scienceGrpId;
        this.clientId = clientId;
        this.monitorId = monitorId;

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
        this.isDone = isDone;
        this.paymentId = paymentId;
        this.comment = comment;
    }

    public int getScienceId() {
        return scienceId;
    }
    public void setScienceId(int scienceId) {
        this.scienceId = scienceId;
    }
    public int getScienceGrpId() {
        return scienceGrpId;
    }
    public void setScienceGrpId(int scienceGrpId) {
        this.scienceGrpId = scienceGrpId;
    }
    public int getClientId() {
        return clientId;
    }
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }
    public int getMonitorId() {
        return monitorId;
    }
    public void setMonitorId(int monitorId) {
        this.monitorId = monitorId;
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
    public boolean isDone() {
        return isDone;
    }
    public void setDone(boolean done) {
        isDone = done;
    }
    public int getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }
    public String getComment() {
        return comment;
    }
    public void setComment(String comment) {
        this.comment = comment;
    }

    private int scienceId;
    private int scienceGrpId;
    private int clientId;
    private int monitorId;
    private NewDate date;
    private NewTime startTime;
    private int durationMinute;
    private boolean isDone;
    private int paymentId;
    private String comment;

    @Override
    public String toString() {
        return  "sc|"+
                scienceId + "|" +
                scienceGrpId + "|" +
                clientId + "|" +
                monitorId + "|" +
                date + "|" +
                startTime + "|" +
                durationMinute + "|" +
                isDone + "|" +
                paymentId + "|" +
                (comment.trim().isEmpty()?"&":comment);
    }

    @Override
    public String Typeof() {
        return "Science";
    }
}
