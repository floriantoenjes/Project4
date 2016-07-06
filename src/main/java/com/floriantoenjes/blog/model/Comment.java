package com.floriantoenjes.blog.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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

    public String getCreationTime() {
        return creationTime.format(DateTimeFormatter.ofPattern("d.M.Y H:m"));
    }
}
