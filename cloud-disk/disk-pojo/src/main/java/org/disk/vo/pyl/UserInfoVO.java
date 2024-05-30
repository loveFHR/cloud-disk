package org.disk.vo.pyl;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class UserInfoVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;

    private String userName;
    /**
     * 头像
     */
    private String avatar;
    /**
     * 手机号
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long phone;

    private String email;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer status;
    /**
     * 部门Id
     */
    private Long groupId;
    /**
     * 部门名字
     */
    private String groupName;
    /**
     * 用户总容量
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long all_cap;
    /**
     * 用户已用容量
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long used_cap;

}
