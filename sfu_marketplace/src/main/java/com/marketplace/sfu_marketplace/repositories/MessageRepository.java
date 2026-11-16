
package com.marketplace.sfu_marketplace.repositories;

import com.marketplace.sfu_marketplace.models.Account;
import com.marketplace.sfu_marketplace.models.Listing;
import com.marketplace.sfu_marketplace.models.Message;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    void deleteByListing_ListingId(int listingId);

    // Finds all messages between two accounts about one specific listing.
    @Query("""
        SELECT m FROM Message m
        WHERE m.listing = :listing
        AND (
            (m.sender = :account1 AND m.receiver = :account2)
            OR
            (m.sender = :account2 AND m.receiver = :account1)
        )
        ORDER BY m.sentAt ASC
        """)
    List<Message> findConversation(
            @Param("listing") Listing listing,
            @Param("account1") Account account1,
            @Param("account2") Account account2);

    // Finds all messages sent or received by an account.
    List<Message> findBySenderOrReceiverOrderBySentAtDesc(
            Account sender, Account receiver);

    // Counts unread messages received by an account.
    long countByReceiverAndReadFalse(Account receiver);

    // Marks incoming messages in a specific conversation as read.
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
        UPDATE Message m
        SET m.read = true
        WHERE m.listing = :listing
        AND m.sender = :otherAccount
        AND m.receiver = :currentAccount
        AND m.read = false
        """)
    int markConversationAsRead(
            @Param("listing") Listing listing,
            @Param("otherAccount") Account otherAccount,
            @Param("currentAccount") Account currentAccount);
}
