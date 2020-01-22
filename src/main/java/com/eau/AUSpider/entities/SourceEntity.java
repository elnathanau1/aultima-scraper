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
@Table(name = "source")
public class SourceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private Integer episode;

    @CreatedDate
    private Instant createTimestampUtc;

    @LastModifiedDate
    private Instant updateTimestampUtc;
}
