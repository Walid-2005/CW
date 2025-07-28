package com.example.demo;

import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

public class Account implements Comparable<Account>, Serializable {
    private long score = 0;
    private String userName ;
    private static ArrayList<Account> accounts = new ArrayList<>();
    private static final String SAVE_FILE = "src/main/resources/accounts.dat";

    public Account(String userName){
        this.userName=userName;
    }

    @Override
    public int compareTo(Account o) {
        return Long.compare(o.getScore(), score);
    }

    public void addToScore(long score) {
        this.score += score;
    }

    public long getScore() {
        return score;
    }

    public String getUserName() {
        return userName;
    }

    // Alias for compatibility with getUsername() references
    public String getUsername() {
        return userName;
    }

    static Account accountHaveBeenExist(String userName){
        for(Account account : accounts){
            if(account.getUserName().equals(userName)){
                return account;
            }
        }
        return null;
    }

    static Account makeNewAccount(String userName){
        Account account = new Account(userName);
        accounts.add(account);
        return account;
    }

    public static void saveAccounts() {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE));
            out.writeObject(accounts);
            out.close();
        } catch (IOException e) {
            System.out.println("❌ Failed to save accounts.");
        }
    }

    public static void loadAccounts() {
        try {
            File file = new File(SAVE_FILE);
            if (!file.exists()) return;
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
            accounts = (ArrayList<Account>) in.readObject();
            in.close();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("❌ Failed to load accounts.");
        }
    }

    public static ArrayList<Account> getAccounts() {
        return accounts;
    }

    @Override
    public String toString() {
        return userName + ": " + score;
    }
}
