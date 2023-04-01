package com.htuozhou.wvp.persistence.service.cache.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.htuozhou.wvp.persistence.po.UserPO;
import com.htuozhou.wvp.persistence.service.IUserService;
import com.htuozhou.wvp.persistence.service.cache.IUserCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hanzai
 * @date 2023/2/3
 */
@Service
@Slf4j
public class UserCacheServiceImpl implements IUserCacheService {

    @Autowired
    private IUserService userService;

    @Override
    @Cacheable(cacheNames = {"user::list"})
    public List<UserPO> list() {
        log.info("获取所有用户信息并放入缓存");
        return userService.list(Wrappers.emptyWrapper());
    }

    @Override
    @CacheEvict(cacheNames = {"user::list"})
    public void listEvict() {
        log.info("从缓存删除所有用户信息");
    }

}
