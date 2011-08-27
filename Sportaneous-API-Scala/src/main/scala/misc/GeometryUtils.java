//
// GeometryUtils.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package misc;

/**
 * GeometryUtils
 *
 */
public class GeometryUtils
{
    public static final Double AVERAGE_EARTH_RADIUS = 3956D; // Wikipedia

    /**
     * Given two validated locations, returns the distance between the two, in
     * miles.
     *
     * Implemented using the Haversine formula:
     *
     * haversin(d/R) = haversin(dlat) + cos(lat1)*cos(lat2)*haversin(dlon)
     *
     * where d is the distance between the two points, R is the radius of the
     * earth, and haversin(theta) = sin^2(theta/2).
     *
     * more info: http://en.wikipedia.org/wiki/Haversine_formula
     */
    public static Double getDistanceBetweenLocations(double lat1,
                                                     double lng1,
                                                     double lat2,
                                                     double lng2)
    {
        double dlat, dlon;

        // some intermediate values.  a is the square of half the straight line
        // (chord) distance between the two points.  c is the great circle
        // distance in radians.  d is the 'surface' distance between the two
        // points, in miles.
        double a, c, d;

        // Convert the appropiate cartestian coordinates to radians.
        // (pi radians / 180 degrees)
        dlat = (lat2 - lat1) * (Math.PI / 180);
        dlon = (lng2 - lng1) * (Math.PI / 180);

        // If the two locations have the same cartesian coordinates, spare us
        // some unnecessary computations and just return a distance of 0
        // between them.
        if (dlat == 0 && dlon == 0)
        {
            return Double.valueOf(0);
        }

        a = Math.pow(Math.sin(dlat/2.0), 2) +
            Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin(dlon/2.0), 2);
        c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0-a));
        d = AVERAGE_EARTH_RADIUS * c;

        return Double.valueOf(d);
    }
}
