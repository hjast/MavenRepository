//
// BitLy.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package misc;

import java.io.IOException;
import java.net.URLEncoder;

/**
 *
 */
public class BitLy
{
    private static final String API_LOGIN = "sportaneous";
    private static final String API_KEY = "R_08d9ee7ee57ea7cebfd0c492b076c56c";

    private static final String API_URL = "http://api.bit.ly";
    private static final String API_METHOD_SHORTEN = "/v3/shorten";

    /**
     *
     */
    public static String shorten(String longURL)
    {
        try
        {
            HttpClient client;
            StringBuilder params;
            HttpGet get;
            HttpResponse response;
            String shortURL;

            client = new DefaultHttpClient();

            params = new StringBuilder();
            params.append("login=" + API_LOGIN);
            params.append("&apiKey=" + API_KEY);
            params.append("&longUrl=" + URLEncoder.encode(longURL, HTTP.UTF_8));
            params.append("&format=txt");

            get = new HttpGet(API_URL + API_METHOD_SHORTEN + "?" + params.toString());

            response = client.execute(get);
            if (response.getStatusLine() == null)
            {
                System.out.println("[bit.ly] Null response");
                return null;
            }
            else if (response.getStatusLine().getStatusCode() != 200)
            {
                int code;
                code = response.getStatusLine().getStatusCode();
                System.out.println("[bit.ly] " + code + " response + (" + response.getStatusLine().getReasonPhrase() + ")");
                return null;
            }

            shortURL = EntityUtils.toString(response.getEntity());
            return shortURL;
        }
        catch (IOException e)
        {
            System.out.println("[bit.ly] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
        catch (RuntimeException e)
        {
            System.out.println("[bit.ly] " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

}
