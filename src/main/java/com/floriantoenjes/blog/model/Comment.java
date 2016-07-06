package com.floriantoenjes.blog.model;

import java.time.LocalDateTime;

public class Comment {
    private String author;
    private String content;
    private LocalDateTime creationTime;

    public Comment(String author, String content, LocalDateTime creationTime) {
        this.author = author;
        this.content = content;
        this.creationTime = creationTime;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getCreationTime() {
        return creationTime;
    }
}
