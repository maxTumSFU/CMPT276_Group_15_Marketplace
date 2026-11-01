package com.marketplace.sfu_marketplace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.marketplace.sfu_marketplace.models.Account;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.marketplace.sfu_marketplace.models.AccountRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.hibernate.annotations.Cache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Controller
public class AccountController {
    private AccountRepository accountRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    //register page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }


    
}
