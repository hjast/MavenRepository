//
// LineSegment.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package misc;

/**
 * A straight line segment defined by its two endpoints.
 */
public class LineSegment
{
    private LatLng a;
    private LatLng b;

    /**
     *
     */
    public LineSegment(LatLng a, LatLng b)
    {
        this.a = a;
        this.b = b;
    }

    /**
     * Get the lat-intersect of this point.
     *
     * See http://www.alienryderflex.com/polygon/
     */
    public LatLng getLatIntersect(Double latitude)
    {
        boolean fPtBelow;
        boolean fPtOnOrAbove;
        double dLon;
        double dLat;
        double slope;
        double longitude;

        fPtBelow = a.getLat() < latitude || b.getLat() < latitude;
        fPtOnOrAbove = a.getLat() >= latitude || b.getLat() >= latitude;

        if (!fPtBelow || !fPtOnOrAbove)
        {
            return null;
        }

        // The 'modified slope' is the increase in degrees longitude per
        // increase in degrees latitude, moving from a --> b.
        dLon = b.getLng() - a.getLng();
        dLat = b.getLat() - a.getLat();
        if (dLat == 0)
        {
            return null;
        }
        slope = dLon/dLat;

        longitude = a.getLng() + (latitude - a.getLat())*slope;

        return new LatLng(latitude, longitude);
    }

    // <editor-fold defaultstate="collapsed" desc="Encapsulated getters & setters">

    public LatLng getA()
    {
        return a;
    }

    public void setA(LatLng a)
    {
        this.a = a;
    }

    public LatLng getB()
    {
        return b;
    }

    public void setB(LatLng b)
    {
        this.b = b;
    }

    // </editor-fold>

}
