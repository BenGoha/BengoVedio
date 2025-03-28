package org.bengo.service.audit;

import java.util.function.Supplier;

/**
 * @description: 用于处理审核
 * @author wuGuanRen
 */
public interface AuditService<T,R> {

    /**
     *  审核规范
     * @param task
     * @return
     */
    R audit(T task);
}
