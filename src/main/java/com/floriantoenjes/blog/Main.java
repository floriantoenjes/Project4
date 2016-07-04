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

    public static void main(String[] args) {
        staticFileLocation("/public");
        dao = new SimpleBlogDao();

        try {
            Slugify slugify = new Slugify();
            String title = "A Great Day with a Friend";
            dao.addEntry(new BlogEntry("Florian Antonius", slugify.slugify(title), title,
                    "This has been a great day!", Arrays.asList("Karl Jaspers", "Oldenburg")));
        } catch (IOException e) {
            e.printStackTrace();
        }

        get("/", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entries", dao.findAllEntries());
            return new ModelAndView(modelMap, "index.hbs");
        }, new HandlebarsTemplateEngine());

        post("/", (req, res) -> {
            return null;
        });
    }

}
