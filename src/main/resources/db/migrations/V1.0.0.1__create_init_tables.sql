CREATE TABLE file (
    id                          serial primary key,
    name                        text,
    download_status             text not null,
    url                         text not null,
    file_location               text unique not null,
    create_timestamp_utc        timestamptz not null,
    update_timestamp_utc        timestamptz not null
);

CREATE TABLE scraping_site (

);