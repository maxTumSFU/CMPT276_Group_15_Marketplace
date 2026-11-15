
package com.marketplace.sfu_marketplace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.AccountRole;
import com.marketplace.sfu_marketplace.repositories.AccountRepository;

import java.util.List;
import java.util.Map;

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

    // Register page
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {

        // Email already used?
        List<Account> existingAccounts = accountRepository.findByEmail(email);

        if (!existingAccounts.isEmpty()) {
            return "register";
        }

        // Making new account
        Account account = new Account();

        account.setUsername(username);
        account.setEmail(email);

        // Hashing password and storing it
        account.setPassword(passwordEncoder.encode(password));
        account.setRole(AccountRole.USER);

        accountRepository.save(account);

        return "redirect:/login";
    }

    // Login page
    @GetMapping("/login")
    public String getLogin(
            Model model,
            HttpServletRequest request,
            HttpSession session,
            HttpServletResponse response) {

        Account account = (Account) session.getAttribute("session_account");

        if (account == null) {
            return "login";
        } else {
            // Prevent caching
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            model.addAttribute("account", account);

            return "account";
        }
    }

    @PostMapping("/login")
    public String login(
            @RequestParam Map<String, String> formData,
            Model model,
            HttpServletRequest request,
            HttpSession session,
            HttpServletResponse response) {

        // Submitted login info
        String email = formData.get("email");
        String password = formData.get("password");

        // Find account using email
        List<Account> accounts = accountRepository.findByEmail(email);

        if (accounts.isEmpty()) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }

        Account account = accounts.get(0);

        // Check submitted password with hash
        if (!passwordEncoder.matches(password, account.getPassword())) {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        } else {
            // Login successful
            request.getSession().setAttribute("session_account", account);

            // Prevent caching of protected pages
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            model.addAttribute("account", account);

            return "redirect:/listings";
        }
    }

    // Account page
    @GetMapping("/account")
    public String showAccount(
            HttpSession session,
            Model model,
            HttpServletResponse response) {

        Account account = (Account) session.getAttribute("session_account");

        if (account == null) {
            return "redirect:/login";
        }

        // Prevent caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        model.addAttribute("account", account);

        return "account";
    }

    // Logout
    @PostMapping("/logout")
    public String logout(HttpSession session) {

        session.invalidate();

        return "redirect:/login";
    }
}
