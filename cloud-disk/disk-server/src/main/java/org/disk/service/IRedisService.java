package org.disk.service;

import java.util.concurrent.TimeUnit;

public interface IRedisService {
    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    <T> void setCacheObject(final String key, final T value);

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    <T> void setCacheObject(final String key, final T value, final Long timeout, final TimeUnit timeUnit);

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @return Hash中的对象
     */
    <T> T getCacheValue(final String key);
}
