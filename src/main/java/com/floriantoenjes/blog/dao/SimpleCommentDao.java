package com.floriantoenjes.blog.dao;

import com.floriantoenjes.blog.model.BlogEntry;
import com.floriantoenjes.blog.model.Comment;

import java.util.List;

public class SimpleCommentDao implements CommentDao {
    @Override
    public boolean add(BlogEntry blogEntry, Comment comment) {
        List<Comment> comments = blogEntry.getCommentList();
        boolean added = comments.add(comment);
        comments.sort( (c1, c2) -> -c1.getCreationTime().compareTo(c2.getCreationTime()));
        return added;
    }
}
