package com.bajaj.webhookproject.model;

import java.util.List;

public class NthLevelInput {
    private int n;
    private int findId;
    private List<User> users;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public int getFindId() {
        return findId;
    }

    public void setFindId(int findId) {
        this.findId = findId;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
