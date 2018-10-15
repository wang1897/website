package com.aethercoder.core.controller;

import com.aethercoder.basic.exception.AppException;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.entity.wallet.Contract;
import com.aethercoder.core.entity.wallet.CurrencyRate;
import com.aethercoder.core.service.ContractService;
import com.aethercoder.core.service.CurrencyRateService;
import com.aethercoder.core.service.VerifyIOSService;
import com.aethercoder.foundation.exception.entity.ErrorCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("contract")
@Api(tags = "contract", description = "合约接口管理")
public class ContractController {

    private static Logger logger = LoggerFactory.getLogger(ContractController.class);

    @Autowired
    private ContractService contractService;

    @Autowired
    private VerifyIOSService verifyIOSService;

    @Autowired
    private CurrencyRateService currencyRateService;

    @Autowired
    private DiscoveryClient discoveryClient;


    /***
     * Qbao 查询所有合约
     * @return
     */
    @ApiOperation(value = "Qbao 查询所有合约", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findContractAll", method = RequestMethod.GET, produces = "application/json")
    public List<Contract> findContractAll(Integer type) {

        logger.info("findContractAll");

        if(null == type){type = WalletConstants.CONTRACT_QTUM_TYPE;}
        List<Contract> contracts = new ArrayList<Contract>();
//        boolean isVerifyIOS = verifyIOSService.isVerifyForIOS(request);
//        if (isVerifyIOS) {
//            contracts.add(contractService.findContractAndIsDeleteIsFalseByName(WalletConstants.QBT_TOKEN_NAME));
//        } else {
            contracts = contractService.findContractAllForIOS(type);
//        }
        return contracts;
    }

    /***
     * Qbao_后台 查询所有没有删除的合约
     * @return
     */
    @ApiOperation(value = "Qbao_后台 查询所有没有删除的合约", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findAllAvailableContracts", method = RequestMethod.GET, produces = "application/json")
    public List<Contract> findAllAvailableContracts(Integer type) {
        logger.info("findAllAvailableContracts");
        if(null == type){type = WalletConstants.CONTRACT_QTUM_TYPE;}
        return contractService.findAllAvailableContracts(type);
     }

    /***
     * Qbao 查询所有合约
     * @return
     */
    @ApiOperation(value = "Qbao 查询所有合约", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findContractsAll", method = RequestMethod.GET, produces = "application/json")
    public List<Contract> findContractsAll() {
        String adminName = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest().getHeader("admin");
        logger.info("findContractsAll");
//        throw new AppException(ErrorCode.CUSTOMER_LOGIN_RESET_PWD_ERROR_CODE);
        return contractService.findContractsAll();
//        restTemplate.getForObject("http://qbao-monitor/monitor/test", String.class);

    }

     /***
     * Qbao_后台 查询所有合约
     * @return
     */
    @ApiOperation(value = "Qbao_后台 查询所有合约", notes = "返回List<contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findContractsAllByAdmin", method = RequestMethod.GET, produces = "application/json")
    public Page<Contract> findContractsAllByAdmin(Integer page, Integer size, String name, Boolean isShow, Boolean isDelete,Integer type) {
        logger.info("findContractsAll");
        if(null == page){page = WalletConstants.DEFAULT_PAGE;}
        if(null == size){size = WalletConstants.DEFAULT_PAGE_SIZE;}
        return contractService.findContractsAll( page,  size,  name, isShow, isDelete,type);
    }

    /***
     * Qbao 根据合约名称查询合约地址
     * @return
     */
    @ApiOperation(value = "Qbao 根据合约名称查询合约地址", notes = "返回该合约对象 Contract")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findContractByName", method = RequestMethod.GET, produces = "application/json")
    public Contract findContractByName(@RequestParam("name") String name, Integer type) {
        logger.info("findContractByName");
        if (type==null){
            type = WalletConstants.CONTRACT_QTUM_TYPE;
        }
        return contractService.findContractByName(name,type);
    }

    /***
     * Qbao 根据id查询合约地址
     * @return
     */
    @ApiOperation(value = "Qbao 根据id查询合约地址", notes = "返回该合约对象Contract")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/findContractById", method = RequestMethod.GET, produces = "application/json")
    public Contract findContractById(@RequestParam("id") Long id) {
        logger.info("/admin/findContractById");
        Assert.notNull(id, " id not null ");
        return contractService.findContractById(id);
    }


    /***
     * Qbao_后台 添加合约
     * @return
     */
    @ApiOperation(value = "Qbao_后台 添加合约", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/saveContract", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Contract saveContract(@RequestBody Contract contract) {
        logger.info("/admin/saveContract");
        Assert.notNull(contract, "contract not null");
        return contractService.saveContract(contract);
    }

    /***
     * Qbao_后台 修改合约
     * @return
     */
    @ApiOperation(value = "Qbao_后台 修改合约", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/updateContract", method = RequestMethod.POST, produces = "application/json", consumes = "application/json")
    public Contract updateContract(@RequestBody Contract contract) {
        logger.info("/admin/updateContract");
        Assert.notNull(contract, "contract not null");
        Assert.notNull(contract.getId(), "id not null");
        return contractService.updateContract(contract);
    }

    /***
     * Qbao_后台 冻结合约
     * @return
     */
    @ApiOperation(value = "Qbao_后台 冻结合约", notes = "参数：id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/disableContract", method = RequestMethod.GET, produces = "application/json")
    public Map disableContract(@RequestParam Long contractId) {
        logger.info("/admin/disableContract");
        Assert.notNull(contractId, "contractId not null");
        Map<String, Object> map = new HashMap<>();
        contractService.deleteContract(contractId, WalletConstants.ADDRESSDEFAULT);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao_后台 解冻合约
     * @return
     */
    @ApiOperation(value = "Qbao_后台 解冻合约", notes = "参数：id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/availableContract", method = RequestMethod.GET, produces = "application/json")
    public Map availableContract(@RequestParam Long contractId) {
        logger.info("/admin/availableContract");
        Assert.notNull(contractId, "contractId not null");
        Map<String, Object> map = new HashMap<>();
        contractService.deleteContract(contractId, WalletConstants.COMMON_NOT_DELETE);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao 查询所有汇率
     * @return
     */
    @ApiOperation(value = "查询所有汇率", notes = "返回List<CurrencyRate>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = CurrencyRate.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/findRateAll", method = RequestMethod.GET, produces = "application/json")
    public List<CurrencyRate> findRateAll() {

        logger.info("findRateAll");
        List<CurrencyRate> rates = new ArrayList<CurrencyRate>();

        rates = currencyRateService.getCurrencyRateList();

        return rates;
    }

    /***
     * Qbao 插入api_address
     * @return
     */
    @ApiOperation(value = "插入api_address", notes = "返回List<CurrencyRate>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = CurrencyRate.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/insertApi", method = RequestMethod.GET, produces = "application/json")
    public Map insertApi() {
        logger.info("insertApi");
        Map<String, Object> map = new HashMap<>();
        contractService.updateContractByApiAddress();
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao 后台 查询资金池情况
     * @return
     */
    @ApiOperation(value = "查询资金池情况", notes = "返回List<Contract>")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getTokenAmountList", method = RequestMethod.GET, produces = "application/json")
    public List<Contract> getTokenAmountList() {
        logger.info("getTokenAmountList");
        return contractService.getTokenAmountList();
    }



    /**
     * Qbao 更新代币顺序
     *
     * @param contractIdList
     * @return group
     */
    @ApiOperation(value = "Qbao 更新代币顺序", notes = "")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = Contract.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateContractSequence", method = RequestMethod.POST, consumes = "application/json", produces = "application/json")
    public Map updateContractSequence(@RequestBody String[] contractIdList) {

        logger.info("updateContractSequence");
        Map<String, Object> map = new HashMap<>();
        contractService.updateContractSequence(contractIdList);
        map.put("result", "success");
        return map;
    }

    /***
     * Qbao 根据type查询
     * @return
     */
    @ApiOperation(value = "根据type查询", notes = "type：0=qutm 1=eth 查询是已过滤isdelete=true的代币")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = CurrencyRate.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getContractsByType", method = RequestMethod.GET, produces = "application/json")
    public List<Contract> getContractsByType(@RequestParam Integer type) {

        logger.info("findRateAll");

        return contractService.getContractsByType(type);

    }
}
