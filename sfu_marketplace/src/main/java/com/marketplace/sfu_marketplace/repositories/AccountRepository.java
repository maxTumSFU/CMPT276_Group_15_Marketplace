package com.marketplace.sfu_marketplace.repositories;
import com.marketplace.sfu_marketplace.models.Account;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByUsername(String username);
    List<Account> findByEmail(String email);
}
