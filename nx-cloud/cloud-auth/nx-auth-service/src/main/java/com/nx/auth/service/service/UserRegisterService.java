package com.nx.auth.service.service;

import com.nx.api.model.R;
import com.nx.auth.service.model.entity.Org;
import com.nx.auth.service.model.entity.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserRegisterService {
    @Transactional
    R<String> addOrgFromExterUni(Org org);

    R<String> addUser(User user);


    R<String> addUsersForOrg(String orgCode, String accounts);

    List<Org> getOrgListByParentId(String parentId);
}
