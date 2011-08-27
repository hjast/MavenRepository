//
// SuperNeighborhood.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: SuperNeighborhood.findByRegion
    //
    // Parameters:
    //   - region
    //
    @NamedQuery(
        name = "SuperNeighborhood.findByRegion",
        query = "SELECT sn FROM SuperNeighborhood sn " +
                "WHERE sn.region=:region " +
                "ORDER BY sn.name"
    )
})


/**
 * SuperNeighborhood
 */
@Entity
@Table(name = "super_neighborhoods")
public class SuperNeighborhood implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "super_neighborhood_id")
    private Long superNeighborhoodID;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    public Long getSuperNeighborhoodID()
    {
        return superNeighborhoodID;
    }

    public void setSuperNeighborhoodID(Long superNeighborhoodID)
    {
        this.superNeighborhoodID = superNeighborhoodID;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }
}
