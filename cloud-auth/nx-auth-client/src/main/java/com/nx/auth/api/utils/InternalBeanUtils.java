package com.nx.auth.api.utils;

import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public class InternalBeanUtils {

    /**
     * 可以用于判断 Map,Collection,String,Array,Long是否为空
     *
     * @param o
     *            java.lang.Object.
     * @return boolean.
     */
    public static boolean isEmpty(Object o) {
        if (o == null)
            return true;
        if (o instanceof String) {
            if (((String) o).trim().length() == 0)
                return true;
        } else if (o instanceof Collection) {
            if (((Collection<?>) o).size() == 0)
                return true;
        } else if (o.getClass().isArray()) {
            if (((Object[]) o).length == 0)
                return true;
        } else if (o instanceof Map) {
            if (((Map<?, ?>) o).size() == 0)
                return true;
        }
        else if(o instanceof Serializable) {
            return ((Serializable)o).toString().trim().length()==0;
        }
        else if(o instanceof ArrayNode) {
            ArrayNode an = (ArrayNode)o;
            return an.isEmpty(null);
        }
        return false;
    }

    public static boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static boolean isInherit(Class cls, Class parentClass) {
        return parentClass.isAssignableFrom(cls);
    }
}
