package com.aethercoder.core.service.impl;

import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.service.StorageService;
import com.aethercoder.core.util.ReduceImgUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by hepengfei on 2017/8/31.
 */
@Service
public class StorageServiceImpl implements StorageService {

    @Value( "${upload.ticketPath}" )
    private String ticketPath;

    @Value( "${upload.eventBannerPath}" )
    private String eventBannerPath;

    private static final int widthdist1 = 82;

    private static final int widthdist2 = 200;

    private static final int heightdist1 = 82;

    private static final int heightdist2 = 200;

    @Override
    public String saveFileHeader(MultipartFile file,String path) {
        BufferedImage file1 = ReduceImgUtil.reduceImg(file, widthdist1, heightdist1);
        BufferedImage file2 = ReduceImgUtil.reduceImg(file, widthdist2, heightdist2);

        String originalName = file.getOriginalFilename();
        String ext = originalName.substring(originalName.lastIndexOf('.'));
        String filename1 = System.currentTimeMillis() + ext;
//        String filename2 = System.currentTimeMillis() + "_B" + ext;

        String name = uploadPhoto(file1, filename1, path, true);
//        String name1 = uploadPhoto(file2, filename2, path, true);
//        String[] names = new String[2];
//        names[0] = name;
//        names[1] = name1;
        return name;
    }

    @Override
    public String saveFile(MultipartFile file,String path) {

        return uploadPhoto(file, path, false);
    }

    @Override
    public String saveFile(MultipartFile file,String path, boolean saveName) {
        return uploadPhoto(file,path, saveName);
    }

    @Override
    public String saveFile(MultipartFile file,String path, boolean saveName,Integer type) {
        //上传反馈截图
        if (type!= null && WalletConstants.SAVE_FILE_TYPE_TICKET.equals(type)){
            path = ticketPath;
        }else if (type!= null && WalletConstants.SAVE_FILE_TYPE_EVENT.equals(type)){
            path = eventBannerPath;
        }
        return uploadPhoto(file,path, saveName);
    }

    private String uploadPhoto(MultipartFile file,String path, boolean saveName){

        try {
            String originalName = file.getOriginalFilename();
            String ext = originalName.substring(originalName.lastIndexOf('.'));
            String filename = System.currentTimeMillis() + ext;
            if (saveName) {
                filename = originalName;
            }

            File dir = new File(path);
            if (!dir.exists()){
                dir.mkdirs();
            }

            File toFile = new File(path + File.separator + filename);
            File absFile = new File(toFile.getAbsolutePath());
            if (!absFile.exists()){
                absFile.createNewFile();
            }
            file.transferTo(absFile);

            return filename;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    private String uploadPhoto(BufferedImage image, String originalName, String path, boolean saveName){

        try {
            String ext = originalName.substring(originalName.lastIndexOf('.'));
            String filename = System.currentTimeMillis() + ext;
            if (saveName) {
                filename = originalName;
            }

            File dir = new File(path);
            if (!dir.exists()){
                dir.mkdirs();
            }

            File toFile = new File(path + File.separator + filename);
            ImageIO.write(image, ext.substring(1), toFile);
//            file.transferTo(absFile);

            return filename;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
