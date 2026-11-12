package com.cosmicexplorer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/** A planet a user starred. */
@Entity
@Table(name = "favourite", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "body_id"}))
public class Favourite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "body_id", nullable = false)
    private String bodyId;

    protected Favourite() {
    }

    public Favourite(Long userId, String bodyId) {
        this.userId = userId;
        this.bodyId = bodyId;
    }

    public Long getUserId() { return userId; }
    public String getBodyId() { return bodyId; }
}
