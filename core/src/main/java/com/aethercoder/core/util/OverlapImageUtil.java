package com.aethercoder.core.util;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * @Author: Xiao YiShan
 * @Description:
 * @Date: Created in 2018/3/30
 * @modified By:
 */
public class OverlapImageUtil {

    //小图片贴到大图片形成一张图(合成)
    public static final ByteArrayOutputStream overlapImage(InputStream bgPath, String smallPath, String fileType) {
//    public static final ByteArrayOutputStream overlapImage(InputStream bgPath, InputStream smallPath, String fileType) {
        try {
            BufferedImage big = ImageIO.read(bgPath);

            BufferedImage small = ImageIO.read(new File(smallPath));
//            BufferedImage small = ImageIO.read(smallPath);
            BufferedImage newImage = new BufferedImage(200, 200, small.getType());

            newImage = resize(small, 200, 200);

            Graphics2D g = big.createGraphics();
            g.drawImage(newImage, 42, 150, newImage.getWidth(), newImage.getHeight(), null);
            g.dispose();
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(big, fileType, os);
            return os;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static final BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, image.getType());

        Graphics g1 = newImage.getGraphics();
        g1.drawImage(image, 0, 0, width, height, null);
        g1.dispose();
        return newImage;
    }

    public static void main(String[] args){

    }
}
