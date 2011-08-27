package com.sportaneous.api.model;

//
// UserSports.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

import java.io.Serializable;

/**
 * UserSports
 */
@NamedQueries({
        @NamedQuery(
                name="UserFacility.getMembers",
                query="select userFac.user from UserFacility userFac where userFac.facility=:facility"
        )

})
@Entity
@Table(name = "user_facilities")
public class UserFacility implements Serializable
{
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Facility getFacility() {
        return facility;
    }

    public void setFacility(Facility facility) {
        this.facility = facility;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;
}
