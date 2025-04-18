package org.bengo.entity.vo;

import lombok.Data;
import org.bengo.entity.vo.BasePage;

import java.util.List;

@Data
public class PageBean<T> {
    /**
     * 记录页数与大小
     */
    private BasePage curPageInfo;
    /**
     * 总记录数
     */
    private Long totalCount;
    /**
     * 总页数
     */
    private Long totalPage;
    /**
     * 每页查询到的数据的集合
     */
    private List<T> list;
}