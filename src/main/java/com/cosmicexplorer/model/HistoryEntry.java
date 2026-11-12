package com.cosmicexplorer.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/** One line of a user's activity: a planet opened, a search, or a question asked. */
@Entity
@Table(name = "history_entry", indexes = @Index(name = "idx_history_user", columnList = "user_id"))
public class HistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(length = 40, nullable = false)
    private String kind;

    @Column(name = "entry_text", length = 300, nullable = false)
    private String text;

    /** time in epoch milliseconds, the same unit the React app uses */
    @Column(name = "created_ms", nullable = false)
    private long createdMs;

    protected HistoryEntry() {
    }

    public HistoryEntry(Long userId, String kind, String text, long createdMs) {
        this.userId = userId;
        this.kind = kind;
        this.text = text;
        this.createdMs = createdMs;
    }

    public Long getUserId() { return userId; }
    public String getKind() { return kind; }
    public String getText() { return text; }
    public long getCreatedMs() { return createdMs; }
}
