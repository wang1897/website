package com.aethercoder.core.service;

import java.util.List;

/**
 * Created by hepengfei on 24/12/2017.
 */
public interface RedisService {
    public boolean set(String key, String value);

    public String get(String key);

    public boolean expire(String key,long expire);

    public <T> boolean setList(String key ,List<T> list);

    public <T> List<T> getList(String key, Class<T> clz);

    public long lpush(String key,Object obj);

    public long rpush(String key,Object obj);

    public String lpop(String key);

    long delete(final String key);
}
