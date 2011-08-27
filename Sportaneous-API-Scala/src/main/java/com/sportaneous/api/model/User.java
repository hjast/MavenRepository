//
// User.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import weka.classifiers.Classifier;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: User.findByEmail
    //
    // Parameters:
    //   - email
    //
    @NamedQuery(
        name = "User.findByEmail",
        query = "SELECT user FROM User user WHERE user.email=:email"
    ),
    //
    // Query: User.findByEmailAndPasswordHash
    //
    // Parameters:
    //   - email
    //   - passwordHash
    //
    @NamedQuery(
        name = "User.findByEmailAndPasswordHash",
        query = "SELECT user FROM User user " +
                "WHERE user.email=:email " +
                "AND user.passwordHash=:passwordHash"
    ),
    //
    // Query: User.findByFbUID
    //
    // Parameters:
    //   - fbUID
    //
    @NamedQuery(
        name = "User.findByFbUID",
        query = "SELECT user FROM User user WHERE user.fbUID=:fbUID"
    ),
    @NamedQuery(
            name= "User.getUpcomingGames",
            query = "SELECT game FROM Game game, GameParticipant gp WHERE gp.user= :user AND gp.game = game " +
                    "AND game.startTime > :startTime"
    ),
     @NamedQuery(name = "User.getGameHistory",
     query = "SELECT game FROM Game game, GameParticipant gp WHERE gp.user = :user AND gp.game = game "
     ),
     @NamedQuery(name = "User.findBySettings",
             query = "SELECT distinct user FROM User user, UserNeighborhood un, UserSports us, UserFacility uf " +
                     "WHERE  user=us.user  AND (user= un.user AND un.neighborhood=:neighborhood OR uf.facility=:facility AND user=uf.user) AND us.sport=:sport")
})

/**
 * User
 */
@Entity

