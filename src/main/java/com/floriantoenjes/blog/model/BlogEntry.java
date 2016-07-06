package com.floriantoenjes.blog.model;

import com.github.slugify.Slugify;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BlogEntry {
    private String author;
    private String content;
    private String slug;
    private String title;

    private List<String> tags = new ArrayList<>();
    private List<Comment> commentList = new ArrayList<>();
    private LocalDateTime creationTime;

    public boolean addComment(Comment comment) {
        return commentList.add(comment);
    }

    public BlogEntry(String author, String title, String slug, String content, List<String> tags) {
        this.author = author;
        this.content = content;
        this.slug = slug;
        this.title = title;
        this.tags = tags;
        creationTime = LocalDateTime.now();
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public String getSlug() {
        return slug;
    }

    public List<String> getTags() {
        return tags;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public String getCreationTime() {
        return creationTime.format(DateTimeFormatter.ofPattern("d.M.Y H:m"));
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public void setContent(String content) {
        this.content = content;

    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }
}
