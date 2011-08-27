//
// Region.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.TimeZone;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: Region.findAll
    //
    // Parameters: <none>
    //
    @NamedQuery(
        name = "Region.findAll",
        query = "SELECT region FROM Region region"
    )
})

/**
 * Region
 */
@Entity
@Table(name = "regions")
public class Region implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "region_id")
    private Long regionID;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "time_zone_name", nullable = false)
    private String timeZoneName;

    @OneToMany(mappedBy = "region")
    private List<Neighborhood> neighborhoods;

    @Column(name = "lat")
    private Double lat;

    @Column(name = "lng")
    private Double lng;

    @Column(name = "radius")
    private Double radius;

    @OneToMany(mappedBy = "region")
    private List<RegionSport> sports;

    public Long getRegionID()
    {
        return regionID;
    }

    public void setRegionID(Long regionID)
    {
        this.regionID = regionID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getTimeZoneName()
    {
        return timeZoneName;
    }

    public void setTimeZoneName(String timeZoneName)
    {
        this.timeZoneName = timeZoneName;
    }

    public List<Neighborhood> getNeighborhoods()
    {
        return neighborhoods;
    }

    public void setNeighborhoods(List<Neighborhood> neighborhoods)
    {
        this.neighborhoods = neighborhoods;
    }

    public Double getLat()
    {
        return lat;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public Double getLng()
    {
        return lng;
    }

    public void setLng(Double lng)
    {
        this.lng = lng;
    }

    public Double getRadius()
    {
        return radius;
    }

    public void setRadius(Double radius)
    {
        this.radius = radius;
    }

    @Transient
    public TimeZone getTimeZone()
    {
        return TimeZone.getTimeZone(timeZoneName);
    }

    public List<RegionSport> getSports()
    {
        return sports;
    }

    public void setSports(List<RegionSport> sports)
    {
        this.sports = sports;
    }

    public String toString()
    {
        return name;
    }
}
