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
    name = "group_game_association",
    uniqueConstraints={
        @UniqueConstraint(columnNames={"game_id","group_id"})
    }
)
public class GroupGameAssociationRank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(name = "group_game_rank", nullable = false)
    private Double rank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getRank() {
        return rank;
    }

    public void setRank(Double rank) {
        this.rank = rank;
    }

    public Game getGame() {

        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }


}
