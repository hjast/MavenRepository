//
// FacilitySport.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;

/**
 * FacilitySport
 */
@Entity
@Table(name = "facility_sports")
public class FacilitySport implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

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

    public Facility getFacility()
    {
        return facility;
    }

    public void setFacility(Facility facility)
    {
        this.facility = facility;
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
