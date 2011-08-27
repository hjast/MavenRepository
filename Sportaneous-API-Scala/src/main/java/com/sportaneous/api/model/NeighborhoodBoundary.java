//
// NeighborhoodBoundary.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;

/**
 * NeighborhoodBoundary
 */
@Entity
@Table(name = "neighborhood_boundaries")
public class NeighborhoodBoundary implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "boundary_id")
    private Long boundaryID;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    @Column(name = "idx", nullable = false)
    private Integer idx;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    public Long getBoundaryID()
    {
        return boundaryID;
    }

    public void setBoundaryID(Long boundaryID)
    {
        this.boundaryID = boundaryID;
    }

    public Neighborhood getNeighborhood()
    {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood)
    {
        this.neighborhood = neighborhood;
    }

    public Integer getIdx()
    {
        return idx;
    }

    public void setIdx(Integer idx)
    {
        this.idx = idx;
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

}
