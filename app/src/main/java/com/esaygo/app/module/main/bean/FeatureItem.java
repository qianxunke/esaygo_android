package com.esaygo.app.module.main.bean;

public class FeatureItem {


    /**
     * id : 1
     * code : 01                     17          18
     * title : 待确认               取货入库     取货情况
     * detail :
     * sort : 1                       5           6
     * logo_title : w_confirmed    w_ramasser  ramasser_situation
     * task_count : 22849
     */

    private int id;
    private String code;
    private String title;
    private String detail;
    private int sort;
    private String logo_title;
    private int task_count;

    public FeatureItem(String code, String title, String logo_title, int task_count) {
        this.code = code;
        this.title = title;
        this.logo_title = logo_title;
        this.task_count = task_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getLogo_title() {
        return logo_title;
    }

    public void setLogo_title(String logo_title) {
        this.logo_title = logo_title;
    }

    public int getTask_count() {
        return task_count;
    }

    public void setTask_count(int task_count) {
        this.task_count = task_count;
    }
}
