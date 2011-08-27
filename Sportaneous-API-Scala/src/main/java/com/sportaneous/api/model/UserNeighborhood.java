//
// UserNeighborhood.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;

/**
 * UserNeighborhood
 */
@Entity
@Table(name = "user_neighborhoods")
public class UserNeighborhood implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id", nullable = false)
    private Neighborhood neighborhood;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Neighborhood getNeighborhood()
    {
        return neighborhood;
    }

    public void setNeighborhood(Neighborhood neighborhood)
    {
        this.neighborhood = neighborhood;
    }
}
