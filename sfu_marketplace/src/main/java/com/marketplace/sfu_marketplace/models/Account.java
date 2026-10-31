package com.marketplace.sfu_marketplace.models;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
public class Account implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int accountId;

    private String username;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING)
    private AccountRole role = AccountRole.USER;

    //listings list - list of integer ids - can be used to search table of listings for the listings
        //had to search up how to do this - Max
    @ElementCollection
    @CollectionTable(
        name = "account_listings",
        joinColumns = @JoinColumn(name = "account_id")
    )
    @Column(name = "listing_id")
    private List<Integer> listingIds = new ArrayList<>();


    public Account() {
    }

    public Account(int accountId, String username, String email, String password, AccountRole role) {

        this.accountId = accountId;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountRole getRole() {
        return role;
    }

    public void setRole(AccountRole role) {
        this.role = role;
    }
    
    public List<Integer> getListingIds() {
        return listingIds;
    }

    public void setListingIds(List<Integer> listingIds) {
        this.listingIds = listingIds;
    }

    public void addListingId(int listingId) {
        listingIds.add(listingId);
    }

    public void removeListingId(int listingId) {
        listingIds.remove(Integer.valueOf(listingId));
    }
}
