package com.bajaj.webhookproject.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class User {
    private int id;
    private String name;

    @JsonProperty("follows")  // Removed the space after follows
    private List<Integer> follows;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Integer> getFollows() { return follows; }
    public void setFollows(List<Integer> follows) { this.follows = follows; }
}
