package com.htuozhou.wvp.business.sip.request.impl.message.impl.response;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.htuozhou.wvp.business.bo.DeviceBO;
import com.htuozhou.wvp.business.bo.DeviceChannelBO;
import com.htuozhou.wvp.business.enumeration.ChannelTypeEnum;
import com.htuozhou.wvp.business.service.ISIPService;
import com.htuozhou.wvp.business.sip.SIPSender;
import com.htuozhou.wvp.business.sip.request.AbstractSIPRequestProcessor;
import com.htuozhou.wvp.business.sip.request.impl.message.IMessageHandler;
import com.htuozhou.wvp.common.utils.XmlUtil;
import gov.nist.javax.sip.RequestEventExt;
import gov.nist.javax.sip.message.SIPRequest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
        // log.info("[SIP MESSAGE RESPONSE] 收到 [SIP ADDRESS:{} CATALOG] 请求",requestAddress);
        log.info("[SIP MESSAGE RESPONSE] 收到 [SIP ADDRESS:{} CATALOG] 请求，请求内容\n{}", requestAddress, request);

        Response response = getMessageFactory().createResponse(Response.OK, request);
        sipSender.transmitRequest(request.getLocalAddress().getHostAddress(), response);
        // log.info("[SIP MESSAGE RESPONSE] [SIP ADDRESS:{} CATALOG] 回复200",requestAddress);
        log.info("[SIP MESSAGE RESPONSE] [SIP ADDRESS:{} CATALOG] 回复200，回复内容\n{}", requestAddress, response);

        List<DeviceChannelBO> deviceChannelBOS = new ArrayList<>();
        Iterator<Element> deviceListIterator = rootElement.element("DeviceList").elementIterator();
        while (deviceListIterator.hasNext()) {
            Element element = deviceListIterator.next();

            DeviceChannelBO deviceChannelBO = new DeviceChannelBO();
            String channelId = XmlUtil.getText(element, "DeviceID");
            deviceChannelBO.setDeviceId(deviceBO.getDeviceId());
            deviceChannelBO.setChannelId(channelId);
            deviceChannelBO.setName(XmlUtil.getText(element, "Name"));
            String civilCode = XmlUtil.getText(element, "CivilCode");
            deviceChannelBO.setCivilCode(civilCode);
            ChannelTypeEnum channelType = ChannelTypeEnum.Other;
            if (channelId.length() <= 8) {
                channelType = ChannelTypeEnum.CivilCode;
                deviceChannelBO.setHasAudio(0);
            } else if (channelId.length() == 20) {
                int code = Integer.parseInt(channelId.substring(10, 13));
                switch (code) {
                    case 215:
                        channelType = ChannelTypeEnum.BusinessGroup;
                        deviceChannelBO.setHasAudio(0);
                        break;
                    case 216:
                        channelType = ChannelTypeEnum.VirtualOrganization;
                        deviceChannelBO.setHasAudio(0);
                        break;
                    case 136:
                    case 137:
                    case 138:
                        deviceChannelBO.setHasAudio(1);
                        break;
                    default:
                        deviceChannelBO.setHasAudio(0);
                        break;
                }
            }

            if (StrUtil.isBlank(civilCode) && Objects.equals(channelType, ChannelTypeEnum.CivilCode)) {
                deviceChannelBO.setParental(1);
                // 行政区划如果没有传递具体值，则推测一个
                if (channelId.length() > 2) {
                    deviceChannelBO.setCivilCode(channelId.substring(0, channelId.length() - 2));
                }
            }
            if (Objects.equals(channelType, ChannelTypeEnum.CivilCode)) {
                // 行政区划其他字段没必要识别了，默认在线即可
                deviceChannelBO.setStatus(1);
                deviceChannelBO.setParental(1);
            }

            String parentId = XmlUtil.getText(element, "ParentID");
            String businessGroupId = XmlUtil.getText(element, "BusinessGroupID");
            if (StrUtil.isNotBlank(parentId)) {
                if (parentId.contains("/")) {
                    String lastParentId = parentId.substring(parentId.lastIndexOf("/") + 1);
                    if (StrUtil.isBlank(businessGroupId)) {
                        businessGroupId = parentId.substring(0, parentId.indexOf("/"));
                    }
                    deviceChannelBO.setParentId(lastParentId);
                } else {
                    deviceChannelBO.setParentId(parentId);
                }
                // 兼容设备通道信息中自己为自己父节点的情况
                if (Objects.equals(deviceChannelBO.getParentId(), deviceChannelBO.getChannelId())) {
                    deviceChannelBO.setParentId(StrUtil.EMPTY);
                }
            }
            deviceChannelBO.setGroupId(businessGroupId);
            if (Objects.equals(channelType, ChannelTypeEnum.BusinessGroup) || Objects.equals(channelType, ChannelTypeEnum.VirtualOrganization)) {
                // 业务分组和虚拟组织 其他字段没必要识别了，默认在线即可
                deviceChannelBO.setStatus(1);
                deviceChannelBO.setParental(1);
            }

            String status = XmlUtil.getText(element, "Status");
            // ONLINE OFFLINE HIKVISION DS-7716N-E4 NVR的兼容性处理
            if (status.equals("ON") || status.equals("On") || status.equals("ONLINE") || status.equals("OK")) {
                deviceChannelBO.setStatus(1);
            }
            if (status.equals("OFF") || status.equals("Off") || status.equals("OFFLINE")) {
                deviceChannelBO.setStatus(0);
            }
            // 识别自带的目录标识
            String parental = XmlUtil.getText(element, "Parental");
            // 由于海康会错误的发送65535作为这里的取值,所以这里除非是0否则认为是1
            if (StrUtil.isNotBlank(parental) && parental.length() == 1 && Integer.parseInt(parental) == 0) {
                deviceChannelBO.setParental(0);
            } else {
                deviceChannelBO.setParental(1);
            }

            deviceChannelBO.setManufacturer(XmlUtil.getText(element, "Manufacturer"));
            deviceChannelBO.setModel(XmlUtil.getText(element, "Model"));
            deviceChannelBO.setOwner(XmlUtil.getText(element, "Owner"));
            deviceChannelBO.setCertNum(XmlUtil.getText(element, "CertNum"));
            deviceChannelBO.setBlock(XmlUtil.getText(element, "Block"));
            deviceChannelBO.setAddress(XmlUtil.getText(element, "Address"));
            deviceChannelBO.setPassword(XmlUtil.getText(element, "Password"));

            String safetyWay = XmlUtil.getText(element, "SafetyWay");
            if (StrUtil.isBlank(safetyWay)) {
                deviceChannelBO.setSafetyWay(0);
            } else {
                deviceChannelBO.setSafetyWay(Integer.parseInt(safetyWay));
            }

            String registerWay = XmlUtil.getText(element, "RegisterWay");
            if (StrUtil.isBlank(registerWay)) {
                deviceChannelBO.setRegisterWay(1);
            } else {
                deviceChannelBO.setRegisterWay(Integer.parseInt(registerWay));
            }

            if (StrUtil.isBlank(XmlUtil.getText(element, "Certifiable"))) {
                deviceChannelBO.setCertifiable(0);
            } else {
                deviceChannelBO.setCertifiable(Integer.parseInt(XmlUtil.getText(element, "Certifiable")));
            }

            if (StrUtil.isBlank(XmlUtil.getText(element, "ErrCode"))) {
                deviceChannelBO.setErrCode(0);
            } else {
                deviceChannelBO.setErrCode(Integer.parseInt(XmlUtil.getText(element, "ErrCode")));
            }

            deviceChannelBO.setEndTime(XmlUtil.getText(element, "EndTime"));
            deviceChannelBO.setSecrecy(Integer.valueOf(XmlUtil.getText(element, "Secrecy")));
            deviceChannelBO.setIpAddress(XmlUtil.getText(element, "IPAddress"));
            if (StrUtil.isBlank(XmlUtil.getText(element, "Port"))) {
                deviceChannelBO.setPort(0);
            } else {
                deviceChannelBO.setPort(Integer.parseInt(XmlUtil.getText(element, "Port")));
            }

            String longitude = XmlUtil.getText(element, "Longitude");
            if (StrUtil.isNotBlank(longitude) && NumberUtil.isDouble(longitude)) {
                deviceChannelBO.setLongitude(Double.parseDouble(longitude));
            } else {
                deviceChannelBO.setLongitude(0.00);
            }
            String latitude = XmlUtil.getText(element, "Latitude");
            if (StrUtil.isNotBlank(latitude) && NumberUtil.isDouble(latitude)) {
                deviceChannelBO.setLatitude(Double.parseDouble(latitude));
            } else {
                deviceChannelBO.setLatitude(0.00);
            }
            deviceChannelBO.setGpsTime(LocalDateTime.now());

            if (StrUtil.isBlank(XmlUtil.getText(element, "PTZType"))) {
                //兼容INFO中的信息
                Element info = element.element("Info");
                if (StrUtil.isBlank((XmlUtil.getText(info, "PTZType")))) {
                    deviceChannelBO.setPtzType(0);
                } else {
                    deviceChannelBO.setPtzType(Integer.parseInt(XmlUtil.getText(info, "PTZType")));
                }
            } else {
                deviceChannelBO.setPtzType(Integer.parseInt(XmlUtil.getText(element, "PTZType")));
            }

            deviceChannelBOS.add(deviceChannelBO);
        }

        sipService.saveBatchDeviceChannel(deviceChannelBOS,deviceBO.getDeviceId());;
    }
}