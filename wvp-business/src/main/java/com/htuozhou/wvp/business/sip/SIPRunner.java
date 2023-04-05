package com.htuozhou.wvp.business.sip;

import com.htuozhou.wvp.business.properties.DefaultProperties;
import com.htuozhou.wvp.business.properties.SIPProperties;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.SipStackImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sip.ListeningPoint;
import javax.sip.PeerUnavailableException;
import javax.sip.SipFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author hanzai
 * @date 2023/4/5
 */
@Component
@Slf4j
public class SIPRunner implements CommandLineRunner {

    @Autowired
    private SIPProperties sipProperties;

    @Autowired
    private ISIPProcessorObserver sipProcessorObserver;

    private SipFactory sipFactory;

    private final Map<String, SipProviderImpl> tcpSipProviderMap = new ConcurrentHashMap<>();
    private final Map<String, SipProviderImpl> udpSipProviderMap = new ConcurrentHashMap<>();

    @Override
    public void run(String... args) throws Exception {
        List<String> monitorIps = new ArrayList<>();
        // 使用逗号分割多个ip
        String separator = ",";
        if (sipProperties.getIp().indexOf(separator) > 0) {
            String[] split = sipProperties.getIp().split(separator);
            monitorIps.addAll(Arrays.asList(split));
        } else {
            monitorIps.add(sipProperties.getIp());
        }

        sipFactory = SipFactory.getInstance();
        sipFactory.setPathName("gov.nist");
        if (monitorIps.size() > 0) {
            for (String monitorIp : monitorIps) {
                addListeningPoint(monitorIp, sipProperties.getPort());
            }
        }
    }

    private void addListeningPoint(String monitorIp, int port) {
        SipStackImpl sipStack;
        try {
            sipStack = (SipStackImpl) sipFactory.createSipStack(DefaultProperties.getProperties(monitorIp, false));
        } catch (PeerUnavailableException e) {
            log.error("[Sip Ip:{}] 启动失败,请检查ip是否正确", monitorIp);
            return;
        }

        try {
            ListeningPoint tcpListeningPoint = sipStack.createListeningPoint(monitorIp, port, "TCP");
            SipProviderImpl tcpSipProvider = (SipProviderImpl) sipStack.createSipProvider(tcpListeningPoint);

            tcpSipProvider.setDialogErrorsAutomaticallyHandled();
            tcpSipProvider.addSipListener(sipProcessorObserver);
            tcpSipProviderMap.put(monitorIp, tcpSipProvider);

            log.info("[Sip Tcp Address:{}] 启动成功", monitorIp + "://" + port);
        } catch (Exception e) {
            log.error("[Sip Tcp Address:{}] 启动失败,请检查端口是否被占用", monitorIp + "://" + port);
        }

        try {
            ListeningPoint udpListeningPoint = sipStack.createListeningPoint(monitorIp, port, "UDP");

            SipProviderImpl udpSipProvider = (SipProviderImpl) sipStack.createSipProvider(udpListeningPoint);
            udpSipProvider.addSipListener(sipProcessorObserver);

            udpSipProviderMap.put(monitorIp, udpSipProvider);

            log.info("[Sip Udp Address:{}] 启动成功", monitorIp + "://" + port);
        } catch (Exception e) {
            log.error("[Sip Udp Address:{}] 启动失败,请检查端口是否被占用", monitorIp + "://" + port);
        }
    }
}
