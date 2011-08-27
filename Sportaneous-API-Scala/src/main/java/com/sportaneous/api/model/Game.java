//
// Game.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: Game.findAll
    //
    // Parameters: <none>
    //
    @NamedQuery(
        name = "Game.findAll",
        query = "SELECT game FROM Game game"
    )
})

/**
 * Game
 */
@Entity
@Table(name = "games")
public class Game implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameID;

    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Column(name = "sport", nullable = false)
    @Enumerated(EnumType.STRING)
    private Sport sport;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private GameStatus status;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne
    @JoinColumn(name = "facility_id", nullable = false)
    private Facility facility;

    @Column(name = "start_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar startTime;

    @Column(name = "min_player_count", nullable = false)
    private Integer minPlayerCount;

    @Column(name = "max_player_count", nullable = false)
    private Integer maxPlayerCount;

    @Column(name = "play_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private PlayLevel playLevel;

    @Lob
    @Column(name = "notes")
    private String notes;

    @Column(name = "short_url", nullable = true)
    private String shortURL;

    @OneToMany(mappedBy = "game")
    private List<GameParticipant> participants;



    @Enumerated(EnumType.STRING)
    @Column(name = "game_type")
    private GameType gameType;

    @OneToMany(mappedBy = "game")
    private List<Message> messages;

    public Long getGameID()
    {
        return gameID;
    }

    public void setGameID(Long gameID)
    {
        this.gameID = gameID;
    }

    public Sport getSport()
    {
        return sport;
    }

    public void setSport(Sport sport)
    {
        this.sport = sport;
    }

    public GameStatus getStatus()
    {
        return status;
    }

    public void setStatus(GameStatus status)
    {
        this.status = status;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }

    public Facility getFacility()
    {
        return facility;
    }

    public void setFacility(Facility facility)
    {
        this.facility = facility;
    }

    public Calendar getStartTime()
    {
        return startTime;
    }

    public void setStartTime(Calendar startTime)
    {
        this.startTime = startTime;
    }

    public Integer getMinPlayerCount()
    {
        return minPlayerCount;
    }

    public void setMinPlayerCount(Integer minPlayerCount)
    {
        this.minPlayerCount = minPlayerCount;
    }

    public Integer getMaxPlayerCount()
    {
        return maxPlayerCount;
    }

    public void setMaxPlayerCount(Integer maxPlayerCount)
    {
        this.maxPlayerCount = maxPlayerCount;
    }

    public PlayLevel getPlayLevel()
    {
        return playLevel;
    }

    public void setPlayLevel(PlayLevel playLevel)
    {
        this.playLevel = playLevel;
    }

    public String getNotes()
    {
        return notes;
    }

    public void setNotes(String notes)
    {
        this.notes = notes;
    }

    public String getShortURL()
    {
        return shortURL;
    }

    public void setShortURL(String shortURL)
    {
        this.shortURL = shortURL;
    }

    public List<GameParticipant> getParticipants()
    {
        return participants;
    }

    public void setParticipants(List<GameParticipant> participants)
    {
        this.participants = participants;
    }

    public List<Message> getMessages()
    {
        return messages;
    }

    public void setMessages(List<Message> messages)
    {
        this.messages = messages;
    }

    @Transient
    public User getOrganizer()
    {
        for (GameParticipant participant : participants)
        {
            if (participant.getIsOrganizer())
            {
                return participant.getUser();
            }
        }
        return null;
    }

     @Column(name="privacy", nullable=true)
    @Enumerated(EnumType.STRING)
    private GamePrivacy privacy;

    public GamePrivacy getPrivacy() {
        return privacy;
    }

    public void setPrivacy(GamePrivacy privacy) {
        this.privacy = privacy;
    }


}
