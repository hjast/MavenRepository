package com.sportaneous.api.model;

/*@NamedQueries({
    //
    // Query: Neighborhood.findByLatLng
    //
    // Parameters:
    //   - lat
    //   - lng
    //
    @NamedQuery(
        name = "PersonalMessage.findByUser",
        query = "SELECT user.personalMessages FROM User user WHERE user.userID=:userID"
    )
})   */
@Entity
@Table(name = "personal_message")
public class PersonalMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_to")
    private User userTo;


    public User getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(User userFrom) {
        this.userFrom = userFrom;
    }

    public PersonalMessageType getPersonalMessageType() {
        return personalMessageType;
    }

    public void setPersonalMessageType(PersonalMessageType personalMessageType) {
        this.personalMessageType = personalMessageType;
    }

    @ManyToOne
    @JoinColumn(name="user_from", nullable = false)
    private User userFrom;

    @Enumerated(EnumType.STRING)
    @Column(name = "personal_message_type")
    private PersonalMessageType personalMessageType;

    @Column(name="message")
    @Lob
    private String message;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }



    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

 /*   public User otherPerson(Long id)
    {
        return user.getUserID() == id
    }
    */
}
