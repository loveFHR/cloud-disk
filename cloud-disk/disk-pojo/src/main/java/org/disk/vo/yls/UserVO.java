package org.disk.vo.yls;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserVO implements Serializable {
    private String username;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long userId;
}
