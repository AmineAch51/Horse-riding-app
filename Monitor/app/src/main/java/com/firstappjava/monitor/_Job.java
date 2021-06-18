package com.firstappjava.monitor;

public class _Job {
    _Job(Job jb) {
        if(jb.Typeof().equals("Task")) {
            ts=(Task)jb;
        }else {
            sc=(Science)jb;
        }
    }
    public Job getVal() {
        if(sc==null && ts==null) {
            return null;
        }
        if(sc==null) {
            return ts;
        }
        return sc;
    }
    private Science sc=null;
    private Task ts=null;
}
