package com.gehj.okhttp_netframe.download.db;

import org.greenrobot.greendao.annotation.*;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT. Enable "keep" sections if you want to edit.

/**
 * Entity mapped to table "DOWN_LOAD_ENTITY".
 */
@Entity
public class DownLoadEntity {
    private Long start_pos;
    private Long end_pos;
    private Long progress_pos;
    private String download_url;
    private Integer thread_id;

    @Id(autoincrement = true)
    private Long id;

    @Generated
    public DownLoadEntity() {
    }

    public DownLoadEntity(Long id) {
        this.id = id;
    }

    @Generated
    public DownLoadEntity(Long start_pos, Long end_pos, Long progress_pos, String download_url, Integer thread_id, Long id) {
        this.start_pos = start_pos;
        this.end_pos = end_pos;
        this.progress_pos = progress_pos;
        this.download_url = download_url;
        this.thread_id = thread_id;
        this.id = id;
    }

    public Long getStart_pos() {
        return start_pos;
    }

    public void setStart_pos(Long start_pos) {
        this.start_pos = start_pos;
    }

    public Long getEnd_pos() {
        return end_pos;
    }

    public void setEnd_pos(Long end_pos) {
        this.end_pos = end_pos;
    }

    public Long getProgress_pos() {
        return progress_pos;
    }

    public void setProgress_pos(Long progress_pos) {
        this.progress_pos = progress_pos;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public Integer getThread_id() {
        return thread_id;
    }

    public void setThread_id(Integer thread_id) {
        this.thread_id = thread_id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

}
