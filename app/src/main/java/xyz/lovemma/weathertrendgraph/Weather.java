package xyz.lovemma.weathertrendgraph;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by OO on 2017/4/18.
 */

public class Weather {
    private int highTmp;    //高温
    private int lowTmp;     //低温
    private String date;    //日期
    private String cond;    //天气状况

    public Weather(int highTmp, int lowTmp) {
        this.highTmp = highTmp;
        this.lowTmp = lowTmp;
    }

    public Weather(int highTmp, int lowTmp, String date) {
        this.highTmp = highTmp;
        this.lowTmp = lowTmp;
        this.date = date;
    }

    public Weather(int highTmp, int lowTmp, String date, String cond) {
        this.highTmp = highTmp;
        this.lowTmp = lowTmp;
        this.date = date;
        this.cond = cond;
    }

    public int getHighTmp() {
        return highTmp;
    }

    public void setHighTmp(int highTmp) {
        this.highTmp = highTmp;
    }

    public int getLowTmp() {
        return lowTmp;
    }

    public void setLowTmp(int lowTmp) {
        this.lowTmp = lowTmp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCond() {
        return cond;
    }

    public void setCond(String cond) {
        this.cond = cond;
    }

    public String getWeek() {
        String[] weeks = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(this.date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            int week_index = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            if (week_index < 0) {
                week_index = 0;
            }
            return weeks[week_index];
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return this.date;
    }
}
