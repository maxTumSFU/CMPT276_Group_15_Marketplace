package com.marketplace.sfu_marketplace.repositories;

import com.marketplace.sfu_marketplace.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

// Provide database operations for accounts
public interface AccountRepository extends JpaRepository<Account, Integer> {

}