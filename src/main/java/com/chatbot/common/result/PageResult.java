package com.chatbot.common.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页返回结果
 * @author Diamond
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 计算总页数
     */
    public Integer getTotalPages() {
        if (pageSize == null || pageSize == 0) {
            return 0;
        }
        return (int) Math.ceil((double) total / pageSize);
    }
}
