package com.marketplace.sfu_marketplace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.marketplace.sfu_marketplace.models.Account;
import java.util.List;
import java.util.Map;

import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.models.AccountRole;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


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

    @PostMapping("/register")
    public String register( @RequestParam String username, @RequestParam String email, @RequestParam String password) {

        //email already used?
        List<Account> existingAccounts = accountRepository.findByEmail(email);

        if (!existingAccounts.isEmpty()) {
            return "register";
        }

        //making new account
        Account account = new Account();

        account.setUsername(username);
        account.setEmail(email);

        //hashing password and storing it
        account.setPassword(passwordEncoder.encode(password));

        account.setRole(AccountRole.USER);

        accountRepository.save(account);

        return "redirect:/login";
    }

    //login page
    @GetMapping("/login")
    public String getLogin( Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {

        Account account = (Account) session.getAttribute("session_account");

        if (account == null) {
            return "login";
        } else { // if already logged in
            //to prevent caching like in example
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            model.addAttribute("account", account);

            return "account";
        }
    }

    @PostMapping("/login")
public String login( @RequestParam Map<String, String> formData, Model model, HttpServletRequest request, HttpSession session, HttpServletResponse response) {

    //submitted login info
    String email = formData.get("email");
    String password = formData.get("password");

    //find account using email
    List<Account> accounts = accountRepository.findByEmail(email);

    if (accounts.isEmpty()) {
        return "login";
    }

    Account account = accounts.get(0);

    //check submitted password with hash
    if (!passwordEncoder.matches(password, account.getPassword())) {
        return "login";
    } else {
        //login successful
        request.getSession().setAttribute("session_account", account);

        //prevent caching of protected pages
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        model.addAttribute("account", account);

        return "redirect:/account";
    }
}

    //account page
    @GetMapping("/account")
    public String showAccount(HttpSession session, Model model, HttpServletResponse response) {

        Account account = (Account) session.getAttribute("session_account");

        if (account == null) {
            return "redirect:/login";
        }

        //prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        model.addAttribute("account", account);

        return "account";
    }

    //logout
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
    
}
