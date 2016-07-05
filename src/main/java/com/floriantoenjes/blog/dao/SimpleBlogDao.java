package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleBlogDao implements BlogDao {
    List<BlogEntry> blogEntryList = new ArrayList<>();

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
    public BlogEntry findEntryBySlug(String slug) {
        return blogEntryList.stream().filter(blockEntry -> blockEntry.getSlug()
                .equals(slug))
                .findFirst()
                .get();
    }
}
