package com.aethercoder.core.dao;

import com.aethercoder.core.entity.admin.AdminAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Guo Feiyan on 2017/8/30.
 */
@Repository
public interface AdminAccountDao extends JpaRepository<AdminAccount, Long> {
    AdminAccount findAdminAccountByNameAndPassword(String name,String password);

    AdminAccount findAdminAccountByName(String name);

    AdminAccount findAdminAccountByPasswordAndId(String password, Long id);
}
