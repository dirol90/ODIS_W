package net.odessa.odisw.model;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {

    private String time;
    private String sn;
    private String[] coord = new String[2];
    private boolean isSended = false;

    public Request(long time, String sn, String x, String y){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.time = formatter.format(new Date(time));
        this.sn = sn;
        coord[0] = "X="+x.replace(".", ",");
        coord[1] = "Y="+y.replace(".", ",");
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String[] getCoord() {
        return coord;
    }

    public void setCoord(String[] coord) {
        this.coord = coord;
    }

    public boolean isSended() {
        return isSended;
    }

    public void setSended(boolean sended) {
        isSended = sended;
    }
}
