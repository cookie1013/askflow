package com.cookie.askflowbackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AiContextChunk {

    @JsonProperty("chunk_id")
    private String chunkId;

    @JsonProperty("document_name")
    private String documentName;

    @JsonProperty("page_no")
    private Integer pageNo;

    @JsonProperty("chunk_type")
    private String chunkType;

    @JsonProperty("section_title")
    private String sectionTitle;
    private String content;

    private Double score;

    public AiContextChunk() {
    }

    public AiContextChunk(String chunkId, String documentName, String content) {
        this.chunkId = chunkId;
        this.documentName = documentName;
        this.content = content;
    }

    public AiContextChunk(String chunkId, String documentName, String content, Double score) {
        this.chunkId = chunkId;
        this.documentName = documentName;
        this.content = content;
        this.score = score;
    }
    public AiContextChunk(String chunkId,
                          String documentName,
                          String content,
                          Double score,
                          Integer pageNo,
                          String chunkType,
                          String sectionTitle) {
        this.chunkId = chunkId;
        this.documentName = documentName;
        this.content = content;
        this.score = score;
        this.pageNo = pageNo;
        this.chunkType = chunkType;
        this.sectionTitle = sectionTitle;
    }

    @JsonProperty("chunk_id")
    public String getChunkId() {
        return chunkId;
    }

    @JsonProperty("document_name")
    public String getDocumentName() {
        return documentName;
    }

    public String getContent() {
        return content;
    }

    public Double getScore() {
        return score;
    }

    public Integer getPageNo() {
        return pageNo;
    }



    public String getChunkType() {
        return chunkType;
    }


    public String getSectionTitle() {
        return sectionTitle;
    }


}