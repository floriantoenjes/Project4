package com.floriantoenjes.blog;

import com.floriantoenjes.blog.dao.BlogDao;
import com.floriantoenjes.blog.dao.SimpleBlogDao;
import com.floriantoenjes.blog.model.BlogEntry;
import com.github.slugify.Slugify;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
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

        get("/entries/:slug", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            BlogEntry blogEntry = dao.findEntryBySlug(req.params(":slug"));
            modelMap.put("title", blogEntry.getTitle());
            modelMap.put("content", blogEntry.getContent());
            modelMap.put("creationTime", blogEntry.getCreationTime());
            return new ModelAndView(modelMap, "detail.hbs");
        }, new HandlebarsTemplateEngine());
    }

}
