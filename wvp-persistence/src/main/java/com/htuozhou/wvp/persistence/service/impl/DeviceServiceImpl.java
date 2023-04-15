package com.htuozhou.wvp.persistence.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.htuozhou.wvp.persistence.mapper.DeviceMapper;
import com.htuozhou.wvp.persistence.po.DevicePO;
import com.htuozhou.wvp.persistence.service.IDeviceService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 设备表 服务实现类
 * </p>
 *
 * @author HTuoZhou
 * @since 2023-04-14
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, DevicePO> implements IDeviceService {

}
