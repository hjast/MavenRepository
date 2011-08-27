//
// APIService.java
//
// Copyright (c) 2010 by Sportaneous, Inc.
//

package urbanairship;

import javax.net.ssl.SSLException;

/**
 * UAHostnameVerifier
 */
public class UAHostnameVerifier extends AbstractVerifier
{
    private X509HostnameVerifier delegate;

    /**
     * Default constructor.
     */
    public UAHostnameVerifier(X509HostnameVerifier delegate)
    {
        this.delegate = delegate;
    }

    /**
     *
     */
    public void verify(String host,
                       String[] cns,
                       String[] subjectAlts) throws SSLException
    {
        /*
        boolean ok = false;
        try {
            delegate.verify(host, cns, subjectAlts);
        } catch (SSLException e) {
            for (String cn : cns) {
                if (cn.startsWith("*.")) {
                    try {
                        delegate.verify(host, new String[] { cn
                                .substring(2) }, subjectAlts);
                        ok = true;
                    } catch (Exception e1) { }
                }
            }
            if(!ok) throw e;
        }
        */
    }
}
