package com.htuozhou.wvp.webapi.vo;

import com.htuozhou.wvp.business.bo.UserBO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

/**
 * @author hanzai
 * @date 2023/2/2
 */
@Data
public class UserVO{

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

    public UserBO vo2bo() {
        UserBO bo = new UserBO();
        BeanUtils.copyProperties(this,bo);

        return bo;
    }

    public static UserVO bo2vo(UserBO bo) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(bo,vo);

        return vo;
    }

}
