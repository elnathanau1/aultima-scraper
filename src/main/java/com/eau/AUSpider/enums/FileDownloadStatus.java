package com.eau.AUSpider.enums;

public enum FileDownloadStatus {
    NOT_STARTED("NOT_STARTED"),
    DOWNLOADING("DOWNLOADING"),
    COMPLETE("COMPLETE");

    public final String state;

    FileDownloadStatus(String state) {
        this.state = state;
    }
}
