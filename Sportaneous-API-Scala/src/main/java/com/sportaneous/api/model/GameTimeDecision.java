//
// GameTimeDecision.java
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
    // Query: GameTimeDecision.findByUserAndGame
    //
    // Parameters:
    //   - user
    //   - game
    //
    @NamedQuery(
        name = "GameTimeDecision.findByUserAndGame",
        query = "SELECT decision FROM GameTimeDecision decision " +
                "WHERE decision.user=:user AND decision.game=:game"
    )
})

/**
 * GameTimeDecision
 */
@Entity
@Table(
    name = "gametime_decisions",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id","game_id"})
    }
)
public class GameTimeDecision implements Serializable
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

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GameTimeDecisionType type;

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

    public GameTimeDecisionType getType()
    {
        return type;
    }

    public void setType(GameTimeDecisionType type)
    {
        this.type = type;
    }
}
