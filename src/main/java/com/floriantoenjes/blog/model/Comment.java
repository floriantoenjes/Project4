package com.floriantoenjes.blog.model;

import java.time.LocalDateTime;

public class Comment {
    int id;
    String author;
    String content;
    LocalDateTime creationTime;

    public Comment(int id, String author, String content, LocalDateTime creationTime) {
        this.id = id;
        this.author = author;
        this.content = content;
        this.creationTime = creationTime;
    }
}
