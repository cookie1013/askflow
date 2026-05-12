package com.cookie.askflowbackend.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "kb_space")
public class KbSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 知识库名称，例如：短链平台运维知识库
     */
    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * 知识库描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 状态：1 正常，0 禁用
     */
    @Column(name = "status", nullable = false)
    private Integer status;

    /**
     * 当前知识库下的文档数量，后面做文档模块时会更新
     */
    @Column(name = "document_count", nullable = false)
    private Integer documentCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public KbSpace() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = 1;
        }

        if (this.documentCount == null) {
            this.documentCount = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public Integer getDocumentCount() {
        return documentCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}