package com.firstappjava.client;

import android.annotation.SuppressLint;
import android.widget.TextView;

import java.util.Date;
import java.util.Objects;

/*
  1 ==> Jan
  2 ==> Feb
  3 ==> Mar
  4 ==> Apr
  5 ==> May
  6 ==> Jun
  7 ==> Jul
  8 ==> Aug
  9 ==> Sep
  10 ==> Oct
  11 ==> Nov
  12 ==> Dec
*/
public class NewDate {
    public NewDate(TextView MonthAndDay, TextView _year) {
        Date d=new Date();
        String []s=d.toString().split(" ");
        day=Integer.parseInt( s[2] );
        month=MonthToNumber(s[1]);
        year=Integer.parseInt(s[5]);

        this.MonthAndDay=MonthAndDay;
        this._year=_year;
    }
    public NewDate(int day, int month, int year) {
        this.day=day;
        this.month=month;
        this.year=year;
        this.MonthAndDay=this._year=null;
    }
    public NewDate(NewDate nd) {
        this.day=nd.day;
        this.month=nd.month;
        this.year=nd.year;
        this.MonthAndDay=this._year=null;
    }
    public void setDate(int day, int month, int year) {
        this.day=day;
        this.month=month;
        this.year=year;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NewDate)) return false;
        NewDate newDate = (NewDate) o;
        return day == newDate.day &&
                month == newDate.month &&
                year == newDate.year;
    }

    @Override
    public int hashCode() {
        return Objects.hash(day, month, year);
    }

    @SuppressLint("SetTextI18n")
    public void UpdateTextView() {
        this.MonthAndDay.setText( this.getMonth()+" "+this.getDay() );
        this._year.setText( this.getYear() );
    }
    public NewDate WhatDateWillGonnaBeAfterGo(int n) {
        NewDate nd=new NewDate(this);
        if(n>0) {
            nd.goForward(n);
        }else if(n<0) {
            nd.goBackWard(Math.abs(n));
        }
        return nd;
    }
    public static int NumberOfDayInMonth(int month, int year) {
        if(month<1 || month>12) {
            return -1;
        }
        if(month==2) {
            return 28+(LeapYear(year)?1:0);
        }
        if(month==1 || month==3 || month==5 || month==7 || month==8 || month==10 || month==12) {
            return 31;
        }
        return 30;
    }
    public void go(int n) {
        if(n==0) {
            return;
        }
        if(n>0) {
            this.goForward(n);
            return;
        }
        this.goBackWard(Math.abs(n));
    }
    public void goForward(int n) {
        while(n-->0) {
            this.goForwardByOne();
        }
    }
    public void goBackWard(int n) {
        while(n-->0) {
            this.goBackwardByOne();
        }
    }
    public void goForwardByOne() {
        int n=NumberOfDayInMonth(month, year);
        if(day+1>n) {
            day=1;
            if(month==12) {
                month=1;
                ++year;
            }else {
                ++month;
            }
        }else {
            ++day;
        }
    }
    public void goBackwardByOne() {
        if(day==1) {
            if(month==1) {
                month=12;
                --year;
            }else {
                --month;
            }
            day=NumberOfDayInMonth(month, year);
        }else {
            --day;
        }
    }
    private static String FixString(String s) {
        return s.length()<3?s:s.charAt(0)+s.substring(1).toLowerCase();
    }
    private static boolean LeapYear(int year) {
        return (year%400==0)||((year%4==0)&&(year%100!=0));
    }
    private static String NumberToDay(int i) {
        if(i<1 || i>7) {
            return "";
        }
        return FixString(days[i-1]);
    }
    private static int DayToNumber(String Day) {
        Day=Day.toUpperCase();
        for(int i=0; i<7; ++i) {
            if(Day.equals(days[i])) {
                return i+1;
            }
        }
        return -1;
    }
    public static String NumberToMonth(int i) {
        if(i<1 || i>12) {
            return "";
        }
        return FixString(months[i-1]);
    }
    public static int MonthToNumber(String Month) {
        Month=Month.toUpperCase();
        for(int i=0; i<12; ++i) {
            if(Month.equals(months[i])) {
                return i+1;
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        /*return "NewDate{" +
                "day=" + day +
                ", month=" + month +
                ", year=" + year +
                '}';*/
        return this.year+"-"+
               (this.month<10?"0"+this.month:this.month)+"-"+
               (this.day<10?"0"+this.day:this.day);
    }

    public NewDate clone() {
        return new NewDate(this);
    }

    private final static String []months={ "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" };
    private final static String []days={ "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };
    public String getDay() {
        return (day<10?"0":"")+String.valueOf(day);
    }
    public String getMonth() {
        return NumberToMonth(month);
    }
    public int getMonthByNumberInteger() {
        return month;
    }
    public String getMonthByNumberString() {
        return (month<10?"0":"")+month;
    }
    public String getYear() {
        return String.valueOf(year);
    }
    public int getDayByNumber() {
        Date d=new Date(month+"/"+day+"/"+year);
        return DayToNumber(d.toString().split(" ")[0]);
    }
    public int getValueDay() {
        return this.day;
    }
    public int getValueMonth() {
        return this.month;
    }
    public int getValueYear() {
        return this.year;
    }
    private int day, month, year;
    private final TextView MonthAndDay;
    private final TextView _year;
}
