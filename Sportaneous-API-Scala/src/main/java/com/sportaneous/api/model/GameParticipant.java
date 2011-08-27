//
// GameParticipant.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;

/**
 * GameParticipant
 */
@NamedQueries({
        @NamedQuery(name="GameParticipant.findParticipantByGameUser",
                query="Select gp FROM GameParticipant gp where gp.user=:user AND gp.game=:game"
        )
})
@Entity
@Table(
    name = "game_participants",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id","game_id"})
    }
)
public class GameParticipant implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name = "is_organizer", nullable = false)
    private Boolean isOrganizer;

    @Column(name="is_invited", nullable = true)
    private Boolean isInvited;

    public Boolean getInvited() {
        return isInvited;
    }

    public void setInvited(Boolean invited) {
        isInvited = invited;
    }

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

    public Game getGame()
    {
        return game;
    }

    public void setGame(Game game)
    {
        this.game = game;
    }

    public Boolean getIsOrganizer()
    {
        return isOrganizer;
    }

    public void setIsOrganizer(Boolean isOrganizer)
    {
        this.isOrganizer = isOrganizer;
    }
}
