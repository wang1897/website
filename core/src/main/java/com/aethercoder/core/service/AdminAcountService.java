package com.aethercoder.core.service;

import com.aethercoder.core.entity.admin.AdminAccount;

import java.util.List;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
public interface AdminAcountService {

    AdminAccount loginAdminAccount(String name, String password);

    AdminAccount findByName(String name);

    AdminAccount findById(long id);

    AdminAccount saveAdminAccount(AdminAccount adminAccount);

    AdminAccount updateAdminAccount(AdminAccount adminAccount);

    AdminAccount getAdminAccountByToken(String  token);

    List<AdminAccount> findAllAdminAccount();

    void deleteAdminAccount(Long id);

    void checkPasswordIsTrue(String password, Long id);

}
