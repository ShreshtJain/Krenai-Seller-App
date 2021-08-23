package com.krenai.vendor.model;


import java.util.List;

public class ResponseEntityList<T> {

    private boolean status;
    private String message;
    private List<T> object;
    private Integer noOfPage[];
    private String action;
    private Integer totalItems;

    public Integer getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(Integer totalItems) {
        this.totalItems = totalItems;
    }

    public ResponseEntityList() {
    }

    public ResponseEntityList(boolean status, String message, List<T> object) {
        this.status = status;
        this.message = message;
        this.object = object;
    }
    public ResponseEntityList(boolean status, String message, List<T> object, Integer[] noOfPage, int totalItems) {
        super();
        this.status = status;
        this.message = message;
        this.object = object;
        this.noOfPage = noOfPage;
        this.totalItems=totalItems;
    }

    public ResponseEntityList(boolean status, String message, List<T> object, Integer[] noOfPage) {
        super();
        this.status = status;
        this.message = message;
        this.object = object;
        this.noOfPage = noOfPage;
        this.totalItems=totalItems;
    }


    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<T> getObject() {
        return object;
    }

    public void setObject(List<T> object) {
        this.object = object;
    }

    public Integer[] getNoOfPage() {
        return noOfPage;
    }

    public void setNoOfPage(Integer[] noOfPage) {
        this.noOfPage = noOfPage;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}