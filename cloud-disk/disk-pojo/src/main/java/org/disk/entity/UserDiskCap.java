package org.disk.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
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
@TableName("user_disk_cap")
@ApiModel(value="UserDiskCap对象", description="")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDiskCap implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "序号，主键，自增")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id，映射user_info.id，逻辑外键")
    private Long userId;

    @ApiModelProperty(value = "用户全部容量，单位MB")
    private Long allCap;

    @ApiModelProperty(value = "已使用容量，单位MB")
    private Long usedCap;


}
