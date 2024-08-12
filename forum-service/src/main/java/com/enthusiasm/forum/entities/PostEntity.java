package com.enthusiasm.forum.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.UUID;

@Entity(name = "post")
public class PostEntity {
    @Id
    @Basic
    @JdbcTypeCode(SqlTypes.UUID)
    private UUID id;
    private String title;
    private String detail;
    private UUID userId;

    private boolean isDeleted;

    public PostEntity() {
    }

    public PostEntity(UUID id, String title, String detail, UUID userId, boolean isDeleted) {
        this.id = id;
        this.title = title;
        this.detail = detail;
        this.userId = userId;
        this.isDeleted = isDeleted;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }
}
