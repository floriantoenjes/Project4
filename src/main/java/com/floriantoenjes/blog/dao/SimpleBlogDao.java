package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleBlogDao implements BlogDao {
    private final List<BlogEntry> blogEntryList = new ArrayList<>();

    @Override
    public boolean add(BlogEntry blogEntry) {
        boolean added = blogEntryList.add(blogEntry);
        blogEntryList.sort((b1, b2) -> -b1.getCreationTime().compareTo(b2.getCreationTime()));
        return added;
    }

    @Override
    public boolean remove(BlogEntry blogEntry) {
        return blogEntryList.remove(blogEntry);
    }

    @Override
    public BlogEntry edit(BlogEntry blogEntry, String title, String slug, String content) {
        blogEntry.setTitle(title);
        blogEntry.setSlug(slug);
        blogEntry.setContent(content);
        blogEntry.setCreationTime(LocalDateTime.now());
        return blogEntry;
    }

    @Override
    public List<BlogEntry> findAll() {
        return blogEntryList;
    }

    @Override
    public List<BlogEntry> findAllFromCategory(String category) {
        return blogEntryList.stream()
                .filter(blockEntry -> blockEntry.getTags().contains(category))
                .collect(Collectors.toList());
    }

    @Override
    public BlogEntry findBySlug(String slug) {
        return blogEntryList.stream().filter(blockEntry -> blockEntry.getSlug()
                .equals(slug))
                .findFirst()
                .orElse(null);
    }
}
