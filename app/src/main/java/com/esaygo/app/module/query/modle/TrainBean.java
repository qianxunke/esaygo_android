package com.esaygo.app.module.query.modle;

import java.io.Serializable;

public class TrainBean implements Serializable {
/*
    {
        "secret_str": "RinL%2F5YUQLdTECtSOV%2FtCTyRNd4eoRzzFud%2FrEPt8ZnvXk5EuheETb0Ti6EwBlhxF8wjHn5CC7A1%0A%2FwpV12aJgXuDDA1TLywDtbTKIACTdiiZGlUgftbWJZI5B%2B%2B%2BeITeBGwkJsR1tjyNIYwLUCxj1bPj%0AlSPbSI460%2BK8D3yZ15VJ%2FdA%2BDXNo9monHFQUGHNsrbiI6rV1dkmxuuUvL3pzhji2DPQKSYcZF9Cj%0AttHMQsBr0Y0Mu6qkw6%2FI594S20OECYmzoXTF41%2B%2Ba77O7LCVnXYmcPDvbo%2FhQtWeyfiNeN%2BX2wUp%0AWse7lg%3D%3D",
            "train_code": "240000G1512P",
            "num": "G151",
            "from": "VNP",
            "to": "AOH",
            "find_from": "VNP",
            "find_to": "AOH",
            "start_time": "16:45",
            "end_time": "23:02",
            "cost_time": "06:17",
            "can_buy": "Y",
            "train_date": "20191219",
            "swtdz": "有",
            "ydz": "有",
            "edz": "有",
            "bz": "预订"
    }
string	ydz  =14 ;    //31
string	edz  =15 ;   //30
string	gjrw =16;    //21
string	rw  =17;   //23
string	dw  =18;   //33
string	yw  =19;  //28
string	rz  =20;   //24
string	yz  =21;  //29
string	wz  =22; //26
string	qt   =23;  //22
string	bz  =24;  //1
 */


    private String secret_str;
    private String train_code;
    private String num;
    private String from;
    private String to;
    private String find_from;
    private String find_to;
    private String start_time;
    private String end_time;
    private String cost_time;
    private String can_buy;
    private String train_date;
    private String swtdz;
    private String ydz;
    private String bz;
    private String rw; //软卧
    private String yw;//硬卧
    private String yz; //硬座
    private String wz; //无座
    private String rz;
    private String dw;
    private String gjrw;
    private String edz;
    private boolean isSelect;

    public String getSecret_str() {
        return secret_str;
    }

    public void setSecret_str(String secret_str) {
        this.secret_str = secret_str;
    }

    public String getTrain_code() {
        return train_code;
    }

    public void setTrain_code(String train_code) {
        this.train_code = train_code;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFind_from() {
        return find_from;
    }

    public void setFind_from(String find_from) {
        this.find_from = find_from;
    }

    public String getFind_to() {
        return find_to;
    }

    public void setFind_to(String find_to) {
        this.find_to = find_to;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getCost_time() {
        return cost_time;
    }

    public void setCost_time(String cost_time) {
        this.cost_time = cost_time;
    }

    public String getCan_buy() {
        return can_buy;
    }

    public void setCan_buy(String can_buy) {
        this.can_buy = can_buy;
    }

    public String getTrain_date() {
        return train_date;
    }

    public void setTrain_date(String train_date) {
        this.train_date = train_date;
    }

    public String getSwtdz() {
        return swtdz;
    }

    public void setSwtdz(String swtdz) {
        this.swtdz = swtdz;
    }

    public String getYdz() {
        return ydz;
    }

    public void setYdz(String ydz) {
        this.ydz = ydz;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getRw() {
        return rw;
    }

    public void setRw(String rw) {
        this.rw = rw;
    }

    public String getYw() {
        return yw;
    }

    public void setYw(String yw) {
        this.yw = yw;
    }

    public String getYz() {
        return yz;
    }

    public void setYz(String yz) {
        this.yz = yz;
    }

    public String getWz() {
        return wz;
    }

    public void setWz(String wz) {
        this.wz = wz;
    }

    public String getRz() {
        return rz;
    }

    public void setRz(String rz) {
        this.rz = rz;
    }

    public String getDw() {
        return dw;
    }

    public void setDw(String dw) {
        this.dw = dw;
    }

    public String getGjrw() {
        return gjrw;
    }

    public void setGjrw(String gjrw) {
        this.gjrw = gjrw;
    }

    public String getEdz() {
        return edz;
    }

    public void setEdz(String edz) {
        this.edz = edz;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
