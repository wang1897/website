package com.aethercoder.core.controller;

import com.aethercoder.core.entity.error.QtumTxError;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.service.error.QtumTxErrorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by hepengfei on 22/03/2018.
 */
@RestController
@RequestMapping("errorUpload")
@Api(tags = "errorUpload", description = "错误数据上报接口")
public class ErrorUploadController {

    @Autowired
    private QtumTxErrorService qtumTxErrorService;

    @ApiOperation(value = "qtum交易失败上报", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/qtum", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public QtumTxError uploadQtumError(@RequestBody QtumTxError qtumTxError, @RequestHeader HttpHeaders headers) {
        List<String> deviceTypes = headers.get("device-type");
        String deviceType = null;
        if (deviceTypes != null && !deviceTypes.isEmpty()) {
            deviceType = deviceTypes.get(0);
        }
        List<String> appVersions = headers.get("app-version");
        String appVersion = null;
        if (appVersions != null && !appVersions.isEmpty()) {
            appVersion = appVersions.get(0);
        }

        List<String> deviceSysVersions = headers.get("device-sys-version");
        String deviceSysVersion = null;
        if (deviceSysVersions != null && !deviceSysVersions.isEmpty()) {
            deviceSysVersion = deviceSysVersions.get(0);
        }

        List<String> deviceModels = headers.get("device-model");
        String deviceModel = null;
        if (deviceModels != null && !deviceModels.isEmpty()) {
            deviceModel = deviceModels.get(0);
        }
        qtumTxError.setDeviceType(deviceType);
        qtumTxError.setAppVersion(appVersion);
        qtumTxError.setDeviceVersion(deviceSysVersion);
        qtumTxError.setDeviceModel(deviceModel);
        return qtumTxErrorService.saveError(qtumTxError);
    }
}
