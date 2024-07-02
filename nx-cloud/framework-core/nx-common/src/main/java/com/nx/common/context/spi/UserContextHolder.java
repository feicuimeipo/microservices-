package com.nx.common.context.spi;


import com.nx.common.exception.ServerException;
import org.springframework.core.Ordered;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

public interface UserContextHolder extends Comparable<UserContextHolder>{
    static UserContextHolder instances= null;

    public LoginUser getCurrentUserByJwtToken(String jwtToken);

    public LoginUser getJwtToken();

    static LoginUser getCurrentUserByToken(String jwtToken){
        if (getInstance()==null){
            return null;
        }
        return getInstance()!=null?getInstance().getCurrentUserByJwtToken(jwtToken):null;
    }



    default int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    default int compareTo(UserContextHolder o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }


    static UserContextHolder getInstance(){
        if (instances==null) {
            synchronized (UserContextHolder.class) {
                if (instances==null) {
                    List<UserContextHolder> list = new ArrayList<>();
                    if (instances!=null && list.size()>0){
                        throw new ServerException(500,"存在多个UserContextHolder");
                    }
                    ServiceLoader.load(UserContextHolder.class).forEach(list::add);
                    list.stream().sorted(Comparator.comparing(UserContextHolder::getOrder)).collect(Collectors.toList());
                    if (list!=null && list.size()>0){
                        return list.get(0);
                    }
                }
            }
        }
        return null;
    }

}
