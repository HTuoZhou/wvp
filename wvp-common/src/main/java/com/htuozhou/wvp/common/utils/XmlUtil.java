package com.htuozhou.wvp.common.utils;

import org.dom4j.Element;

/**
 * @author hanzai
 * @date 2023/4/15
 */
public class XmlUtil {

    public static String getText(Element em, String tag) {
        if (null == em) {
            return null;
        }
        Element e = em.element(tag);
        //
        return null == e ? null : e.getText().trim();
    }


}
