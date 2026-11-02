package com.marketplace.sfu_marketplace.repositories;

import com.marketplace.sfu_marketplace.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Provides database operations for accounts.
public interface AccountRepository extends JpaRepository<Account, Integer> {

    // Finds accounts using their username.
    List<Account> findByUsername(String username);

    // Finds accounts using their email.
    List<Account> findByEmail(String email);
}