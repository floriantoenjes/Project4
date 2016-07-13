package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;
import com.floriantoenjes.blog.model.Comment;

public interface CommentDao {
    boolean add(BlogEntry blogEntry, Comment comment);
}
