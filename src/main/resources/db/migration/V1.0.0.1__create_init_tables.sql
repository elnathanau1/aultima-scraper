CREATE TABLE file (
    id                          serial primary key,
    name                        text,
    media_type                  text,
    sorting_folder              text,
    download_status             text not null,
    url                         text not null,
    file_location               text,
    file_size                   text,
    create_timestamp_utc        timestamptz not null,
    update_timestamp_utc        timestamptz not null
);

CREATE TABLE source (
    id                          serial primary key,
    name                        text,
    url                         text,
    episode                     integer,
    create_timestamp_utc        timestamptz not null,
    update_timestamp_utc        timestamptz not null
);