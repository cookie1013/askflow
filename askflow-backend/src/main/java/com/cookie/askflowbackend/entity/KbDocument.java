package com.cookie.askflowbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kb_document")
public class KbDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属知识库 ID
     */
    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    /**
     * 文档标题
     */
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    /**
     * 文档类型，例如 markdown、pdf、txt、docx
     */
    @Column(name = "document_type", nullable = false, length = 50)
    private String documentType;

    /**
     * 原始文件名
     */
    @Column(name = "original_filename", length = 255)
    private String originalFilename;

    /**
     * 文件存储路径，当前阶段先手动填写，后面文件上传时自动生成
     */
    @Column(name = "storage_path", length = 500)
    private String storagePath;

    /**
     * 解析状态：PENDING、PARSING、PARSED、FAILED
     */
    @Column(name = "parse_status", nullable = false, length = 30)
    private String parseStatus;

    /**
     * 文档切片数量，后面做 chunk 模块时会更新
     */
    @Column(name = "chunk_count", nullable = false)
    private Integer chunkCount;

    /**
     * 状态：1 正常，0 删除或禁用
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public KbDocument() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.parseStatus == null) {
            this.parseStatus = "PENDING";
        }

        if (this.chunkCount == null) {
            this.chunkCount = 0;
        }

        if (this.status == null) {
            this.status = 1;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(Long spaceId) {
        this.spaceId = spaceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getParseStatus() {
        return parseStatus;
    }

    public void setParseStatus(String parseStatus) {
        this.parseStatus = parseStatus;
    }

    public Integer getChunkCount() {
        return chunkCount;
    }

    public void setChunkCount(Integer chunkCount) {
        this.chunkCount = chunkCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}