package com.sportaneous.api.model;

import java.util.Calendar;


@NamedQueries({
@NamedQuery(name = "GameNotifications.numberGamesProposed",
        query = "SELECT Count(*) FROM GameNotifications g WHERE g.timeCreated  BETWEEN :from AND :until AND g.user=:user")
})
@Entity
@Table(name = "game_notifications")
public class GameNotifications {

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

  @Column(name = "timeSent")
  @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeCreated;

    public Calendar getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Calendar timeCreated) {
        this.timeCreated = timeCreated;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}