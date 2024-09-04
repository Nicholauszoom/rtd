package com.example.transactionsms;

public class Category {
    private int logoResId;
    private int count;

    public Category(int logoResId, int count) {
        this.logoResId = logoResId;
        this.count = count;
    }

    public int getLogoResId() {
        return logoResId;
    }

    public int getCount() {
        return count;
    }
}