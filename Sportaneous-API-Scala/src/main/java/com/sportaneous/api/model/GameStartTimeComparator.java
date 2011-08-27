//
// GameStartTimeComparator.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package com.sportaneous.api.model;

import java.util.Comparator;

/**
 *
 */
public class GameStartTimeComparator implements Comparator<Game>
{
    public int compare(Game o1, Game o2)
    {
        return o1.getStartTime().compareTo(o2.getStartTime());
    }
}
