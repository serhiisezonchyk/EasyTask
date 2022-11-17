package com.example.taskorg.Utils;

import com.google.firebase.Timestamp;

import org.checkerframework.checker.units.qual.A;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil {

    public static int getPercentTimeLeft(String startDate, String startTime, String endDate, String endTime) {
        if(endDate.isEmpty()&&endTime.isEmpty()){
            return 8;
        }else {

            int[] startD = dateSplitUp(startDate,true);
            int[] startT = dateSplitUp(startTime,false);
            int[] endD = dateSplitUp(endDate,true);
            int[] endT = dateSplitUp(endTime,false);

            Calendar createDate = new GregorianCalendar(startD[2], startD[1], startD[0], startT[0], startT[1]);
            Calendar expirationDate = new GregorianCalendar(endD[2], endD[1], endD[0], endT[0], endT[1]);

            Date date = new Date();
            SimpleDateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat formatterTime = new SimpleDateFormat("HH:mm");
            int[] currD = dateSplitUp(formatterDate.format(date),true);
            int[] currT = dateSplitUp(formatterTime.format(date),false);

            Calendar currentDate = new GregorianCalendar(currD[2], currD[1], currD[0], currT[0], currT[1]);
            long differenceOne = expirationDate.getTimeInMillis() - createDate.getTimeInMillis();
            long differenceTwo = currentDate.getTimeInMillis() - createDate.getTimeInMillis();

            float res = ((float)differenceTwo / differenceOne) * 100;
            return (int)res<0?100:(int)res;
        }
    }

    public static Date getDateFromStrings(String date, String time){
        if(!date.isEmpty()){
        int[] D = dateSplitUp(date,true);
        int[] T = dateSplitUp(time,false);
        Calendar calendarDate = new GregorianCalendar(D[2], D[1]-1, D[0], T[0], T[1]);
        return calendarDate.getTime();
        }
        else return null;
    }

    public static int[] dateSplitUp(String date, boolean isDate){
        int[] intArr;
        int pos = 0;
        if(isDate){
            intArr = new int[3];
            String[] dateArrStr = date.split("/");
            for (String str: dateArrStr) {
                intArr[pos] = Integer.parseInt(str);
                pos++;
            }
        }else{
            intArr = new int[2];
            String[] dateArrStr = date.split(":");
            for (String str: dateArrStr) {
                intArr[pos] = Integer.parseInt(str);
                pos++;
            }
        }
        return intArr;
    }

    public static ArrayList<Timestamp> listDateToTimestamp(ArrayList<Date> input){
        ArrayList<Timestamp> output = new ArrayList<>();
        for (Date date: input) {
            output.add(new Timestamp(date));
        }
        return  output;
    }
}