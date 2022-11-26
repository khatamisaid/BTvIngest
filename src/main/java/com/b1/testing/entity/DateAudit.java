package com.b1.testing.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class DateAudit implements Serializable {

    @JsonFormat(pattern="dd-MMMM-yyyy HH:mm:ss", timezone = "GMT+7")
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Date createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = true, updatable = false)
    private Date updatedAt;

    @PrePersist
    protected void prePersist() {
        if (this.createdAt == null) {
            createdAt = new Date();
        }
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = new Date();
    }
}
