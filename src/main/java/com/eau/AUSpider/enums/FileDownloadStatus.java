package com.eau.AUSpider.enums;

public enum FileDownloadStatus {
    CANNOT_BE_SCRAPED("CANNOT_BE_SCRAPED"),
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
