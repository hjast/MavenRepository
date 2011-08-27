package com.sportaneous.api.model;

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 6/20/11
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(
    name = "group_participants",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"user_id","group_id"})
    }
)
public class GroupParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

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

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    @Column(name = "is_admin", nullable = false)

    private Boolean isAdmin;

}
