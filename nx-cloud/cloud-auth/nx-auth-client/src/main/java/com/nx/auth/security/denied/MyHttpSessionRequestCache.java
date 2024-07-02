package com.nx.auth.security.denied;

import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.stereotype.Component;

@Component("myHttpSessionRequestCache")
public class MyHttpSessionRequestCache  extends HttpSessionRequestCache {
    public MyHttpSessionRequestCache() {
        super();
    }
}
