package com.phage.ex_sepim.entity;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class Message {

    private Integer id;

    private Integer fromId;

    private Integer toId;

    private String content;

    private Timestamp date;

    private Integer type;

    private Integer isLatest;

    private User user;

    private Integer noReadNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFromId() {
        return fromId;
    }

    public void setFromId(Integer fromId) {
        this.fromId = fromId;
    }

    public Integer getToId() {
        return toId;
    }

    public void setToId(Integer toId) {
        this.toId = toId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getIsLatest() {
        return isLatest;
    }

    public void setIsLatest(Integer isLatest) {
        this.isLatest = isLatest;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getNoReadNum() {
        return noReadNum;
    }

    public void setNoReadNum(Integer noReadNum) {
        this.noReadNum = noReadNum;
    }
}