package com.aethercoder.core.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * Created by hepengfei on 2017/8/31.
 */
public interface StorageService {

    String saveFileHeader(MultipartFile file,String path);

    String saveFile(MultipartFile file,String path);

    String saveFile(MultipartFile file,String path, boolean saveName);

    String saveFile(MultipartFile file,String path, boolean saveName,Integer type);
}
