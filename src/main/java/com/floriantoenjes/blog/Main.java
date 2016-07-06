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
    private static BlogDao dao;
    private static HandlebarsTemplateEngine hbsEngine;
    private static Slugify slugify;

    public static void main(String[] args) {
        dao = new SimpleBlogDao();
        hbsEngine = new HandlebarsTemplateEngine();
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set location of static files
        staticFileLocation("/public");

        // Mock blog data
        String titleTmp = "A Great Day with a Friend";
        String slugTmp = slugify.slugify(titleTmp);
        dao.addEntry(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "It was an amazing day with a good friend.", Arrays.asList(new String[]{"Friends", "Amazing"})));

        // Cookie assignment to attribute
        before((req, res) -> {
            if (req.cookie("user") != null) {
                req.attribute("user", req.cookie("user"));
            }
        });

        // List all blog entries
        get("/", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entries", dao.findAllEntries());
            return new ModelAndView(modelMap, "index.hbs");
        }, hbsEngine);

        // Create a new blog entry
        before("/new.html", (req, res) -> {
            if (req.attribute("user") == null || !req.attribute("user").equals("admin")) {
                res.redirect("/password.html");
                halt();
            }
        });

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

        // Detail view of a blog entry
        get("/entry/:slug", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entry", blogEntry);
            return new ModelAndView(modelMap, "detail.hbs");
        }, hbsEngine);

        // Adding a comment to a blog entry
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

        before("/entry/:slug/*", (req, res) -> {
            if (req.attribute("user") == null || !req.attribute("user").equals("admin")) {
                res.redirect("/password.html");
                halt();
            }
        });

        // Removing a blog entry
        get("/entry/:slug/delete", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params(":slug"));
            dao.removeEntry(blogEntry);
            res.redirect("/");
            return res;
        });

        // Edit a blog entry
        get("/entry/:slug/edit", (req, res) -> {
            BlogEntry blogEntry = dao.findEntryBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entry", blogEntry);
            return new ModelAndView(modelMap, "edit.hbs");
        }, hbsEngine);

        // Submitting the blog entry changes
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

        post("/password.html", (req, res) -> {
            if (req.queryParams("password").equals("admin")) {
                res.cookie("user", "admin");
                res.redirect("/");
                halt();
            }
            res.redirect("/password.html");
            return res;
        });
    }
}
