package net.odessa.odisw.model;

import com.google.gson.annotations.Expose;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Request {

    private String time;
    private String sn;
    private Coord coord;
    private boolean isSended = false;

    public Request(long time, String sn, String x, String y){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        this.time = formatter.format(new Date(time));
        this.sn = sn;
        coord = new Coord(Double.parseDouble(x), Double.parseDouble(y));
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

    public Coord getCoord() {
        return coord;
    }

    public void setCoord(Coord coord) {
        this.coord = coord;
    }

    public boolean isSended() {
        return isSended;
    }

    public void setSended(boolean sended) {
        isSended = sended;
    }
}
