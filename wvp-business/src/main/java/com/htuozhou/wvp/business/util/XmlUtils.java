package com.htuozhou.wvp.business.util;

import org.dom4j.Element;

/**
 * @author hanzai
 * @date 2023/4/15
 */
public class XmlUtils {

    public static String getText(Element em, String tag) {
        if (null == em) {
            return null;
        }
        Element e = em.element(tag);
        //
        return null == e ? null : e.getText().trim();
    }


}
