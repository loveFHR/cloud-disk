package org.disk.vo.pyl;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

@Data
public class GroupInfoVO  {
    /**
     * 部门id
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id;
    /**
     * 部门名字
     */
    private String groupName;
    /**
     * 部门所含人数
      */
    private Integer num;
}
