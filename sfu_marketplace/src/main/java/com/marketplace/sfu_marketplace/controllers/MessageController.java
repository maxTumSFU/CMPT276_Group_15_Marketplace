
package com.marketplace.sfu_marketplace.controllers;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.models.Message;

import com.marketplace.sfu_marketplace.repositories.AccountRepository;
import com.marketplace.sfu_marketplace.repositories.ListingRepository;
import com.marketplace.sfu_marketplace.repositories.MessageRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class MessageController {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    private final ListingRepository listingRepository;

    // Provides access to messages, accounts, and listings
    public MessageController(
            MessageRepository messageRepository,
            AccountRepository accountRepository,
            ListingRepository listingRepository) {

        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
        this.listingRepository = listingRepository;
    }

    // Opens a conversation when the buyer clicks Message Seller
    @GetMapping("/messages/start/{listingId}")
    public String startConversation(
            @PathVariable int listingId,
            HttpSession session) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        // Users must log in before messaging
        if (sessionAccount == null) {
            return "redirect:/login";
        }

        Listing listing =
                listingRepository.findById(listingId).orElse(null);

        if (listing == null) {
            return "redirect:/listings";
        }

        // Get the latest account information from the database
        Account buyer = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (buyer == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Account seller = listing.getSeller();

        // Prevent sellers from messaging themselves
        if (buyer.getAccountId() == seller.getAccountId()) {
            return "redirect:/listings/" + listingId;
        }

        // Open the conversation for this buyer, seller, and listing
        return "redirect:/messages/conversation/"
                + listingId + "/" + seller.getAccountId();
    }

    // Displays an existing conversation or an empty conversation for a buyer
    @GetMapping("/messages/conversation/{listingId}/{otherAccountId}")
    public String showConversation(
            @PathVariable int listingId,
            @PathVariable int otherAccountId,
            HttpSession session,
            Model model) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        if (sessionAccount == null) {
            return "redirect:/login";
        }

        Account currentAccount = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (currentAccount == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Listing listing =
                listingRepository.findById(listingId).orElse(null);

        Account otherAccount =
                accountRepository.findById(otherAccountId).orElse(null);

        if (listing == null || otherAccount == null) {
            return "redirect:/listings";
        }

        Account seller = listing.getSeller();

        // Only the listing's seller and the other participant can access the chat.
        boolean currentIsSeller =
                currentAccount.getAccountId() == seller.getAccountId();

        boolean otherIsSeller =
                otherAccount.getAccountId() == seller.getAccountId();

        if (currentIsSeller == otherIsSeller) {
            return "redirect:/listings";
        }

        List<Message> messages = messageRepository.findConversation(
                listing, currentAccount, otherAccount);

        // A seller can only open a conversation that a buyer has started
        if (currentIsSeller && messages.isEmpty()) {
            return "redirect:/listings";
        }

        // Mark incoming messages as read when the conversation is opened
        messageRepository.markConversationAsRead(
                listing, otherAccount, currentAccount);

        model.addAttribute("listing", listing);
        model.addAttribute("messages", messages);
        model.addAttribute("currentAccount", currentAccount);
        model.addAttribute("otherAccount", otherAccount);

        return "conversation";
    }

    // Saves a new message and returns to the same conversation
    @PostMapping("/messages/send")
    public String sendMessage(
            @RequestParam int listingId,
            @RequestParam int otherAccountId,
            @RequestParam String content,
            HttpSession session) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        if (sessionAccount == null) {
            return "redirect:/login";
        }

        Account currentAccount = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (currentAccount == null) {
            session.invalidate();
            return "redirect:/login";
        }

        Listing listing =
                listingRepository.findById(listingId).orElse(null);

        Account otherAccount =
                accountRepository.findById(otherAccountId).orElse(null);

        if (listing == null || otherAccount == null) {
            return "redirect:/listings";
        }

        Account seller = listing.getSeller();

        boolean currentIsSeller =
                currentAccount.getAccountId() == seller.getAccountId();

        boolean otherIsSeller =
                otherAccount.getAccountId() == seller.getAccountId();

        // Make sure the conversation is between the buyer and this listing's seller
        if (currentIsSeller == otherIsSeller) {
            return "redirect:/listings";
        }

        List<Message> existingMessages = messageRepository.findConversation(
                listing, currentAccount, otherAccount);

        // Sellers can reply only after a buyer has started the conversation
        if (currentIsSeller && existingMessages.isEmpty()) {
            return "redirect:/listings";
        }

        // Prevent empty messages from being saved
        if (content == null || content.trim().isEmpty()) {
            return "redirect:/messages/conversation/"
                    + listingId + "/" + otherAccountId;
        }

        // Create and save the message
        Message message = new Message();
        message.setSender(currentAccount);
        message.setReceiver(otherAccount);
        message.setListing(listing);
        message.setContent(content.trim());

        messageRepository.save(message);

        return "redirect:/messages/conversation/"
                + listingId + "/" + otherAccountId;
    }

    // Displays all conversations belonging to the logged-in account
    @GetMapping("/messages")
    public String showMessages(HttpSession session, Model model) {

        Account sessionAccount =
                (Account) session.getAttribute("session_account");

        // Users must log in to access their inbox
        if (sessionAccount == null) {
            return "redirect:/login";
        }

        Account currentAccount = accountRepository.findById(
                sessionAccount.getAccountId()).orElse(null);

        if (currentAccount == null) {
            session.invalidate();
            return "redirect:/login";
        }

        // Get all messages sent or received by this account
        List<Message> allMessages =
                messageRepository.findBySenderOrReceiverOrderBySentAtDesc(
                        currentAccount, currentAccount);

        // Count all unread messages received by this account
        long unreadCount =
                messageRepository.countByReceiverAndReadFalse(currentAccount);

        // Store the unread message count for each conversation
        Map<String, Long> unreadConversationCounts = new LinkedHashMap<>();

        // Keep only the latest message for each conversation
        Map<String, Message> latestMessages = new LinkedHashMap<>();

        for (Message message : allMessages) {

            Account otherAccount =
                    message.getSender().getAccountId() == currentAccount.getAccountId()
                    ? message.getReceiver()
                    : message.getSender();

            // Each listing and other account has a separate conversation
            String conversationKey =
                    message.getListing().getListingId()
                    + "-" + otherAccount.getAccountId();

            // Keep the latest message for this conversation
            latestMessages.putIfAbsent(conversationKey, message);

            // Count unread messages received by the logged-in account
            if (message.getReceiver().getAccountId() == currentAccount.getAccountId()
                    && !message.isRead()) {

                unreadConversationCounts.merge(conversationKey, 1L, Long::sum);
            }
        }

        // Send unread counts and conversations to the inbox page
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("unreadConversationCounts", unreadConversationCounts);
        model.addAttribute("latestMessages", latestMessages.values());
        model.addAttribute("currentAccount", currentAccount);

        return "messages";
    }
}
