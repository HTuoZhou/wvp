package com.htuozhou.wvp.common.dict;

/**
 * @author hanzai
 * @date 2023/2/3
 */
public interface BaseDict {
    String DICT_I18N_KEY_FMT = "random.dict.%s";

    /**
     * 字典编码
     *
     * @return
     */
    String getCode();

    /**
     * 字典值
     *
     * @return
     */
    String getDefaultValue();

    /**
     * 国际化的KEY
     *
     * @return
     */
    default String getI18nKey() {
        return String.format(DICT_I18N_KEY_FMT, getCode());
    }

}