@Table(name = "users")
public class User implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userID;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "fb_uid")
    private String fbUID;

    @Column(name = "fb_access_token")
    private String fbAccessToken;

    @Column(name = "fb_session_expires")
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar fbSessionExpires;

    @Column(name = "twitter_access_token")
    private String twAccessToken;

    @Column(name= "twitter_secret_token")
    private String twSecretToken;

    @Column(name="automatic_twitter_push")
    private Boolean autoTwitterPush;



    public Boolean getAutoTwitterPush() {
        return autoTwitterPush;
    }

    public void setAutoTwitterPush(Boolean autoTwitterPush) {
        this.autoTwitterPush = autoTwitterPush;
    }

    public Integer getProfilePoints() {
        return profilePoints;
    }

    public String getTwSecretToken() {
        return twSecretToken;
    }

    public void setTwSecretToken(String twSecretToken) {
        this.twSecretToken = twSecretToken;
    }

    public void setProfilePoints(Integer profilePoints) {
        this.profilePoints = profilePoints;
    }

   /* public List<PersonalMessage> getSentMessages() {
        return sentMessages;
    }

    public void setSentMessages(List<PersonalMessage> sentMessages) {
        this.sentMessages = sentMessages;
    }

    public List<PersonalMessage> getReceivedMessages() {
        return receivedMessages;
    }

    public void setReceivedMessages(List<PersonalMessage> receivedMessages) {
        this.receivedMessages = receivedMessages;
    }
    */

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @Column(name = "ios_device_token", length = 64)
    private String iosDeviceToken;

    @OneToMany(mappedBy = "user")
    private List<UserNeighborhood> neighborhoods;

    @OneToMany(mappedBy = "user")
    private List<UserSports> sports;


    @Column(name = "goal_count")
    private Integer goalCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "goal_period")
    private FitnessGoalPeriod goalPeriod;

    @Lob
    @Column(name = "about_me")
    private String aboutMe;

    @Column(name = "profile_points")
    private Integer profilePoints;

    public Boolean isAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    @Column(name ="admin")
    private Boolean admin;



    /*
    @OneToMany(mappedBy = "user")
    List<PersonalMessage> messages;

    public List<PersonalMessage> getSentMessages(EntityManager em)
    {
        Query q = em.createQuery("FROM PersonalMessage pm WHERE pm.user=:u AND pm.personalMessageType = 'SENT'");
        q.setParameter("u", this);

        return q.getResultList();
    }

      public List<PersonalMessage> getReceivedMessages(EntityManager em)
    {
        Query q = em.createQuery("FROM PersonalMessage pm WHERE pm.user=:u AND pm.personalMessageType = 'RECEIVED'");
        q.setParameter("u", this);

        return q.getResultList();
    }
    */
    @ManyToMany

    @JoinTable(name="user_friend", joinColumns =
        @JoinColumn(name="users", referencedColumnName="user_id"),
        inverseJoinColumns = @JoinColumn(name="friends",referencedColumnName="user_id"))
    private List<User> myFriends;

    @ManyToMany(mappedBy = "myFriends")
    private List<User> friendsOf;

   @Column(name = "push_notifications",nullable = true)
    private Boolean pushNotifications;

   @Column(name = "email_notifications",nullable = true)
    private Boolean emailNotifications;

    public Boolean getPushNotifications() {
        return pushNotifications;
    }

    public void setPushNotifications(boolean pushNotifications) {
        this.pushNotifications = pushNotifications;
    }

    public Boolean getEmailNotifications() {
        return emailNotifications;
    }

    public void setEmailNotifications(boolean emailNotifications) {
        this.emailNotifications = emailNotifications;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public void setMyFriends(List<User> myFriends) {
        this.myFriends = myFriends;
    }

    public void setFriendsOf(List<User> friendsOf) {
        this.friendsOf = friendsOf;
    }


    public String getAboutMe() {
        return aboutMe;
    }

    public List<User> getMyFriends() {
        return myFriends;
    }

    public List<User> getFriendsOf() {
        return friendsOf;
    }



    public Long getUserID()
    {
        return userID;
    }

    public void setUserID(Long userID)
    {
        this.userID = userID;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public String getFbUID()
    {
        return fbUID;
    }

    public void setFbUID(String fbUID)
    {
        this.fbUID = fbUID;
    }

    public String getFbAccessToken()
    {
        return fbAccessToken;
    }

    public void setFbAccessToken(String fbAccessToken)
    {
        this.fbAccessToken = fbAccessToken;
    }

    public Calendar getFbSessionExpires()
    {
        return fbSessionExpires;
    }

    public void setFbSessionExpires(Calendar fbSessionExpires)
    {
        this.fbSessionExpires = fbSessionExpires;
    }

    public Region getRegion()
    {
        return region;
    }

    public void setRegion(Region region)
    {
        this.region = region;
    }

    public String getIosDeviceToken()
    {
        return iosDeviceToken;
    }

    public void setIosDeviceToken(String iosDeviceToken)
    {
        this.iosDeviceToken = iosDeviceToken;
    }

    public List<UserNeighborhood> getNeighborhoods()
    {
        return neighborhoods;
    }

    public void setNeighborhoods(List<UserNeighborhood> neighborhoods)
    {
        this.neighborhoods = neighborhoods;
    }

    public List<UserSports> getSports()
    {
        return sports;
    }

    public void setSports(List<UserSports> sports)
    {
        this.sports = sports;
    }

    public Integer getGoalCount()
    {
        return goalCount;
    }

    public void setGoalCount(Integer goalCount)
    {
        this.goalCount = goalCount;
    }

    public FitnessGoalPeriod getGoalPeriod()
    {
        return goalPeriod;
    }

    public void setGoalPeriod(FitnessGoalPeriod goalPeriod)
    {
        this.goalPeriod = goalPeriod;
    }
    //TODO
    public void setClassifer(Classifier u)
    {

    }
    //TODO
    public Classifier getClassifier()
    {
        return null;
    }

    public String getTwAccessToken() {
        return twAccessToken;
    }

    public void setTwAccessToken(String twAccessToken) {
        this.twAccessToken = twAccessToken;
    }
}
