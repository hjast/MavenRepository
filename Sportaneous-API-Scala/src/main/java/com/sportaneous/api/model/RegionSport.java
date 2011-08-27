//
// RegionSport.java
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
    // Query: RegionSport.findByRegion
    //
    // Parameters:
    //   - region
    //
    @NamedQuery(
        name = "RegionSport.findByRegion",
        query = "SELECT rs FROM RegionSport rs " +
                "WHERE rs.region=:region"
    )
})

/**
 * RegionSport
 */
@Entity
@Table(name = "region_sports")
public class RegionSport implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "sport", nullable = false)
    @Enumerated(EnumType.STRING)
    private Sport sport;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }

    public Sport getSport()
    {
        return sport;
    }

    public void setSport(Sport sport)
    {
        this.sport = sport;
    }
}
