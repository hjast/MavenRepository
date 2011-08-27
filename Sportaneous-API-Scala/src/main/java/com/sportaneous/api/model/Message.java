//
// Message.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.util.Calendar;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: Message.findByGame
    //
    // Parameters:
    //   - game
    //
    @NamedQuery(
        name = "Message.findByGame",
        query = "SELECT message FROM Message message " +
                "WHERE message.game=:game " +
                "ORDER BY message.postTime"
    )
})

/**
 * Message
 */
@Entity
@Table(name = "messages")
public class Message {
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

    @Lob
    @Column(name = "text")
    private String text;

    @Column(name = "post_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar postTime;

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

    public String getText()
    {
        return text;
    }

    public void setText(String notes)
    {
        this.text = notes;
    }

    public Calendar getPostTime()
    {
        return postTime;
    }

    public void setPostTime(Calendar postTime)
    {
        this.postTime = postTime;
    }


}
