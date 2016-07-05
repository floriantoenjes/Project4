package com.floriantoenjes.blog;

import com.floriantoenjes.blog.dao.BlogDao;
import com.floriantoenjes.blog.dao.SimpleBlogDao;
import com.floriantoenjes.blog.model.BlogEntry;
import com.floriantoenjes.blog.model.Comment;
import com.github.slugify.Slugify;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    static BlogDao dao;
    static Slugify slugify;

    public static void main(String[] args) {
        staticFileLocation("/public");
        dao = new SimpleBlogDao();
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Mock blog data
        String titleTmp = "A Great Day with a Friend";
        String slugTmp = slugify.slugify(titleTmp);
        dao.addEntry(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "It was an amazing day with a good friend.", null));

        get("/", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entries", dao.findAllEntries());
            return new ModelAndView(modelMap, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/", (req, res) -> {
            String author = "Florian Antonius";
            String title = req.queryParams("title");
            String slug = slugify.slugify(title);
            String content = req.queryParams("entry");

            BlogEntry blogEntry = new BlogEntry(author, title, slug, content, null);

            dao.addEntry(blogEntry);

            res.redirect("/");
            return res;
        });

        get("/entry/:slug", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("title", blogEntry.getTitle());
            modelMap.put("content", blogEntry.getContent());
            modelMap.put("creationTime", blogEntry.getCreationTime());
            modelMap.put("slug", blogEntry.getSlug());
            modelMap.put("comments", blogEntry.getCommentList());
            return new ModelAndView(modelMap, "detail.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entry/:slug", (req, res) -> {
            String slug = req.params(":slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            Comment comment = new Comment(req.queryParams("name"),
                    req.queryParams("comment"),
                    LocalDateTime.now());
            blogEntry.addComment(comment);
            res.redirect("/entry/" + slug);
            return res;
        });

        get("/entry/:slug/edit", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("title", blogEntry.getTitle());
            modelMap.put("content", blogEntry.getContent());
            modelMap.put("creationTime", blogEntry.getCreationTime());
            return new ModelAndView(modelMap, "edit.hbs");
        }, new HandlebarsTemplateEngine());

        post("/entry/:slug/edit", (req, res) -> {
            String slug = req.params(":slug");
            BlogEntry blogEntry = dao.findEntryBySlug(slug);
            String title = req.queryParams("title");

            blogEntry.setTitle(title);
            String newSlug = slugify.slugify(title);
            blogEntry.setSlug(newSlug);
            blogEntry.setContent(req.queryParams("entry"));
            blogEntry.setCreationTime(LocalDateTime.now());

            res.redirect("/entry/" + newSlug);
            return res;
        });
    }

}
