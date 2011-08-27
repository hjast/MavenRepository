package com.sportaneous.api.model;

import java.util.Calendar;

@NamedQueries({
        @NamedQuery(name="GroupEmailInvite.findByUser",
                query="Select gp FROM GroupEmailInvite gp where gp.email=:email"
        )
})
@Table(
    name = "game_email_invite",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id","game_id","email"})
    }
)
@Entity
public class GroupEmailInvite {
     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User inviter;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name="email")
    private String email;


  @Column(name = "timeSent")
  @Temporal(TemporalType.TIMESTAMP)
    private Calendar timeCreated;

    public User getInviter() {
        return inviter;
    }

    public void setInviter(User inviter) {
        this.inviter = inviter;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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
}
