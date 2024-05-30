package org.disk.dto.pyl;

import lombok.Data;

@Data
public class GroupQuery extends PageRequest{

    /**
     * 部门名字
     */
    private  String name;

}
