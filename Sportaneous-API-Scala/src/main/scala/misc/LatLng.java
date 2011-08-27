//
// LatLng.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package misc;

/**
 *
 */
public class LatLng
{
    private Double lat;
    private Double lng;

    /**
     * Default constructor.
     */
    public LatLng()
    {}

    /**
     * Alternative constructor.
     */
    public LatLng(Double lat, Double lng)
    {
        this.lat = lat;
        this.lng = lng;
    }

    // <editor-fold defaultstate="collapsed" desc="Encapsulated getters & setters">

    public Double getLat()
    {
        return lat;
    }

    public void setLat(Double lat)
    {
        this.lat = lat;
    }

    public Double getLng()
    {
        return lng;
    }

    public void setLng(Double lng)
    {
        this.lng = lng;
    }

    // </editor-fold>

}
