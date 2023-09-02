package com.htuozhou.wvp.business.sip.request.impl.message.impl.response;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.htuozhou.wvp.business.bean.CivilCodeItem;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.business.properties.SIPProperties;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.CivilCodeRunner;
import com.htuozhou.wvp.business.sip.SIPSender;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.utils.XmlUtil;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.dom4j.Element;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sip.RequestEvent;
import javax.sip.message.Response;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * @author hanzai
 * @date 2023/6/3
 */
@Component
@Slf4j
public class CatalogResponseMessageHandler extends AbstractSIPRequestProcessor implements InitializingBean, IMessageHandler {

    private static final String cmdType = "Catalog";

    @Autowired
    private ResponseMessageHandler responseMessageHandler;

    @Autowired
    private SIPSender sipSender;

    @Autowired
    private ISIPService sipService;

    @Autowired
    private CivilCodeRunner civilCodeRunner;

    @Autowired
    private SIPProperties sipProperties;

    @Override
    public void afterPropertiesSet() throws Exception {
        responseMessageHandler.addMessageHandler(cmdType, this);
    }

    @Override
    @SneakyThrows(Exception.class)
    public void handForDevice(RequestEvent requestEvent, DeviceBO deviceBO, Element rootElement) {
        SIPRequest request = (SIPRequest) requestEvent.getRequest();
        RequestEventExt requestEventExt = (RequestEventExt) requestEvent;
        String requestAddress = requestEventExt.getRemoteIpAddress() + ":" + requestEventExt.getRemotePort();
        log.info("[SIP REQUEST MESSAGE RESPONSE] 收到 [SIP ADDRESS:{}] 设备通道信息", requestAddress);

        Response response = getMessageFactory().createResponse(Response.OK, request);
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        log.info("[SIP REQUEST MESSAGE RESPONSE] 设备通道信息回复 [SIP ADDRESS:{}]", requestAddress);

        List<DeviceChannelBO> deviceChannelBOS = new ArrayList<>();
        Integer sumNum = Integer.valueOf(XmlUtil.getText(rootElement, "SumNum"));
        Iterator<Element> deviceListIterator = rootElement.element("DeviceList").elementIterator();
        while (deviceListIterator.hasNext()) {
            Element element = deviceListIterator.next();

            DeviceChannelBO deviceChannelBO = new DeviceChannelBO();
            String channelId = XmlUtil.getText(element, "DeviceID");
            deviceChannelBO.setDeviceId(deviceBO.getDeviceId());
            deviceChannelBO.setChannelId(channelId);
            deviceChannelBO.setName(XmlUtil.getText(element, "Name"));
            if (channelId.length() <= 8) {
                deviceChannelBO.setHasAudio(Boolean.FALSE);
                CivilCodeItem item = civilCodeRunner.getParentCode(channelId);
                if (Objects.nonNull(item)) {
                    deviceChannelBO.setParentId(item.getCode());
                    deviceChannelBO.setCivilCode(item.getCode());
                }
            } else if (channelId.length() == 20) {
                int code = Integer.parseInt(channelId.substring(10, 13));
                if (code == 136 || code == 137 || code == 138) {
                    deviceChannelBO.setHasAudio(Boolean.TRUE);
                } else {
                    deviceChannelBO.setHasAudio(Boolean.FALSE);
                }
                // 设备厂商
                String manufacturer = XmlUtil.getText(element, "Manufacturer");
                // 设备型号
                String model = XmlUtil.getText(element, "Model");
                // 设备归属
                String owner = XmlUtil.getText(element, "Owner");
                // 行政区域
                String civilCode = XmlUtil.getText(element, "CivilCode");
                // 虚拟组织所属的业务分组ID,业务分组根据特定的业务需求制定,一个业务分组包含一组特定的虚拟组织
                String businessGroupID = XmlUtil.getText(element, "BusinessGroupID");
                // 父设备/区域/系统ID
                String parentID = XmlUtil.getText(element, "ParentID");
                // 注册方式(必选)缺省为1;1:符合IETFRFC3261标准的认证注册模式;2:基于口令的双向认证注册模式;3:基于数字证书的双向认证注册模式
                String registerWay = XmlUtil.getText(element, "RegisterWay");
                // 保密属性(必选)缺省为0;0:不涉密,1:涉密
                String secrecy = XmlUtil.getText(element, "Secrecy");
                // 安装地址
                String address = XmlUtil.getText(element, "Address");
                switch (code) {
                    case 200:
                        // 系统目录
                        if (StrUtil.isNotBlank(manufacturer)) {
                            deviceChannelBO.setManufacturer(manufacturer);
                        }
                        if (StrUtil.isNotBlank(model)) {
                            deviceChannelBO.setModel(model);
                        }
                        if (StrUtil.isNotBlank(owner)) {
                            deviceChannelBO.setOwner(owner);
                        }
                        if (StrUtil.isNotBlank(civilCode)) {
                            deviceChannelBO.setCivilCode(civilCode);
                            deviceChannelBO.setParentId(civilCode);
                        } else {
                            if (StrUtil.isNotBlank(parentID) && !Objects.equals(parentID, CommonConstant.NULL_STR)) {
                                deviceChannelBO.setParentId(parentID);
                            }
                        }
                        if (StrUtil.isNotBlank(address)) {
                            deviceChannelBO.setAddress(address);
                        }
                        if (StrUtil.isNotBlank(registerWay)) {
                            deviceChannelBO.setRegisterWay(Integer.parseInt(registerWay));
                        }
                        if (StrUtil.isNotBlank(secrecy)) {
                            deviceChannelBO.setSecrecy(Integer.valueOf(secrecy));
                        }
                        break;
                    case 215:
                        // 业务分组
                        if (StrUtil.isNotBlank(parentID)) {
                            if (!StrUtil.equalsIgnoreCase(parentID.trim(), deviceBO.getDeviceId())) {
                                deviceChannelBO.setParentId(parentID);
                            }
                        } else {
                            if (StrUtil.isNotBlank(civilCode)) {
                                deviceChannelBO.setCivilCode(civilCode);
                            }
                        }
                        break;
                    case 216:
                        // 虚拟组织
                        if (StrUtil.isNotBlank(businessGroupID)) {
                            deviceChannelBO.setBusinessGroupId(businessGroupID);
                        }

                        if (StrUtil.isNotBlank(parentID)) {
                            if (parentID.contains("/")) {
                                String[] split = parentID.split("/");
                                parentID = split[split.length - 1];
                            }
                            deviceChannelBO.setParentId(parentID);
                        } else {
                            if (StrUtil.isNotBlank(businessGroupID)) {
                                deviceChannelBO.setParentId(businessGroupID);
                            }
                        }
                        break;
                    default:
                        // 设备目录
                        if (StrUtil.isNotBlank(manufacturer)) {
                            deviceChannelBO.setManufacturer(manufacturer);
                        }
                        if (StrUtil.isNotBlank(model)) {
                            deviceChannelBO.setModel(model);
                        }
                        if (StrUtil.isNotBlank(owner)) {
                            deviceChannelBO.setOwner(owner);
                        }
                        if (StrUtil.isNotBlank(civilCode)
                                && civilCode.length() <= 8
                                && NumberUtils.isParsable(civilCode)
                                && civilCode.length() % 2 == 0
                        ) {
                            deviceChannelBO.setCivilCode(civilCode);
                        }
                        if (StrUtil.isNotBlank(businessGroupID)) {
                            deviceChannelBO.setBusinessGroupId(businessGroupID);
                        }

                        // 警区
                        String block = XmlUtil.getText(element, "Block");
                        if (StrUtil.isNotBlank(block)) {
                            deviceChannelBO.setBlock(block);
                        }
                        if (StrUtil.isNotBlank(address)) {
                            deviceChannelBO.setAddress(address);
                        }

                        if (StrUtil.isNotBlank(secrecy)) {
                            deviceChannelBO.setSecrecy(Integer.valueOf(secrecy));
                        }

                        // 当为设备时,是否有子设备(必选)1有,0没有
                        String parental = XmlUtil.getText(element, "Parental");
                        if (StrUtil.isNotBlank(parental)) {// 由于海康会错误的发送65535作为这里的取值,所以这里除非是0否则认为是1
                            if (StrUtil.isNotBlank(parental) && parental.length() == 1 && Integer.parseInt(parental) == 0) {
                                deviceChannelBO.setParental(0);
                            } else {
                                deviceChannelBO.setParental(1);
                            }
                        }
                        // 父设备/区域/系统ID
                        if (StrUtil.isNotBlank(parentID)) {
                            if (parentID.contains("/")) {
                                String[] split = parentID.split("/");
                                deviceChannelBO.setParentId(split[split.length - 1]);
                            } else {
                                deviceChannelBO.setParentId(parentID);
                            }
                        } else {
                            if (StrUtil.isNotBlank(businessGroupID)) {
                                deviceChannelBO.setParentId(businessGroupID);
                            } else {
                                if (StrUtil.isNotBlank(deviceChannelBO.getCivilCode())) {
                                    deviceChannelBO.setParentId(deviceChannelBO.getCivilCode());
                                }
                            }
                        }
                        // 注册方式
                        if (StrUtil.isNotBlank(registerWay)) {
                            deviceChannelBO.setRegisterWay(Integer.valueOf(registerWay));
                        }

                        // 信令安全模式(可选)缺省为0; 0:不采用;2:S/MIME 签名方式;3:S/MIME加密签名同时采用方式;4:数字摘要方式
                        String safetyWay = XmlUtil.getText(element, "SafetyWay");
                        if (StrUtil.isNotBlank(safetyWay)) {
                            deviceChannelBO.setSafetyWay(Integer.parseInt(safetyWay));
                        }

                        // 证书序列号(有证书的设备必选)
                        String certNum = XmlUtil.getText(element, "CertNum");
                        if (StrUtil.isNotBlank(certNum)) {
                            deviceChannelBO.setCertNum(certNum);
                        }

                        // 证书有效标识(有证书的设备必选)缺省为0;证书有效标识:0:无效 1:有效
                        String certifiable = XmlUtil.getText(element, "Certifiable");
                        if (StrUtil.isNotBlank(certifiable)) {
                            deviceChannelBO.setCertifiable(Integer.parseInt(certifiable));
                        }

                        // 无效原因码(有证书且证书无效的设备必选)
                        String errCode = XmlUtil.getText(element, "ErrCode");
                        if (StrUtil.isNotBlank(errCode)) {
                            deviceChannelBO.setErrCode(Integer.parseInt(errCode));
                        }

                        // 证书终止有效期(有证书的设备必选)
                        String endTime = XmlUtil.getText(element, "EndTime");
                        if (StrUtil.isNotBlank(endTime)) {
                            deviceChannelBO.setEndTime(endTime);
                        }

                        // 设备/区域/系统IP地址
                        String ipAddress = XmlUtil.getText(element, "IPAddress");
                        if (StrUtil.isNotBlank(ipAddress)) {
                            deviceChannelBO.setIpAddress(ipAddress);
                        }

                        // 设备/区域/系统端口
                        String port = XmlUtil.getText(element, "Port");
                        if (StrUtil.isNotBlank(port)) {
                            deviceChannelBO.setPort(Integer.parseInt(port));
                        }

                        // 设备口令
                        String password = XmlUtil.getText(element, "Password");
                        if (StrUtil.isNotBlank(password)) {
                            deviceChannelBO.setPassword(password);
                        }

                        // 设备状态
                        String status = XmlUtil.getText(element, "Status");
                        if (StrUtil.isNotBlank(status)) {
                            // ONLINE OFFLINE HIKVISION DS-7716N-E4 NVR的兼容性处理
                            if (status.equals("ON") || status.equals("On") || status.equals("ONLINE") || status.equals("OK")) {
                                deviceChannelBO.setStatus(Boolean.TRUE);
                            }
                            if (status.equals("OFF") || status.equals("Off") || status.equals("OFFLINE")) {
                                deviceChannelBO.setStatus(Boolean.FALSE);
                            }
                        } else {
                            deviceChannelBO.setStatus(Boolean.TRUE);
                        }

                        // 经度
                        String longitude = XmlUtil.getText(element, "Longitude");
                        if (StrUtil.isNotBlank(longitude) && NumberUtil.isDouble(longitude)) {
                            deviceChannelBO.setLongitude(Double.parseDouble(longitude));
                        } else {
                            deviceChannelBO.setLongitude(0.00);
                        }

                        // 纬度
                        String latitude = XmlUtil.getText(element, "Latitude");
                        if (StrUtil.isNotBlank(latitude) && NumberUtil.isDouble(latitude)) {
                            deviceChannelBO.setLatitude(Double.parseDouble(latitude));
                        } else {
                            deviceChannelBO.setLatitude(0.00);
                        }

                        deviceChannelBO.setGpsTime(LocalDateTime.now());

                        // -摄像机类型扩展,标识摄像机类型:1-球机;2-半球;3-固定枪机;4-遥控枪机。当目录项为摄像机时可选
                        String ptzType = XmlUtil.getText(element, "PTZType");
                        if (StrUtil.isBlank(ptzType)) {
                            // 兼容INFO中的信息
                            Element info = element.element("Info");
                            String ptzTypeInfo = XmlUtil.getText(info, "PTZType");
                            if (StrUtil.isNotBlank(ptzTypeInfo)) {
                                deviceChannelBO.setPtzType(Integer.parseInt(ptzTypeInfo));
                            }
                        } else {
                            deviceChannelBO.setPtzType(Integer.parseInt(ptzType));
                        }

                        deviceChannelBO.setSecrecy(Integer.valueOf(secrecy));
                        break;
                }
            }

            if (Objects.nonNull(deviceChannelBO.getParentId()) && Objects.equals(deviceChannelBO.getParentId(), sipProperties.getId())) {
                deviceChannelBO.setParentId(null);
            }
            deviceChannelBOS.add(deviceChannelBO);
        }
        sipService.saveDeviceChannel(deviceChannelBOS, deviceBO.getDeviceId());
    }
}