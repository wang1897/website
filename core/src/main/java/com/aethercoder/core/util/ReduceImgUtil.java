package com.aethercoder.core.util;


import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

/**
 * @Author: YiShan Xiao
 * @Description:
 * @Date: Created in 2018/1/29
 * @modified By:
 */
public class ReduceImgUtil {
    /**
     * 采用指定宽度、高度或压缩比例 的方式对图片进行压缩
     * @param widthdist 压缩后图片宽度（当rate==null时，必传）
     * @param heightdist 压缩后图片高度（当rate==null时，必传）
     */
    public static BufferedImage reduceImg(MultipartFile file, int widthdist, int heightdist) {
//        File outfile = null;
        BufferedImage tag = null;
        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf('.'));
            String filename = System.currentTimeMillis() + ext;
//
//            File srcfile = new File(imgPath);
//            // 检查文件是否存在
//            if (!srcfile.exists()) {
//                return null;
//            }
//
//            outfile = new File(imgPath);
//            // 检查文件是否存在
//            if (!outfile.exists()) {
//                return null;

            // 开始读取文件并进行压缩
            Image src = ImageIO.read(file.getInputStream());
//            Image src = ImageIO.read(srcfile);
            tag = new BufferedImage(widthdist, heightdist, BufferedImage.TYPE_INT_RGB);
            tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist, Image.SCALE_SMOOTH), 0, 0, null);
//            ImageIO.write(tag,filename,outfile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
//        return outfile;
        return tag;
    }
}
