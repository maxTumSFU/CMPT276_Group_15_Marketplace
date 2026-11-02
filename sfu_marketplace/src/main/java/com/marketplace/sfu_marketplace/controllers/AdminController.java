package com.marketplace.sfu_marketplace.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.AccountRole;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Controller
public class AdminController {

    private final AccountRepository accountRepository;
    
    public AdminController(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    @GetMapping("/admin")
    public String getAllUsers(
        Model model,
        HttpSession session,
        HttpServletResponse response){

            Account sessionAccount = (Account) session.getAttribute("session_account");

            if (sessionAccount == null){
            return "redirect:/login";
            }   

            List<Account> matches = accountRepository.findByEmail(sessionAccount.getEmail());
            
            if (matches.size() != 1) {
            session.invalidate();
            return "redirect:/login";
            }

            Account currentAccount = matches.get(0);

            if (currentAccount.getRole() != AccountRole.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
            }

        model.addAttribute("accounts", accountRepository.findAll());
        return "admin";
    }


}
