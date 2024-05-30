package org.disk.result;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 封装分页查询结果
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> implements Serializable {

    @ApiModelProperty(value = "总记录数")
    private long total; //总记录数

    @ApiModelProperty(value = "当前页数据集合")
    private List<T> records; //当前页数据集合

}
