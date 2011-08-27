package com.sportaneous.api.model;

import java.util.Calendar;


@NamedQueries({
        @NamedQuery(name="GameEmailInvite.findByUser",
                query="Select gp FROM GameEmailInvite gp where gp.email=:email"
        )
})
@Table(
    name = "game_email_invite",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id","game_id","email"})
    }
)
@Entity
public class GameEmailInvite {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Calendar getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Calendar timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ManyToOne

    @JoinColumn(name = "user_id", nullable = false)
    private User inviter;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @Column(name="email")
    private String email;


  @Column(name = "timeSent")
  @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeCreated;

}
