package com.cosmicexplorer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonRawValue;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * A body in the Solar System (the Sun, the 8 planets and Pluto).
 * Field names match the JSON the React app already uses, so the frontend needs no mapping.
 */
@Entity
@Table(name = "planet")
public class Planet {

    @Id
    private String id;

    private int sortOrder;
    private String name;

    @Column(name = "body_type")
    private String type;

    private String color;

    // values used only by the 3D scene (display size, orbit radius, speeds, tilt, start angle)
    private double r;
    private double a;
    private double w;
    private double spin;
    private double tilt;
    private double phase;

    @Column(length = 1000)
    private String blurb;

    private Integer d;
    private String distM;
    private Double au;
    private String temp;
    private String moons;

    @Column(name = "year_text")
    private String year;

    private String rot;
    private Double g;
    private Double periodD;
    private String hz;

    /** JSON array of [label, value] pairs. Only the Sun uses it. */
    @Column(length = 2000)
    private String stats;

    protected Planet() {
    }

    public String getId() { return id; }
    @JsonIgnore
    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getColor() { return color; }
    public double getR() { return r; }
    public double getA() { return a; }
    public double getW() { return w; }
    public double getSpin() { return spin; }
    public double getTilt() { return tilt; }
    public double getPhase() { return phase; }
    public String getBlurb() { return blurb; }
    public Integer getD() { return d; }
    public String getDistM() { return distM; }
    public Double getAu() { return au; }
    public String getTemp() { return temp; }
    public String getMoons() { return moons; }
    public String getYear() { return year; }
    public String getRot() { return rot; }
    public Double getG() { return g; }
    public Double getPeriodD() { return periodD; }
    public String getHz() { return hz; }

    @JsonRawValue
    public String getStats() { return stats; }
}
