package com.floriantoenjes.blog;

import com.floriantoenjes.blog.dao.BlogDao;
import com.floriantoenjes.blog.dao.CommentDao;
import com.floriantoenjes.blog.dao.SimpleBlogDao;
import com.floriantoenjes.blog.dao.SimpleCommentDao;
import com.floriantoenjes.blog.model.BlogEntry;
import com.floriantoenjes.blog.model.Comment;
import com.github.slugify.Slugify;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static final BlogDao blogDao = new SimpleBlogDao();
    private static final CommentDao commentDao = new SimpleCommentDao();
    private static final HandlebarsTemplateEngine hbsEngine = new HandlebarsTemplateEngine();
    private static Slugify slugify;

    static {
        try {
            slugify = new Slugify();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Could not create a Slugify instance!");
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        // Create first blog entries
        createMockData();


        // Set location of static files
        staticFileLocation("/public");

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
            modelMap.put("entries", blogDao.findAll());
            return new ModelAndView(modelMap, "index.hbs");
        }, hbsEngine);

        // Authentication before adding a new blog entry
        before("/new.html", Main::redirectToLogin);

        // Submitting the creation of a new blog entry
        post("/", (req, res) -> {
            String author = "Florian Antonius";
            String title = req.queryParams("title");
            String slug = slugify.slugify(title);
            String content = req.queryParams("entry");

            blogDao.add(new BlogEntry(author, title, slug, content, null));

            res.redirect("/");
            return null;
        });

        // Detail view of a blog entry
        get("/entry/:slug", (req, res) -> {
            BlogEntry blogEntry = blogDao.findBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entry", blogEntry);
            return new ModelAndView(modelMap, "detail.hbs");
        }, hbsEngine);

        // Adding a comment to a blog entry
        post("/entry/:slug", (req, res) -> {
            String slug = req.params(":slug");
            commentDao.add(blogDao.findBySlug(slug),
                    new Comment(req.queryParams("name"),
                            req.queryParams("comment")));
            res.redirect("/entry/" + slug);
            return null;
        });

        // User authentication before editing or deleting a blog entry
        before("/entry/:slug/*", Main::redirectToLogin);

        // Removing a blog entry
        get("/entry/:slug/delete", (req, res) -> {
            BlogEntry blogEntry = blogDao.findBySlug(req.params(":slug"));
            blogDao.remove(blogEntry);
            res.redirect("/");
            return null;
        });

        // Editing a blog entry
        get("/entry/:slug/edit", (req, res) -> {
            BlogEntry blogEntry = blogDao.findBySlug(req.params(":slug"));
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entry", blogEntry);
            return new ModelAndView(modelMap, "edit.hbs");
        }, hbsEngine);

        // Submitting the blog entry changes
        post("/entry/:slug/edit", (req, res) -> {
            String slug = req.params(":slug");
            BlogEntry blogEntry = blogDao.findBySlug(slug);
            String title = req.queryParams("title");
            String newSlug = slugify.slugify(title);
            blogDao.edit(blogEntry, title, newSlug, req.queryParams("entry"));
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

    private static void redirectToLogin(Request req, Response res) {
        if (req.attribute("user") == null || !req.attribute("user").equals("admin")) {
            req.session().attribute("origin", req.uri());
            res.redirect("/password.html");
            halt();
        }
    }

    private static void createMockData() {
        String titleTmp;
        String slugTmp;

        titleTmp = "A Great Day with a Friend";
        slugTmp = slugify.slugify(titleTmp);
        blogDao.add(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "It was an amazing day with a good friend.", Arrays.asList("Friends", "Amazing")));

        titleTmp = "The Unusual Coder";
        slugTmp = slugify.slugify(titleTmp);
        blogDao.add(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "Some people code in extraordinarily strange ways.", Arrays.asList("People", "Coding", "Strange")));

        titleTmp = "What Will This Day Bring?";
        slugTmp = slugify.slugify(titleTmp);
        blogDao.add(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "Isn't it always a mystery what is going to happen next? One day it looks like a shiny morning and the " +
                        "next thing you know, it is pouring rain without limitation. It is this unforeseen factor" +
                        "which brings about a freshness in life everyday.", Arrays.asList("Time")));
    }
}
