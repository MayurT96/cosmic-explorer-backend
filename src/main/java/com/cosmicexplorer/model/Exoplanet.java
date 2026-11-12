package com.cosmicexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A confirmed planet that orbits another star. */
@Entity
@Table(name = "exoplanet")
public class Exoplanet {

    @Id
    private String id;

    private int sortOrder;
    private String name;
    private String host;
    private String star;

    @Column(name = "planet_type")
    private String type;

    /** radius in Earth radii, null when not measured */
    private Double radiusE;
    private Double periodD;
    private Double au;
    private Double distLy;
    private String hz;

    @Column(name = "discovery_year")
    private Integer year;

    protected Exoplanet() {
    }

    public String getId() { return id; }
    @JsonIgnore
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getName() { return name; }
    public String getHost() { return host; }
    public String getStar() { return star; }
    public String getType() { return type; }
    public Double getRadiusE() { return radiusE; }
    public Double getPeriodD() { return periodD; }
    public Double getAu() { return au; }
    public Double getDistLy() { return distLy; }
    public String getHz() { return hz; }
    public Integer getYear() { return year; }
}
