package com.sportaneous.api.model;

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 6/21/11
 * Time: 3:57 AM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name="recurring_games")
public class RecurringGame {

        @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "game_id")
    private Long gameID;



    /*
    public GameType getGameType() {
        return gameType;
    }

    public void setGameType(GameType gameType) {
        this.gameType = gameType;
    }

    @Column(name = "sport", nullable = false)
    @Enumerated(EnumType.STRING)
    private Sport sport;
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
   */

}
