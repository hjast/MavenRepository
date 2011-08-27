//
// UrbanAirship.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package urbanairship;

/**
 *
 */
public class UrbanAirship
{
    private static final String UA_HOST = "go.urbanairship.com";
    private static final String UA_APP_KEY = "SbEGS_tpQPuVBxB_hIGxUg";
    private static final String UA_APP_MASTER_SECRET = "VPkL9rnMQ-ustM0kZiMcMw";

    /**
     *
     */
    public static void push(String iosDeviceToken, String alert, Long gameID)
    {
        try
        {
            HttpClient client;
            ObjectMapper mapper;
            UAPush push;
            HttpHost target;
            HttpPost post;
            StringEntity se;
            String json;
            HttpResponse response;

            client = getHttpClient();

            push = new UAPush();
            push.device_tokens = new String[1];
            push.device_tokens[0] = iosDeviceToken;
            push.aps = new UAAPS();
            push.aps.alert = alert;
            push.gameID = gameID;

            mapper = new ObjectMapper();
            json = mapper.writeValueAsString(push);

            se = new StringEntity(json);
            se.setContentType("application/json");

            target = new HttpHost(UA_HOST, 443, "https");
            post = new HttpPost("/api/push/");
            post.setEntity(se);

            response = client.execute(target, post);

            if (response.getStatusLine() == null)
            {
                System.out.println("[UrbanAirship] Null response");
            }
            else if (response.getStatusLine().getStatusCode() != 200)
            {
                int code;
                code = response.getStatusLine().getStatusCode();
                System.out.println("[UrbanAirship] " + code + " response");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Private Helpers">

    /**
     * Instantiate and return an HttpClient instance.
     */
    private static HttpClient getHttpClient()
    {
        AuthScope scope;
        Credentials creds;
        DefaultHttpClient client;
        SSLSocketFactory sslSocketFactory;
        final X509HostnameVerifier delegate;

        scope = new AuthScope(UA_HOST, AuthScope.ANY_PORT);
        creds = new UsernamePasswordCredentials(UA_APP_KEY, UA_APP_MASTER_SECRET);

        client = new DefaultHttpClient();
        client.getCredentialsProvider().setCredentials(scope, creds);

        sslSocketFactory = (SSLSocketFactory) client.
                getConnectionManager().
                getSchemeRegistry().getScheme("https").
                getSocketFactory();
        delegate = sslSocketFactory.getHostnameVerifier();
        sslSocketFactory.setHostnameVerifier(new UAHostnameVerifier(delegate));

        return client;
    }

    // </editor-fold>

}
