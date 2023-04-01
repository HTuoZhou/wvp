package com.htuozhou.wvp.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.htuozhou.wvp.persistence.mapper.UserMapper;
import com.htuozhou.wvp.persistence.po.UserPO;
import com.htuozhou.wvp.persistence.service.IUserService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author HTuoZhou
 * @since 2023-02-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserPO> implements IUserService {

}
