package org.disk.dto.pyl;

import lombok.Data;

@Data
public class UserQuery extends PageRequest{

    /**
     * 用户名字
     */
    private String name;

    /**
     * 部门名字
     */
    private  String groupName;

    /**
     * 部门id
     */
    private Long groupId;
}
