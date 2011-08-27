//
// GMapsReverseGeocoder.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package misc;

import java.io.IOException;

/**
 * GMapsReverseGeocoder
 */
public class GMapsReverseGeocoder
{
    private static final String URL_FORMAT =
            "http://maps.google.com/maps/api/geocode/json?latlng=%1$.5f,%2$.5f&sensor=true";
    private static final String STATUS_OK = "OK";
    private static final String COMPONENT_TYPE_LOCALITY = "locality";

    public static String getCity(Double lat, Double lng)
    {
        try
        {
            JSONObject json;
            String status;
            JSONObject best;
            JSONArray components;

            json = getReverseGeocodeResponse(lat, lng);
            status = json.getString("status");
            if (!status.equals(STATUS_OK))
            {
                return null;
            }

            best = json.getJSONArray("results").getJSONObject(0);
            components = best.getJSONArray("address_components");
            for (int i = 0; i < components.length(); i++)
            {
                JSONObject component;
                JSONArray types;
                boolean fLocality = false;
                String shortName;

                component = components.getJSONObject(i);
                types = component.getJSONArray("types");
                shortName = component.getString("short_name");

                for (int j = 0; j < types.length() && !fLocality; j++)
                {
                    String type;
                    type = types.getString(j);
                    if (type.equals(COMPONENT_TYPE_LOCALITY))
                    {
                        fLocality = true;
                    }
                }

                if (fLocality)
                {
                    return shortName;
                }
            }

            return null;
        }
        catch (JSONException e)
        {
            // TODO: error
            return null;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Private Helpers">

    private static JSONObject getReverseGeocodeResponse(Double lat, Double lng)
    {
        try
        {
            String url;
            HttpClient client;
            HttpGet get;
            ResponseHandler<String> responseHandler;
            String response;

            url = String.format(URL_FORMAT, lat, lng);
            client = new DefaultHttpClient();
            get = new HttpGet(url);

            responseHandler = new BasicResponseHandler();
            response = client.execute(get, responseHandler);

            // When HttpClient instance is no longer needed,
            // shut down the connection manager to ensure
            // immediate deallocation of all system resources
            client.getConnectionManager().shutdown();

            return new JSONObject(response);
        }
        catch (IOException e)
        {
            // TODO: error
            return null;
        }
        catch (JSONException e)
        {
            // TODO: error
            return null;
        }
    }

    // </editor-fold>
}
