package com.sportaneous.api.model;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: reuben
 * Date: 6/20/11
 * Time: 10:57 PM
 * To change this template use File | Settings | File Templates.
 */


@Entity
@Table(name="groups")
public class Group {

     @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Long groupID;

    @Column(name="name" , nullable=false)
    private String name;

    public Long getGroupID() {
        return groupID;
    }

    public void setGroupID(Long groupID) {
        this.groupID = groupID;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getAboutUs() {
        return aboutUs;
    }

    public void setAboutUs(String aboutUs) {
        this.aboutUs = aboutUs;
    }

    public String getLongURL() {
        return longURL;
    }

    public void setLongURL(String longURL) {
        this.longURL = longURL;
    }

    public List<GroupParticipant> getMembers() {
        return members;
    }

    public void setMembers(List<GroupParticipant> members) {
        this.members = members;
    }

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Lob
    @Column(name = "about_us")
    private String aboutUs;

    @Column(name = "long_url", nullable = true)
    private String longURL;

    @OneToMany(mappedBy = "group")
    private List<GroupParticipant> members;


}
