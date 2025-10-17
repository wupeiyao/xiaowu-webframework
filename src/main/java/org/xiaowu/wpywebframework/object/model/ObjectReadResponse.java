package org.xiaowu.wpywebframework.object.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * @author wupy
 */
@Getter
@Setter
public class ObjectReadResponse extends ObjectResponse {

    /**
     * 文件流
     */
    @Schema(description = "文件流")
    private byte[] bytes;
}
