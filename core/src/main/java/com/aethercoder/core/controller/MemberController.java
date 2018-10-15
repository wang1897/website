package com.aethercoder.core.controller;

import com.aethercoder.core.entity.member.MemberLevel;
import com.aethercoder.core.entity.wallet.SysWallet;
import com.aethercoder.core.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("member")
@Api(tags = "member", description = "会员等级接口管理")
public class MemberController {
    private static Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    /***
     * Qbao 前段 会员信息显示
     * @return
     */
    @ApiOperation(value="Qbao 前段 会员信息显示", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/memberLevelInformation",method = RequestMethod.GET,produces = "application/json")
    public List memberLevelInformation(){
        logger.info("memberLevelInformation");
        List list = memberService.getMemberInformation();
        return list;
    }

    /***
     * Qbao 前段 获取会员等级
     * @return
     */
    @ApiOperation(value="Qbao 前段 获取会员等级(icon level )", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = MemberLevel.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/getMemberLevel",method = RequestMethod.GET,produces = "application/json")
    public MemberLevel getMemberLevel(String accountNo){
        logger.info("getMemberLevel");
        MemberLevel memberLevel = memberService.getMemberLevelByAccountNo(accountNo);
        return memberLevel;
    }

    /***
     * Qbao 后台 会员信息显示
     * @return
     */
    @ApiOperation(value="Qbao 后台 会员信息显示", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/memberInformation",method = RequestMethod.GET,produces = "application/json")
    public List memberInformation(){
        logger.info("/admin/memberInformation");
        List list = memberService.getMember();
        return list;
    }

    /***
     * Qbao 后台 会员等级添加
     * @return
     */
    @ApiOperation(value="Qbao 后台 会员等级添加", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/addMemberInformation",method = RequestMethod.POST,produces = "application/json")
    public Map addMemberInformation(@RequestBody MemberLevel memberLevel){
        logger.info("/admin/addMemberInformation");
        return memberService.saveMemberLevelByMoney(memberLevel);
    }

    /***
     * Qbao 后台 会员等级修改
     * @return
     */
    @ApiOperation(value="Qbao 后台 会员等级修改", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @Transactional
    @RequestMapping(value = "/admin/updateMemberInformation",method = RequestMethod.POST,produces = "application/json")
    public Map  updateMemberInformation(@RequestBody MemberLevel memberLevel){
        logger.info("/admin/updateMemberInformation");
        org.springframework.util.Assert.notNull(memberLevel.getMoney(),"Money not null");
        org.springframework.util.Assert.notNull(memberLevel.getLevel(),"level not null");
       return memberService.updateMemberLevelByMoney(memberLevel);

    }

    /***
     * Qbao 后台 删除会员等级
     * @return
     */
    @ApiOperation(value="Qbao 后台 删除会员等级", notes="")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "成功", response = SysWallet.class),
            @ApiResponse(code = 400, message = "输入不正确"),
            @ApiResponse(code = 401, message = "没有权限访问"),
            @ApiResponse(code = 500, message = "系统错误")
    }
    )
    @RequestMapping(value = "/admin/deleteMemberInformation",method = RequestMethod.GET,produces = "application/json")
    public Map deleteMemberInformation(Long id){
        logger.info("/admin/deleteMemberInformation");
        org.springframework.util.Assert.notNull(id,"id not null");
        return memberService.deleteMemberInformationByLevel(id);
    }
}
