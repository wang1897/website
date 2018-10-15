package com.aethercoder.core.service;

import com.aethercoder.core.entity.android.Android;

import java.util.List;

/**
 * @auther Guo Feiyan
 * @date 2017/9/26 上午11:14
 */
public interface AndroidService {

    List<Android> findAndroidAll();

    Android findAndroidById(Long id);

    Android findAndroidLatest();

    Android findIOSLatest();

    Android saveAndroid(Android android);

    Android updateAndroid(Android android);

    void deleteAndroid(Long id);

}
