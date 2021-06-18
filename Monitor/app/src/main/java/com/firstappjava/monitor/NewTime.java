package com.firstappjava.monitor;

import java.util.Objects;

public class NewTime {
    public NewTime(int hour, int minute, int second) {
        this.hour=hour;
        this.minute=minute;
        this.second=second;
    }
    public NewTime(NewTime nt) {
        this.hour=nt.hour;
        this.minute=nt.minute;
        this.second=nt.second;
    }
    public NewTime Add(int minute) {
        NewTime nt=new NewTime(this);
        nt.minute+=minute;
        if(nt.minute>59) {
            nt.hour+=nt.minute/60;
            nt.minute%=60;
        }
        if(nt.hour>23) {
            nt.hour=0;
        }
        return nt;
    }
    public NewTime clone() {
        return new NewTime(this.hour, this.minute, this.second);
    }
    public String print() {
        return this.hour+":"+this.minute;
    }
    public int getHour() {
        return hour;
    }
    public void setHour(int hour) {
        this.hour = hour;
    }
    public int getMinute() {
        return minute;
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }
    public int getSecond() {
        return second;
    }
    public void setSecond(int second) {
        this.second = second;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewTime)) return false;
        NewTime newTime = (NewTime) o;
        return hour == newTime.hour &&
                minute == newTime.minute &&
                second == newTime.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hour, minute, second);
    }

    @Override
    public String toString() {
        /*return "NewTime{" +
                "hour=" + hour +
                ", minute=" + minute +
                ", second=" + second +
                '}';*/
        return (this.hour<10?"0"+this.hour:this.hour)+":"+
               (this.minute<10?"0"+this.minute:this.minute);
    }
    private int hour;
    private int minute;
    private int second;
}
