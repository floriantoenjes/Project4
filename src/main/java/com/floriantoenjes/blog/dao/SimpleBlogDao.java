package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleBlogDao implements BlogDao {
    private final List<BlogEntry> blogEntryList = new ArrayList<>();

    @Override
    public boolean addEntry(BlogEntry blogEntry) {
        return blogEntryList.add(blogEntry);
    }

    @Override
    public boolean removeEntry(BlogEntry blogEntry) {
        return blogEntryList.remove(blogEntry);
    }

    @Override
    public boolean editEntry(BlogEntry blogEntry) {
        return false;
    }

    @Override
    public List<BlogEntry> findAllEntries() {
        return blogEntryList;
    }

    @Override
    public List<BlogEntry> findAllEntriesFromCategory(String category) {
        return blogEntryList.stream()
                .filter(blockEntry -> blockEntry.getTags().contains(category))
                .collect(Collectors.toList());
    }

    @Override
    public BlogEntry findEntryBySlug(String slug) {
        return blogEntryList.stream().filter(blockEntry -> blockEntry.getSlug()
                .equals(slug))
                .findFirst()
                .orElse(null);
    }
}
