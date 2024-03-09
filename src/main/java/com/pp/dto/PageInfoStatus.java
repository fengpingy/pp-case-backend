package com.pp.dto;

import com.pp.dto.response.page.PageResponse;
import lombok.Data;

import java.util.List;

@Data
public class PageInfoStatus<T> extends PageResponse {
    private int completeCount;
    private int noCompleteCount;
    private int ingCount;
    private int noCaseCount;

    private int notStartCount;
    //计划未关联case
    private List<T> noCaseList;
    //按时完成
    private List<T> completeList;

    //未按时完成
    private List<T> noCompleteList;
    //进行中
    private List<T> ingList;

    //未开始
    private List<T> notStartList;
    public int getIngCount() {
        return ingCount;
    }

    public void setIngCount(int ingCount) {
        this.ingCount = ingCount;
    }

    public int getNotStartCount() {
        return notStartCount;
    }

    public void setNotStartCount(int notStartCount) {
        this.notStartCount = notStartCount;
    }

    public List<T> getIngList() {
        return ingList;
    }

    public void setIngList(List<T> ingList) {
        this.ingList = ingList;
    }

    public List<T> getNotStartList() {
        return notStartList;
    }

    public void setNotStartList(List<T> notStartList) {
        this.notStartList = notStartList;
    }


    public void setCompleteCount(int completeCount) {
        this.completeCount = completeCount;
    }

    public void setNoCompleteCount(int noCompleteCount) {
        this.noCompleteCount = noCompleteCount;
    }

    public void setCompleteList(List<T> list) {
        this.completeList = list;
    }

    public List<T> getCompleteList() {
        return this.completeList;
    }

    public List<T> getNoCompleteList() {
        return this.noCompleteList;
    }


    public void setNoCompleteList(List<T> list) {
        this.noCompleteList = list;
    }

    public int getCompleteCount() {
        return completeCount;
    }

    public int getNoCompleteCount() {
        return noCompleteCount;
    }

    public int getNoCaseCount() {
        return noCaseCount;
    }

    public void setNoCaseCount(int noCaseCount) {
        this.noCaseCount = noCaseCount;
    }

    public List<T> getNoCaseList() {
        return noCaseList;
    }

    public void setNoCaseList(List<T> noCaseList) {
        this.noCaseList = noCaseList;
    }
}
