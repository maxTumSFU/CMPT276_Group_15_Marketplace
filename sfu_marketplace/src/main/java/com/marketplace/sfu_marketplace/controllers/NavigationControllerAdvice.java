package com.marketplace.sfu_marketplace.controllers;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.repositories.MessageRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class NavigationControllerAdvice {

    private final MessageRepository messageRepository;

    public NavigationControllerAdvice(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    // Makes the unread message count available on every page.
    @ModelAttribute
    public void addUnreadCount(Model model, HttpSession session) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        // Visitors who are not logged in have no unread messages.
        if (sessionAccount == null) {
            model.addAttribute("unreadCount", 0);
            return;
        }

        // Count messages received but not yet read.
        long unreadCount =
                messageRepository.countByReceiverAndReadFalse(sessionAccount);

        model.addAttribute("unreadCount", unreadCount);
    }
}