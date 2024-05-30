package org.disk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author author
 * @since 2023-12-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("group_table")
@ApiModel(value="GroupTable对象", description="")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupTable implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号，主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "部门名字")
    private String groupName;

    @ApiModelProperty(value = "创建时间")
    private LocalDateTime createTime;


}
