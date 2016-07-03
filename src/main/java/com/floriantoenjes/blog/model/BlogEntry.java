package com.floriantoenjes.blog.model;

import java.time.LocalDateTime;
import java.util.List;

public class BlogEntry {
    int id;
    String author;
    String content;
    String title;
    List<String> tags;
    List<Comment> commentList;
    LocalDateTime creationtime;

    public boolean addComment(Comment comment) {
        // Store these comments!
        return false;
    }

    public BlogEntry(int id, String author, String content, String title, List<String> tags) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.title = title;
        this.tags = tags;
        creationtime = LocalDateTime.now();
    }
}
