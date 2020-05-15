package com.willkong.networkdemo.dragger2;

public class AppLoginReq {
    /**
     * 用户账号
     */

    private String userName;
    /**
     * 用户密码
     */

    private String pwd;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
