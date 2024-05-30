package org.disk.vo.yls;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页的前端返回类
 */
@Data
@NoArgsConstructor
public class PageVO<T> {

    public PageVO(List<T> list, Page page) {
        this.list = list;
        this.current=page.getCurrent();
        this.total= page.getTotal();
        this.size= page.getSize();
    }

    @ApiModelProperty(value = "数据list")
    private List<T> list;

    @ApiModelProperty(value = "总记录条数")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long total;

    @ApiModelProperty(value = "当前页")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long current;

    @ApiModelProperty(value = "页大小")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long size;
}