package org.test.model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "files")
public class FileStatistics {
    @Id
    @Column(name = "file_id")
    private Integer fileId;

    @Column(name = "file_name")
    private String fileName;

    @OneToMany(mappedBy = "fileId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LineStatistics> lineStatistics;

    public Integer getFileId() {
        return fileId;
    }

    public void setFileId(Integer fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public List<LineStatistics> getLineStatistics() {
        return lineStatistics;
    }

    public void setLineStatistics(List<LineStatistics> lineStatistics) {
        this.lineStatistics = lineStatistics;
    }
}
