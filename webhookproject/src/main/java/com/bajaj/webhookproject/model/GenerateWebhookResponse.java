package com.bajaj.webhookproject.model;

import java.util.List;

public class GenerateWebhookResponse {
    private String webhook;
    private String accessToken;
    private Data data;

    public static class Data {
        private List<com.bajaj.webhookproject.model.User> users;
        private int findId;  // Added findId field
        private int n;       // Added n field

        public List<User> getUsers() {
            return users;
        }

        public void setUsers(List<User> users) {
            this.users = users;
        }

        public int getFindId() {
            return findId;  // Getter for findId
        }

        public void setFindId(int findId) {
            this.findId = findId;  // Setter for findId
        }

        public int getN() {
            return n;  // Getter for n
        }

        public void setN(int n) {
            this.n = n;  // Setter for n
        }
    }

    public String getWebhook() { return webhook; }
    public void setWebhook(String webhook) { this.webhook = webhook; }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public Data getData() { return data; }
    public void setData(Data data) { this.data = data; }
}
