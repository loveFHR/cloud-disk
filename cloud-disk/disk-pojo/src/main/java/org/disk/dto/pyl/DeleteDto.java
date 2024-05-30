package org.disk.dto.pyl;

import lombok.Data;

import java.util.List;
@Data
public class DeleteDto {
    /**
     * 删除id列表
     */
    private List<Long> list;
    /**
     * 某个具体id
     */
    private Long id;
    /**
     * 某个名字
     */
    private String name;
}
