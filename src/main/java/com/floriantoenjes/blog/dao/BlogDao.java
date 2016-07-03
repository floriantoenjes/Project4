package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;

import java.util.List;

public interface BlogDao {
    boolean addEntry(BlogEntry blogEntry);
    boolean removeEntry(BlogEntry blogEntry);
    boolean editEntry(BlogEntry blogEntry);
    List<BlogEntry> findAllEntries();
    BlogEntry findEntryBySlug(String slug);
}
