package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;

import java.util.List;

public interface BlogDao {
    boolean add(BlogEntry blogEntry);
    boolean remove(BlogEntry blogEntry);

    BlogEntry edit(BlogEntry blogEntry, String title, String slug, String content);

    List<BlogEntry> findAll();
    BlogEntry findBySlug(String slug);
    List<BlogEntry> findAllFromCategory(String category);
}
