//
// Neighborhood.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;
import java.util.List;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: Neighborhood.findByLatLng
    //
    // Parameters:
    //   - lat
    //   - lng
    //
    @NamedQuery(
        name = "Neighborhood.findByLatLng",
        query = "SELECT n FROM Neighborhood n " +
                "WHERE n.minLat <= :lat AND n.maxLat >= :lat " +
                "AND   n.minLng <= :lng AND n.maxLng >= :lng"
    ),
    //
    // Query: Neighborhood.findByRegion
    //
    // Parameters:
    //   - region
    //
    @NamedQuery(
        name = "Neighborhood.findByRegion",
        query = "SELECT neighborhood FROM Neighborhood neighborhood " +
                "WHERE neighborhood.region=:region " +
                "ORDER BY neighborhood.name"
    ),

    @NamedQuery(
            name= "Neighborhood.getUpcomingGames",
            query = "SELECT game FROM Game game WHERE game.facility.neighborhood = :neigh AND game.startTime > :startTime"
    ),
     @NamedQuery(name = "Neighborhood.getGameHistory",
        query = "SELECT game FROM Game game WHERE game.facility.neighborhood = :neigh"
     )
})


/**
 * Neighborhood
 */
@Entity
@Table(name = "neighborhoods")
public class Neighborhood implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "neighborhood_id")
    private Long neighborhoodID;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne
    @JoinColumn(name = "super_neighborhood_id", nullable = false)
    private SuperNeighborhood superNeighborhood;

    @Column(name = "zillow_region_id")
    private String zillowRegionID;

    @OneToMany(mappedBy = "neighborhood")
    private List<NeighborhoodBoundary> boundaries;

    @Column(name = "min_lat", nullable = false)
    private Double minLat;

    @Column(name = "max_lat", nullable = false)
    private Double maxLat;

    @Column(name = "min_lng", nullable = false)
    private Double minLng;

    @Column(name = "max_lng", nullable = false)
    private Double maxLng;

    public Long getNeighborhoodID()
    {
        return neighborhoodID;
    }

    public void setNeighborhoodID(Long neighborhoodID)
    {
        this.neighborhoodID = neighborhoodID;
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

    public SuperNeighborhood getSuperNeighborhood()
    {
        return superNeighborhood;
    }

    public void setSuperNeighborhood(SuperNeighborhood superNeighborhood)
    {
        this.superNeighborhood = superNeighborhood;
    }

    public String getZillowRegionID()
    {
        return zillowRegionID;
    }

    public void setZillowRegionID(String zillowRegionID)
    {
        this.zillowRegionID = zillowRegionID;
    }

    public List<NeighborhoodBoundary> getBoundaries()
    {
        return boundaries;
    }

    public void setBoundaries(List<NeighborhoodBoundary> boundaries)
    {
        this.boundaries = boundaries;
    }

    public Double getMinLat()
    {
        return minLat;
    }

    public void setMinLat(Double minLat)
    {
        this.minLat = minLat;
    }

    public Double getMaxLat()
    {
        return maxLat;
    }

    public void setMaxLat(Double maxLat)
    {
        this.maxLat = maxLat;
    }

    public Double getMinLng()
    {
        return minLng;
    }

    public void setMinLng(Double minLng)
    {
        this.minLng = minLng;
    }

    public Double getMaxLng()
    {
        return maxLng;
    }

    public void setMaxLng(Double maxLng)
    {
        this.maxLng = maxLng;
    }

}
