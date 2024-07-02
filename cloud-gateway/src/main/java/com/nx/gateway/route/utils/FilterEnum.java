package com.nx.gateway.route.utils;


public enum FilterEnum {
    AddRequestHeader,
    AddRequestParameter,
    AddResponseHeader,
    DedupeResponseHeader,
    Hystrix,
    CircuitBreaker,
    FallbackHeaders,
    MapRequestHeader,
    PrefixPath,
    PreserveHostHeader,
    RequestRateLimiter,
    RedirectTo,
    RemoveRequestHeader,
    RemoveResponseHeader,
    RemoveRequestParameter,
    RewritePath,
    RewriteLocationResponseHeader,
    RewriteResponseHeader,
    SaveSession,
    SecureHeaders,
    SetPath,
    SetRequestHeader,
    SetResponseHeader,
    SetStatus,
    StripPrefix,
    Retry,
    RequestSize,
    ModifyRequestBody,
    ModifyResponseBody
    ,RequestHeaderToRequestUri, RequestHeaderSize;

    @Override
    public String toString() {
        return this.name();
    }

    public static FilterEnum lookup(String v) {
        if (v==null) return null;
        for (FilterEnum f : FilterEnum.values()) {
            if (f.name().equalsIgnoreCase(v)) {
                return f;
            }
        }
        return null;
    }
}
