//
// UserPasscode.java
//
// Copyright (c) 2011 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.io.Serializable;
import java.util.Calendar;

/**
 * Named Queries
 */
@NamedQueries({
    //
    // Query: UserPasscode.findByPasscode
    //
    // Parameters:
    //   - passcode
    //
    @NamedQuery(
        name = "UserPasscode.findByPasscode",
        query = "SELECT userPasscode FROM UserPasscode userPasscode " +
                "WHERE UserPasscode.passcode=:passcode " +
                "AND UserPasscode.expiration > Now()"
    ),

    //
    // Query: UserPasscode.cleanUpOldPasscodes
    //
    // Parameters:
    //   - user
    //
    @NamedQuery(
        name = "UserPasscode.cleanUpOldPasscodes",
        query = "DELETE FROM UserPasscode userPasscode " +
                "WHERE UserPasscode.user=:user AND expiration < NOW()"
    )
})

/**
 * UserPasscode - The table maps passcodes to users.  There may be zero, one, or
 * multiple passcodes for a particular user.  Passcodes are unique in the table,
 * however.  Each passcode is accompanied by an expiration date and a password
 * hash.  A passcode after the expiration date is invalid, as is a passcode
 * whose password hash does not match the user's current password hash.  The
 * latter case is so that we can guarantee that when a user changes their
 * password they also invalidate all outstanding passcodes.
 */
@Entity
@Table(
    name = "user_passcodes",
    uniqueConstraints={@UniqueConstraint(columnNames={"passcode"})}
)
public class UserPasscode implements Serializable
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_passcode_ID")
    private Long userPasscodeID;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "passcode")
    private String passcode;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "expiration", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar expiration;

    public Long getUserPasscodeID()
    {
        return userPasscodeID;
    }

    public User getUser()
    {
        return user;
    }

    public String getPasscode()
    {
        return passcode;
    }

    public String getPasswordHash()
    {
        return passwordHash;
    }

    public Calendar getExpiration()
    {
        return expiration;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public void setPasscode(String passcode)
    {
        this.passcode = passcode;
    }

    public void setPasswordHash(String passwordHash)
    {
        this.passwordHash = passwordHash;
    }

    public void setExpiration(Calendar expiration)
    {
        this.expiration = expiration;
    }

}
