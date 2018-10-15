package com.aethercoder.core.security;

import com.aethercoder.core.contants.RedisConstants;
import com.aethercoder.core.contants.WalletConstants;
import com.aethercoder.core.dao.AccountDao;
import com.aethercoder.core.entity.admin.AdminAccount;
import com.aethercoder.core.entity.wallet.Account;
import com.aethercoder.core.entity.wallet.Customer;
import com.aethercoder.core.service.CustomerService;
import com.aethercoder.foundation.contants.CommonConstants;
import com.aethercoder.core.service.AdminAcountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Created by hepen on 7/12/2017.
 */
//@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private AdminAcountService adminAcountService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private AccountDao accountDao;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    private String tokenHeader = CommonConstants.HEADER_WEB_TOKEN_KEY;

    private String appHeader = CommonConstants.HEADER_APP_TOKEN_KEY;

    private String adminFlag = CommonConstants.HEADER_ADMIN_FLAG_KEY;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {
        /*Enumeration<String> headerNameEnum = request.getHeaderNames();
        String authHeader = null;
        String appHeader = null;
        String isFromAdmin = null;

        UserDetails userDetails = null;
        Object credentials = null;

        while (headerNameEnum.hasMoreElements()) {
            String headerName = headerNameEnum.nextElement();
            if (headerName.equalsIgnoreCase(this.tokenHeader)) {
                authHeader = request.getHeader(headerName);
            } else if (headerName.equalsIgnoreCase(this.appHeader)) {
                appHeader = request.getHeader(headerName);
            } else if (headerName.equalsIgnoreCase(this.adminFlag)) {
                isFromAdmin = request.getHeader(headerName);
            }
        }

        if (appHeader == null) {
            appHeader = request.getParameter(this.appHeader);
        }

        boolean isAdmin = false, isCustPlatform = false;
        PathMatcher matcher = new AntPathMatcher();
        if (matcher.match(CommonConstants.SYS_ADMIN_PATH, request.getServletPath()) && authHeader != null) {
            isAdmin = true;
        } else if (matcher.match(CommonConstants.SYS_CUSTOMER_PLATFORM_PATH, request.getServletPath()) && authHeader != null) {
            isCustPlatform = true;
        }

        if (isAdmin || isCustPlatform) {
            final String authToken = authHeader;
            String username = jwtTokenUtil.getUsernameFromToken(authToken);

            logger.info("checking authentication " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                try {
                    if (isAdmin) {
                        AdminAccount adminAccount = adminAcountService.findByName(username);
                        if (adminAccount != null) {
                            userDetails = new org.springframework.security.core.userdetails.User(
                                    adminAccount.getName(), "", true, true, true, true,
                                    new HashSet<>());
                            credentials = adminAccount;
                        }
                    } else if (isCustPlatform) {
                        Customer customer = customerService.findCustomerByCustomerNo(username);
                        if (customer != null) {
                            userDetails = new org.springframework.security.core.userdetails.User(
                                    customer.getCustomerNo(), "", true, true, true, true,
                                    new HashSet<>());
                            credentials = customer;
                        }
                    }
                } catch (AppException e) {

                }

                if (userDetails != null && jwtTokenUtil.validateToken(authToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, credentials, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                            request));
                    logger.info("authenticated user " + username + ", setting security context");
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    response.setHeader(tokenHeader, jwtTokenUtil.generateToken(username, WalletConstants.JWT_EXPIRATION));
                }
            }
        } else {
            if ("true".equals(isFromAdmin) && authHeader != null) {
                final String authToken = authHeader;
                String username = jwtTokenUtil.getUsernameFromToken(authToken);
                if (username != null) {
                    AdminAccount adminAccount = adminAcountService.findByName(username);
                    if (adminAccount != null) {
                        userDetails = new org.springframework.security.core.userdetails.User(
                                adminAccount.getName(), "", true, true, true, true,
                                new HashSet<>());
                        credentials = adminAccount;
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails,
                                credentials, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        logger.info("authenticated user " + "admin" + ", setting security context");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        response.setHeader(tokenHeader, jwtTokenUtil.generateToken(username, WalletConstants.JWT_EXPIRATION));
                    }
                }
            } else {
                String accountNo = jwtTokenUtil.getUsernameFromToken(appHeader);
                if (accountNo != null) {
                    String token = (String) redisTemplate.opsForValue().get(RedisConstants.REDIS_KEY_TOKEN + accountNo);
                    if (token != null && token.equals(appHeader)) {
                        userDetails = new org.springframework.security.core.userdetails.User(
                                accountNo, "", true, true, true, true,
                                new HashSet<>());
                        Account account = accountDao.findAccountByAccountNo(accountNo);
                        Account account1 = new Account();
                        BeanUtils.copyProperties(account, account1);
                        credentials = account1;
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                userDetails, credentials, userDetails.getAuthorities());
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        logger.info("authenticated user " + "admin" + ", setting security context");
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
                }
            }
        }*/

        chain.doFilter(request, response);
    }
}
