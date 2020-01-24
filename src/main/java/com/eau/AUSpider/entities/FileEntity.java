package com.eau.AUSpider.entities;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.Instant;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder(toBuilder = true)
@With
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "file")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String mediaType;
    private String sortingFolder;
    private String downloadStatus;
    private String url;
    private String fileLocation;

    private String fileSize;

    @CreatedDate
    private Instant createTimestampUtc;

    @LastModifiedDate
    private Instant updateTimestampUtc;

    private Integer season;
    private Integer episode;
    private Integer priority;

}