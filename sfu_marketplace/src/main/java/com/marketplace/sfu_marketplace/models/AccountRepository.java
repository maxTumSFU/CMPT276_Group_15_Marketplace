package com.marketplace.sfu_marketplace.models;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AccountRepository extends JpaRepository<Account, Integer> {
    List<Account> findByName(String username);
    List<Account> findByEmail(String email);
}
