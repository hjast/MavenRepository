//
// Polygon.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package misc;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Polygon
 */
public class Polygon
{
    private List<LatLng> pts;

    /**
     *
     */
    public Polygon(List<LatLng> pts)
    {
        this.pts = pts;
    }

    /**
     * Return true if the given point is contained in this polygon.
     */
    public boolean contains(LatLng pt)
    {
        List<LineSegment> edges;
        int count;

        // First, prepare a list of the polygons edges.
        edges = new ArrayList<LineSegment>();
        for (int i = 0; i < pts.size(); i++)
        {
            LatLng a;
            LatLng b;
            a = pts.get(i);
            b = pts.get((i+1) % pts.size());
            edges.add(new LineSegment(a, b));
        }

        // For each each, check to see if it has a latitude-intersect
        // with the point in question.  If so, and if it on the (arbitrarily)
        // right side of the point in question, count it.
        count = 0;
        for (LineSegment edge : edges)
        {
            LatLng intersect;
            intersect = edge.getLatIntersect(pt.getLat());
            if (intersect != null &&
                intersect.getLng() > pt.getLng())
            {
                count++;
            }
        }

        // If we have an odd number of such latitude-intersections, then the
        // point in question is inside of our polygon.
        return count % 2 == 1;
    }

    /**
     * Return the mean center of the polygon.
     */
    public LatLng getMeanCenter()
    {
        double latitude = 0;
        double longitude = 0;

        for (LatLng pt : pts)
        {
            latitude += pt.getLat();
            longitude += pt.getLng();
        }

        return new LatLng(latitude/pts.size(), longitude/pts.size());
    }

    // <editor-fold defaultstate="collapsed" desc="Encoding/Decoding">

    /**
     * Encode the given polygon as a string.  We'll encode it as:
     * lat1,lon1,lat2,lon2,...,latN,lonN
     *
     * Where latX and lonX are the Xth latitude/longitude, fixed to 5
     * decimal figures.
     */
    public String encodeAsString()
    {
        NumberFormat nf;
        StringBuilder sb;

        if (pts == null || pts.isEmpty())
        {
            return null;
        }

        nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(5);

        sb = new StringBuilder();
        for (LatLng pt : pts)
        {
            sb.append(nf.format(pt.getLat()));
            sb.append(",");
            sb.append(nf.format(pt.getLng()));
            sb.append(",");
        }

        if (sb.charAt(sb.length() - 1) == ',')
        {
            sb.replace(sb.length() - 1, sb.length(), "");
        }

        return sb.toString();
    }

    /**
     * Static factory method that decodes a string and returns the corresponding
     * polygon.
     */
    public static Polygon decodeString(String s)
    {
        List<LatLng> pts;
        String[] rgPts;

        rgPts = s.split(",");

        if (rgPts.length < 2 || rgPts.length % 2 != 0)
        {
            throw new RuntimeException("Invalid # of boundary pts " +
                                       "[" + rgPts.length + "]");
        }

        pts = new ArrayList<LatLng>();
        for (int i = 0; i < rgPts.length - 1; i += 2)
        {
            Double lat;
            Double lon;

            lat = Double.valueOf(rgPts[i]);
            lon = Double.valueOf(rgPts[i+1]);

            pts.add(new LatLng(lat, lon));
        }

        return new Polygon(pts);
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Encapsulated getters & setters">

    public List<LatLng> getPts()
    {
        return pts;
    }

    public void setPts(List<LatLng> pts)
    {
        this.pts = pts;
    }

    // </editor-fold>

}
