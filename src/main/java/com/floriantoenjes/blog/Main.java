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

        // Cookie assignment to attribute
        before((req, res) -> {
            if (req.cookie("user") != null) {
                req.attribute("user", req.cookie("user"));
            }
        });

        // Listing of all blog entries
        get("/", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entries", dao.findAllEntries());
            return new ModelAndView(modelMap, "index.hbs");
        }, hbsEngine);

        // Creating a new blog entry
        before("/new.html", (req, res) -> {
            if (req.attribute("user") == null || !req.attribute("user").equals("admin")) {
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

        // User authentication before editing or deleting a blog entry
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

        // Editing a blog entry
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

        // Authentication and cookie setting on the password page
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

    public static void createMockData() {
        String titleTmp;
        String slugTmp;

        titleTmp = "A Great Day with a Friend";
        slugTmp = slugify.slugify(titleTmp);
        dao.addEntry(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "It was an amazing day with a good friend.", Arrays.asList("Friends", "Amazing")));

        titleTmp = "The Unusual Coder";
        slugTmp = slugify.slugify(titleTmp);
        dao.addEntry(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "Some people code in extraordinarily strange ways.", Arrays.asList("People", "Coding", "Strange")));

        titleTmp = "What Will This Day Bring?";
        slugTmp = slugify.slugify(titleTmp);
        dao.addEntry(new BlogEntry("Florian Antonius", titleTmp, slugTmp,
                "Isn't it always a mystery what is going to happen next?", Arrays.asList("Time")));
    }
}
