package com.sportaneous.api.model;

import java.util.Calendar;


@NamedQueries({
        @NamedQuery(name="GameUserInvite.findByUser",
                query="Select gp FROM GameUserInvite gp where gp.invitee=:user"
        )
})
@Table(
    name = "game_user_invite",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id","game_id","user_invitee_id"})
    }
)
@Entity
public class GameUserInvite {

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

    public Calendar getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Calendar timeCreated) {
        this.timeCreated = timeCreated;
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User inviter;

    public User getInvitee() {
        return invitee;
    }

    public void setInvitee(User invitee) {
        this.invitee = invitee;
    }

    @ManyToOne
    @JoinColumn(name = "user_invitee_id", nullable = false)
    private User invitee;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;



  @Column(name = "timeSent")
  @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeCreated;

  @Column(name="game_create")
  private Boolean onGameCreate;

    public Boolean getOnGameCreate() {
        return onGameCreate;
    }

    public void setOnGameCreate(Boolean onGameCreate) {
        this.onGameCreate = onGameCreate;
    }
}
