package org.test.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class LineStatistics {
    @Id
    @Column(name = "file_id")
    private Integer fileId;

    @Id
    @Column(name = "line_pos_indicator")
    private Long linePositionIndicator;

    @Column(name = "line_src")
    private String lineSrc;
}
