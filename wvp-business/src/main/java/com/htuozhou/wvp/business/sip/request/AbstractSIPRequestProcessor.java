package com.htuozhou.wvp.business.sip.request;

import org.apache.commons.lang3.ArrayUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import javax.sip.RequestEvent;
import javax.sip.SipFactory;
import javax.sip.header.HeaderFactory;
import javax.sip.message.MessageFactory;
import javax.sip.message.Request;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author hanzai
 * @date 2023/4/5
 */
public  abstract class AbstractSIPRequestProcessor {

    public HeaderFactory getHeaderFactory() throws Exception{
        return SipFactory.getInstance().createHeaderFactory();
    }

    public MessageFactory getMessageFactory() throws Exception{
        return SipFactory.getInstance().createMessageFactory();
    }

    public Element getRootElement(RequestEvent requestEvent) throws Exception {
        return getRootElement(requestEvent, "gb2312");
    }

    public Element getRootElement(RequestEvent requestEvent, String charset) throws Exception {
        if (charset == null) {
            charset = "gb2312";
        }
        Request request = requestEvent.getRequest();
        SAXReader reader = new SAXReader();
        reader.setEncoding(charset);
        // 对海康出现的未转义字符做处理。
        String[] destStrArray = new String[]{"&lt;","&gt;","&amp;","&apos;","&quot;"};
        char despChar = '&'; // 或许可扩展兼容其他字符
        byte destBye = (byte) despChar;
        List<Byte> result = new ArrayList<>();
        byte[] rawContent = request.getRawContent();
        if (rawContent == null) {
            return null;
        }
        for (int i = 0; i < rawContent.length; i++) {
            if (rawContent[i] == destBye) {
                boolean resul = false;
                for (String destStr : destStrArray) {
                    if (i + destStr.length() <= rawContent.length) {
                        byte[] bytes = Arrays.copyOfRange(rawContent, i, i + destStr.length());
                        resul = resul || (Arrays.equals(bytes,destStr.getBytes()));
                    }
                }
                if (resul) {
                    result.add(rawContent[i]);
                }
            }else {
                result.add(rawContent[i]);
            }
        }
        Byte[] bytes = new Byte[0];
        byte[] bytesResult = ArrayUtils.toPrimitive(result.toArray(bytes));

        Document xml = reader.read(new ByteArrayInputStream(bytesResult));
        return xml.getRootElement();
    }



}
