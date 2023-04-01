package com.htuozhou.wvp.persistence.service.cache;


import com.htuozhou.wvp.persistence.po.UserPO;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/2/3
 */
public interface IUserCacheService {

    List<UserPO> list();

    void listEvict();

}
