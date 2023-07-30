package com.htuozhou.wvp.common.utils;

import com.htuozhou.wvp.common.constant.CommonConstant;
import com.htuozhou.wvp.common.constant.EasyExcelConstant;
import com.htuozhou.wvp.common.dict.BaseDict;
import com.htuozhou.wvp.common.service.I18nService;

import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * @author hanzai
 * @date 2023/2/3
 */
public class CommonUtil {

    /**
     * 设置excel响应
     * @param response
     * @param fileName
     */
    public static void setResponse(HttpServletResponse response, String fileName) {
        response.setContentType(EasyExcelConstant.EXCEL_CONTENT_TYPE);
        // 下载EXCEL
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + EasyExcelConstant.EXCEL_DOWNLOAD_FORMAT);
        response.setCharacterEncoding(EasyExcelConstant.CHARSET_UTF);
    }

    /***
     * 获取国际化的值
     * @param dict
     * @param i18nService
     * @return
     */
    public static String getI18nMsg(BaseDict dict, I18nService i18nService) {
        return i18nService.getMessage(dict.getI18nKey(), dict.getDefaultValue());
    }

    public static String localIP() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                String name = intf.getName();
                if (!name.contains("docker") && !name.contains("lo") && !name.startsWith("veth") && !name.startsWith("cali")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()) {
                            String ipaddress = inetAddress.getHostAddress().toString();
                            if (!ipaddress.contains("::") && !ipaddress.contains("0:0:") && !ipaddress.contains("fe80")) {
                                return ipaddress;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonConstant.DEFAULT_LOCAL_IP;
    }
}
