package com.phage.ex_sepim.entity;

public class SqlMessage {
    private Integer id;

    private Integer l_account;

    private Integer u_id;

    private String username;

    private String avatar;

    private String content;

    private String date;

    private Integer noRead;

    public Integer getL_account() {
        return l_account;
    }

    public void setL_account(Integer l_account) {
        this.l_account = l_account;
    }

    public Integer getU_id() {
        return u_id;
    }

    public void setU_id(Integer u_id) {
        this.u_id = u_id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getNoRead() {
        return noRead;
    }

    public void setNoRead(Integer noRead) {
        this.noRead = noRead;
    }
}
