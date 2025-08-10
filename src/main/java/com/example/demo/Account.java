package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Represents a player account for the 2048 game.
 * Stores the player's username, score, and manages
 * saving/loading account data for leaderboard tracking.
 */
public class Account implements Comparable<Account>, Serializable {

    /** The current score of the account. */
    private long score = 0;

    /** The username associated with the account. */
    private String userName;

    /** A list of all registered accounts. */
    private static ArrayList<Account> accounts = new ArrayList<>();

    /** File path used for saving and loading accounts. */
    private static final String SAVE_FILE = "src/main/resources/accounts.dat";

    /**
     * Constructs a new account with the given username.
     *
     * @param userName the username for the new account
     */
    public Account(String userName){
        this.userName = userName;
    }

    /**
     * Compares this account to another based on score, for leaderboard sorting.
     * Accounts with higher scores are ordered first.
     *
     * @param o the account to compare against
     * @return a negative integer, zero, or a positive integer as this account's score
     *         is greater than, equal to, or less than the specified account's score
     */
    @Override
    public int compareTo(Account o) {
        return Long.compare(o.getScore(), score);
    }

    /**
     * Adds the given amount to the account's score.
     *
     * @param score the number of points to add
     */
    public void addToScore(long score) {
        this.score += score;
    }

    /**
     * Gets the account's current score.
     *
     * @return the current score
     */
    public long getScore() {
        return score;
    }

    /**
     * Gets the account's username.
     *
     * @return the username
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Alias for {@link #getUserName()} to maintain compatibility with
     * references using the alternative method name.
     *
     * @return the username
     */
    public String getUsername() {
        return userName;
    }

    /**
     * Checks if an account with the given username already exists.
     *
     * @param userName the username to check
     * @return the existing account if found, otherwise null
     */
    static Account accountHaveBeenExist(String userName){
        for(Account account : accounts){
            if(account.getUserName().equals(userName)){
                return account;
            }
        }
        return null;
    }

    /**
     * Creates a new account with the given username and adds it to the accounts list.
     *
     * @param userName the username for the new account
     * @return the newly created account
     */
    static Account makeNewAccount(String userName){
        Account account = new Account(userName);
        accounts.add(account);
        return account;
    }

    /**
     * Saves all accounts to a file for persistent storage.
     */
    public static void saveAccounts() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE));
            out.writeObject(accounts);
            out.close();
        } catch (IOException e) {
            System.out.println(" Failed to save accounts.");
        }
    }

    /**
     * Loads all accounts from the save file, if it exists.
     */
    public static void loadAccounts() {
        try {
            File file = new File(SAVE_FILE);
            if (!file.exists()) return;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            accounts = (ArrayList<Account>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(" Failed to load accounts.");
        }
    }

    /**
     * Returns the list of all accounts.
     *
     * @return a list of all accounts
     */
    public static ArrayList<Account> getAccounts() {
        return accounts;
    }

    /**
     * Returns a string representation of the account in the format "username: score".
     *
     * @return a string containing the username and score
     */
    @Override
    public String toString() {
        return userName + ": " + score;
    }
}
