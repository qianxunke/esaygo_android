package com.esaygo.app.module.ticket.model;

public class TaskDetails {

  private  Task task;

  private Task_passenger []task_passenger;


    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Task_passenger[] getTask_passenger() {
        return task_passenger;
    }

    public void setTask_passenger(Task_passenger[] task_passenger) {
        this.task_passenger = task_passenger;
    }

    public static class Task {
        String task_id;      //订单ID
        String user_id ;       //用户id
        String seat_types ;     //座位类型 1,2,3
        String train_dates ;    // 乘车日期 格式为2017-01-01,
        String find_from ;     // 查询始发站
        String find_to ;        // 查询终点站
        String ok_no ;          //抢成功时的12306订单号
        int status ;        //状态 -1：失效  1：待抢  2，正在抢  3，抢成功
        int created_time ;
        int update_time;
        String type ;         //ADULT STU
        String trips ;       //车次

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getSeat_types() {
            return seat_types;
        }

        public void setSeat_types(String seat_types) {
            this.seat_types = seat_types;
        }

        public String getTrain_dates() {
            return train_dates;
        }

        public void setTrain_dates(String train_dates) {
            this.train_dates = train_dates;
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

        public String getOk_no() {
            return ok_no;
        }

        public void setOk_no(String ok_no) {
            this.ok_no = ok_no;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getCreated_time() {
            return created_time;
        }

        public void setCreated_time(int created_time) {
            this.created_time = created_time;
        }

        public int getUpdate_time() {
            return update_time;
        }

        public void setUpdate_time(int update_time) {
            this.update_time = update_time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTrips() {
            return trips;
        }

        public void setTrips(String trips) {
            this.trips = trips;
        }
    }

    public static class Task_passenger {
        String id ;
        String task_id ;
        String name ;      // 乘车人用户名
        String id_num ;         // 乘车人身份证
        String tel_num ;     // 语音通知及接收短信手机号
        String type ;   // 身份 成人，学生
        String seat_num ;    // 座号，同12306，不一定可以选到希望的座位
        String allEncStr ;   //

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTask_id() {
            return task_id;
        }

        public void setTask_id(String task_id) {
            this.task_id = task_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getId_num() {
            return id_num;
        }

        public void setId_num(String id_num) {
            this.id_num = id_num;
        }

        public String getTel_num() {
            return tel_num;
        }

        public void setTel_num(String tel_num) {
            this.tel_num = tel_num;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSeat_num() {
            return seat_num;
        }

        public void setSeat_num(String seat_num) {
            this.seat_num = seat_num;
        }

        public String getAllEncStr() {
            return allEncStr;
        }

        public void setAllEncStr(String allEncStr) {
            this.allEncStr = allEncStr;
        }
    }


}