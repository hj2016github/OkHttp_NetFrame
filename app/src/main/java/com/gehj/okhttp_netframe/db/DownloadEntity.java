package com.gehj.okhttp_netframe.db;

import org.litepal.crud.LitePalSupport;

public class DownloadEntity extends LitePalSupport {
    private long id;
    private Long start_pos;
    private Long end_pos;
    private Long progress_pos;
    private String download_url;
    private boolean isCancel;
    private boolean isSuccess;
    public long getId() {
        return id;
    }

    public void setId(int id) {
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

    public boolean isCancel() {
        return isCancel;
    }

    public void setCancel(boolean cancel) {
        isCancel = cancel;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }


}
