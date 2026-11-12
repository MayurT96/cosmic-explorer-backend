package com.cosmicexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A space mission shown on the Missions page. JSON keys (y, n, by, t, body, plan) match the React app. */
@Entity
@Table(name = "mission")
public class Mission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int sortOrder;

    /** year */
    @Column(name = "year_num")
    private int y;

    /** name */
    @Column(name = "title")
    private String n;

    /** "India" or "World" */
    @Column(name = "origin")
    private String by;

    /** short description */
    @Column(name = "summary", length = 800)
    private String t;

    /** id of a planet to open in 3D, optional */
    @Column(name = "body_id")
    private String body;

    /** true for a planned mission */
    @Column(name = "planned")
    private Boolean plan;

    protected Mission() {
    }

    @JsonIgnore
    public Long getId() { return id; }
    @JsonIgnore
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public int getY() { return y; }
    public String getN() { return n; }
    public String getBy() { return by; }
    public String getT() { return t; }
    public String getBody() { return body; }
    public Boolean getPlan() { return plan; }
}
