package com.htuozhou.wvp.business.bo;

import com.htuozhou.wvp.persistence.po.UserPO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/2/2
 */
@Data
public class UserBO {

    private Integer id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别（0、男 1、女）
     */
    private Integer sex;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    public static UserBO po2bo(UserPO po) {
        UserBO bo = new UserBO();
        BeanUtils.copyProperties(po, bo);

        return bo;
    }

    public UserPO bo2po() {
        UserPO po = new UserPO();
        BeanUtils.copyProperties(this, po);

        return po;
    }

}
