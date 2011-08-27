//
// Facility.java
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
    // Query: Facility.findByRegion
    //
    // Parameters:
    //   - region
    //
    @NamedQuery(
        name = "Facility.findByRegion",
        query = "SELECT facility FROM Facility facility " +
                "WHERE facility.region=:region " +
                "ORDER BY facility.name"
    ),
    @NamedQuery(
            name= "Facility.getUpcomingGames",
            query = "SELECT game FROM Game game WHERE game.facility = :fac AND game.startTime > :startTime"
    ),
     @NamedQuery(name = "Facility.getGameHistory",
     query = "SELECT game FROM Game game WHERE game.facility = :fac"
     )
})

/**
 * Facility
 */
@Entity
@Table(name = "facilities")
public class Facility implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "facility_id")
    private Long facilityID;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id", nullable = true)
    private Neighborhood neighborhood;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "addressLine1")
    private String addressLine1;

    @Column(name = "addressLine2")
    private String addressLine2;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "zip")
    private String zip;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "notes")
    private String notes;

    @Column(name="long_description")
    @Lob
    private String longDescription;

    @OneToMany(mappedBy = "facility")
    private List<FacilitySport> sports;

    public Long getFacilityID()
    {
        return facilityID;
    }

    public void setFacilityID(Long facilityID)
    {
        this.facilityID = facilityID;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }

    public Neighborhood getNeighborhood()
    {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood)
    {
        this.neighborhood = neighborhood;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getShortDescription()
    {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription)
    {
        this.shortDescription = shortDescription;
    }

    public String getAddressLine1()
    {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1)
    {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2()
    {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2)
    {
        this.addressLine2 = addressLine2;
    }

    public String getCity()
    {
        return city;
    }

    public void setCity(String city)
    {
        this.city = city;
    }

    public String getState()
    {
        return state;
    }

    public void setState(String state)
    {
        this.state = state;
    }

    public String getZip()
    {
        return zip;
    }

    public void setZip(String zip)
    {
        this.zip = zip;
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

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public List<FacilitySport> getSports()
    {
        return sports;
    }

    public void setSports(List<FacilitySport> sports)
    {
        this.sports = sports;
    }

}
