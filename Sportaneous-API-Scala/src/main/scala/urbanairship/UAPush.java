//
// UAPush.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package urbanairship;

/**
 *
 */
@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class UAPush
{
    public String[] device_tokens;
    public UAAPS aps;
    public Long gameID;
}
