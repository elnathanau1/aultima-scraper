package com.eau.AUSpider.enums;

public enum FileDownloadStatus {
    CANNOT_BE_SCRAPED("CANNOT_BE_SCRAPED"),
    SEND_TO_PI("SEND_TO_PI"),
    NOT_STARTED("NOT_STARTED"),
    DOWNLOADING("DOWNLOADING"),
    DOWNLOADED("DOWNLOADED"),
    TRANSFERRING("TRANSFERRING"),
    COMPLETE("COMPLETE");

    public final String state;

    FileDownloadStatus(String state) {
        this.state = state;
    }
}
