package com.nx.gateway.route.utils;


public enum PredicateEnum {
    After,Before,Between,Cookie,Header,Host,Method,Path,Query,RemoteAddr,Weight,CloudFoundryRouteService,ReadBody;

            @Override
            public String toString() {
                return this.name();
            }


            public static PredicateEnum lookup(String v) {
                if (v==null) return null;
                for (PredicateEnum p : PredicateEnum.values()) {
                    if (p.name().equalsIgnoreCase(v)) {
                        return p;
                    }
        }
        return null;
    }
}
