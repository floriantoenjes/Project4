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
    private static BlogDao dao = new SimpleBlogDao();
    private static HandlebarsTemplateEngine hbsEngine = new HandlebarsTemplateEngine();
    private static Slugify slugify;

    static {
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // Set location of static files
        staticFileLocation("/public");

        // Create first blog entries
        createMockData();

        // Session creation & cookie assignment to attribute
        before((req, res) -> {
            req.session(true);
            if (req.cookie("user") != null) {
                req.attribute("user", req.cookie("user"));
            }
        });

        // Listing of all blog entries
        get("/", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entries", dao.findAll());
            return new ModelAndView(modelMap, "index.hbs");
        }, hbsEngine);

        // Creating a new blog entry
        before("/new.html", (req, res) -> {
            if (req.attribute("user") == null || !req.attribute("user").equals("admin")) {
                req.session().attribute("origin", "/new.html");
                res.redirect("/password.html");
                halt();
            }
        });

        // Submitting the creation of a new blog entry
        post("/", (req, res) -> {
            String author = "Florian Antonius";
            String title = req.queryParams("title");
            String slug = slugify.slugify(title);
            String content = req.queryParams("entry");

            dao.add(new BlogEntry(author, title, slug, content, null));

            res.redirect("/");
            return null;
        });

        // Detail view of a blog entry
        get("/entry/:slug", (req, res) -> {
            BlogEntry blogEntry = dao.findBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entry", blogEntry);
            return new ModelAndView(modelMap, "detail.hbs");
        }, hbsEngine);

        // Adding a comment to a blog entry
        post("/entry/:slug", (req, res) -> {
            String slug = req.params(":slug");
            BlogEntry blogEntry = dao.findBySlug(slug);
            Comment comment = new Comment(req.queryParams("name"),
                    req.queryParams("comment"),
                    LocalDateTime.now());
            blogEntry.addComment(comment);
            blogEntry.getCommentList().sort( (c1, c2) -> -c1.getCreationTime().compareTo(c2.getCreationTime()));
            res.redirect("/entry/" + slug);
            return null;
        });

        // User authentication before editing or deleting a blog entry
        before("/entry/:slug/*", (req, res) -> {
            if (req.attribute("user") == null || !req.attribute("user").equals("admin")) {
                req.session().attribute("origin", "/entry/" + req.params(":slug") + "/edit");
                res.redirect("/password.html");
                halt();
            }
        });

        // Removing a blog entry
        get("/entry/:slug/delete", (req, res) -> {
            BlogEntry blogEntry = dao.findBySlug(req.params(":slug"));
            dao.remove(blogEntry);
            res.redirect("/");
            return null;
        });

        // Editing a blog entry
        get("/entry/:slug/edit", (req, res) -> {
            BlogEntry blogEntry = dao.findBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entry", blogEntry);
            return new ModelAndView(modelMap, "edit.hbs");
        }, hbsEngine);

        // Submitting the blog entry changes
        post("/entry/:slug/edit", (req, res) -> {
            String slug = req.params(":slug");
            BlogEntry blogEntry = dao.findBySlug(slug);
            String title = req.queryParams("title");
            String newSlug = slugify.slugify(title);
            dao.edit(blogEntry, title, newSlug, req.queryParams("entry"));
            res.redirect("/entry/" + newSlug);
            return null;
        });

        // Authentication and cookie setting on the password page
        post("/password.html", (req, res) -> {
            if (req.queryParams("password").equals("admin")) {
                res.cookie("user", "admin");
                String origin = req.session().attribute("origin");
                req.session().removeAttribute("origin");
                res.redirect(origin);
                halt();
            }
            res.redirect("/password.html");
            return null;
        });
    }

    public static void createMockData() {
        String titleTmp;
        String slugTmp;

        titleTmp = "A Great Day with a Friend";
        slugTmp = slugify.slugify(titleTmp);
        dao.add(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "It was an amazing day with a good friend.", Arrays.asList("Friends", "Amazing")));

        titleTmp = "The Unusual Coder";
        slugTmp = slugify.slugify(titleTmp);
        dao.add(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "Some people code in extraordinarily strange ways.", Arrays.asList("People", "Coding", "Strange")));

        titleTmp = "What Will This Day Bring?";
        slugTmp = slugify.slugify(titleTmp);
        dao.add(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "Isn't it always a mystery what is going to happen next? One day it looks like a shiny morning and the " +
                        "next thing you know, it is pouring rain without limitation. It is this unforeseen factor" +
                        "which brings about a freshness in life everyday.", Arrays.asList("Time")));
    }
}
