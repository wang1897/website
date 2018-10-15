package com.aethercoder.core.controller;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.basic.exception.FileNotExistException;
import com.aethercoder.basic.exception.entity.ReqExceptionEntity;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.service.CustomerService;
import com.aethercoder.core.util.OverlapImageUtil;
import com.aethercoder.core.service.StorageService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import freemarker.core.LocalContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * Created by hepengfei on 2017/8/31.
 */

@RestController
@RequestMapping( "file" )
@Api( tags = "file", description = "头像／图标接口管理" )
public class FileController {

    private static Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private CustomerService customerService;

    @Value("${upload.headerPath}")
    private String headerPath;

    @Value("${upload.iconPath}")
    private String iconPath;

    @Value("${upload.pkgPath}")
    private String pkgPath;

    @Value("${upload.IDCardPath}")
    private String IDCardPath;

    @Value("${upload.ticketPath}")
    private String ticketPath;

    @Value("${upload.eventBannerPath}")
    private String eventBannerPath;


    /**
     * Qbao 上传头像
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "Qbao 上传头像", notes = "成功时返回文件名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/upload/header", method = RequestMethod.POST, headers = ("content-type=multipart/*"))
    public String uploadHeader(MultipartFile file) {

        logger.info("/upload/header");
        String filename = storageService.saveFileHeader(file, headerPath);
        return filename;
    }

    /**
     * Qbao 上传icon图标
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "Qbao 上传icon图标", notes = "成功时返回文件名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/upload/icon", method = RequestMethod.POST, headers = ("content-type=multipart/*"))
    public String uploadIcon(@RequestPart(required = true) MultipartFile file) {
        logger.info("/upload/icon");
        String filename = storageService.saveFile(file, iconPath);
        return filename;
    }


    /**
     * Qbao 上传身份证图片
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "Qbao 上传身份证图片", notes = "成功时返回文件名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/upload/IdCardPhoto", method = RequestMethod.POST, headers = ("content-type=multipart/*"))
    public String uploadIdCardPhoto(@RequestPart(required = true) MultipartFile file) {
        logger.info("/upload/IdCardPhoto");
        String filename = storageService.saveFile(file, IDCardPath);
        return filename;
    }

    /**
     * Qbao 上传安卓版本二维码
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "Qbao 上传安卓版本二维码", notes = "成功时返回文件名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/upload/pkg", method = RequestMethod.POST, headers = ("content-type=multipart/*"))
    public String uploadAndroid(@RequestPart(required = true) MultipartFile file) {
        logger.info("/upload/pkg");
        String filename = storageService.saveFile(file, pkgPath, true);
        return filename;

    }

    /**
     * Qbao 上传活动banner
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "Qbao 上传活动banner", notes = "成功时返回文件名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/upload/eventBanner", method = RequestMethod.POST, headers = ("content-type=multipart/*"))
    public String uploadEventBanner(@RequestPart(required = true) MultipartFile file) {
        logger.info("/upload/eventBanner");
        String filename = storageService.saveFile(file, eventBannerPath, true);
        return filename;

    }

    /**
     * Qbao 上传图片
     *
     * @param file
     * @return
     */
    @ApiOperation(value = "Qbao 上传图片", notes = "type=1 反馈问题截图 成功时返回文件名称")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/upload/image", method = RequestMethod.POST, headers = ("content-type=multipart/*"))
    public String uploadImage(@RequestPart(required = true) MultipartFile file, Integer type) {
        logger.info("/upload/image");
        org.springframework.util.Assert.notNull(type, "type not null");
        String filename = storageService.saveFile(file, pkgPath, false, type);
        return filename;

    }

    /**
     * Qbao 下载文件(根据类型区分)
     *
     * @param filename
     * @return
     */
    @ApiOperation(value = "Qbao 下载文件", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "downloadFile", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getPicket(@RequestParam String filename, @RequestParam Integer type) {
        logger.info("downloadFile");
        return getFile(filename, type);
    }

    private ResponseEntity<InputStreamResource> getFile(String filename, Integer type) {
        String path = iconPath;
        if (WalletConstants.SAVE_FILE_TYPE_EVENT.equals(type)) {
            path = eventBannerPath;
        }
        return getfile(filename, path, path);
    }

    /**
     * Qbao 下载反馈截图
     *
     * @param filename
     * @return
     */
    @ApiOperation(value = "Qbao 下载反馈截图", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "download/ticket/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getPicket(@PathVariable String filename) {
        logger.info("download/ticket/{filename:.+}");
        return getfile(filename, ticketPath, WalletConstants.DEFAULT_TICKET_PATH);
    }

    /**
     * Qbao 下载头像
     *
     * @param filename
     * @return
     */
    @ApiOperation(value = "Qbao 下载头像", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "download/header/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getHeader(@PathVariable String filename) {
        logger.info("download/header/{filename:.+}");
        return getfile(filename, headerPath, WalletConstants.DEFAULT_HEADER_PATH);
    }

    /**
     * Qbao 下载icon图标
     *
     * @param filename
     * @return
     */
    @ApiOperation(value = "Qbao 下载icon图标", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Account.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "download/icon/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getIcon(@PathVariable String filename) {
        logger.info("download/icon/{filename:.+}");
        return getfile(filename, iconPath, WalletConstants.DEFAULT_ICON_PATH);
    }

    /**
     * Qbao 下载安卓版本二维码
     *
     * @param filename
     * @return
     */
    @ApiOperation(value = "Qbao 下载安卓版本二维码", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = InputStreamResource.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "download/pkg/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getPkg(@PathVariable String filename) {
        logger.info("download/pkg/{filename:.+}");
        return getfile(filename, pkgPath, WalletConstants.DEFAULT_PKG_PATH);
    }

    /**
     * Qbao 下载二维码
     *
     * //@param content QR内容
     * //@param width   图片宽度
     * //@param height  图片高度
     * @return
     */
    @ApiOperation(value = "Qbao 下载二维码", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = InputStreamResource.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "download/qrcode/{filename:.+}", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> getQrCode(@PathVariable String filename) {
        logger.info("download/qrcode/{filename:.+}");
        String language = LocaleContextHolder.getLocale().getLanguage();

        String qrCodePath = iconPath + File.separator + filename;

        String qrCodeBgPath = "";
        if (language.equals(WalletConstants.LANGUAGE_TYPE_ZH)){
            qrCodeBgPath = "qrcode_bg_zh.png";
        }else if (language.equals(WalletConstants.LANGUAGE_TYPE_KO)){
            qrCodeBgPath = "qrcode_bg_ko.png";
        }else {
            qrCodeBgPath = "qrcode_bg_en.png";
        }
        return mergeImage(qrCodeBgPath, qrCodePath);
    }

    /**
     * Qbao 下载身份证图片
     *
     * @param filename
     * @return
     */
    @ApiOperation( value = "Qbao 下载身份证图片", notes = "" )
    @ApiResponses( value = {
            @ApiResponse( code = 200, message = "成功", response = Account.class ),
            @ApiResponse( code = 400, message = "输入不正确" ),
            @ApiResponse( code = 401, message = "没有权限访问" ),
            @ApiResponse( code = 500, message = "系统错误" )
    }
    )
    @RequestMapping( value = "download/IDCard/{filename:.+}", method = RequestMethod.GET )
    public ResponseEntity<InputStreamResource> getIDCard(@PathVariable String filename) {
        logger.info("download/IDCard/{filename:.+}");
        return getfile(filename, IDCardPath, WalletConstants.DEFAULT_IDCARD_PATH);
    }

    private static ResponseEntity<InputStreamResource> mergeImage(String bgPath, String fgPath) {
        logger.info("getfile");

        InputStream isbg = FileController.class.getClassLoader().getResourceAsStream(bgPath);
//        InputStream isfg = FileController.class.getClassLoader().getResourceAsStream(fgPath);


        File file = null;

        HttpHeaders respHeaders = new HttpHeaders();
        String ext = bgPath.substring(bgPath.lastIndexOf('.') + 1);
        if (ext.toLowerCase().equals("png")) {
            respHeaders.setContentType(MediaType.IMAGE_PNG);
        } else if (ext.toLowerCase().equals("gif")) {
            respHeaders.setContentType(MediaType.IMAGE_GIF);
        } else if (ext.toLowerCase().equals("jpg")) {
            respHeaders.setContentType(MediaType.IMAGE_JPEG);
        }
            ByteArrayOutputStream os = OverlapImageUtil.overlapImage(isbg, fgPath, ext);
//            ByteArrayOutputStream os = OverlapImageUtil.overlapImage(isbg, isfg, ext);

            InputStream is = new ByteArrayInputStream(os.toByteArray());
            InputStreamResource isr = new InputStreamResource(is);
            return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
    }

    private static ResponseEntity<InputStreamResource> getfile(String filename, String path, String defaultPath) {
        logger.info("getfile");
        if (filename.contains("/")) {
            throw new AppException(ErrorCode.PATH_ERROR);
        }
        String localFileName = null;
        File file = null;
        if (filename.equals(WalletConstants.DEFAULT_HEADER)) {
            localFileName = defaultPath + File.separator + WalletConstants.DEFAULT_HEADER_NAME;
            InputStream is = FileController.class.getClassLoader().getResourceAsStream(localFileName);
            HttpHeaders respHeaders = new HttpHeaders();
            respHeaders.setContentType(MediaType.IMAGE_PNG);

            return new ResponseEntity<InputStreamResource>(new InputStreamResource(is), respHeaders, HttpStatus.OK);

        }

        localFileName = path + File.separator + filename;
        file = new File(localFileName);

        if (!file.exists()) {
            throw new FileNotExistException(filename);
        }

        HttpHeaders respHeaders = new HttpHeaders();
        String ext = filename.substring(filename.lastIndexOf('.') + 1);
        if (ext.toLowerCase().equals("png")) {
            respHeaders.setContentType(MediaType.IMAGE_PNG);
        } else if (ext.toLowerCase().equals("gif")) {
            respHeaders.setContentType(MediaType.IMAGE_GIF);
        } else if (ext.toLowerCase().equals("jpg")) {
            respHeaders.setContentType(MediaType.IMAGE_JPEG);
        } else {
            respHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            respHeaders.setContentDispositionFormData("attachment", filename);
        }
        respHeaders.setContentLength(file.length());

        try {
            InputStreamResource isr = new InputStreamResource(new FileInputStream(file));
            return new ResponseEntity<InputStreamResource>(isr, respHeaders, HttpStatus.OK);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
