package com.floriantoenjes.blog;

import com.floriantoenjes.blog.dao.BlogDao;
import com.floriantoenjes.blog.dao.SimpleBlogDao;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    static BlogDao dao;

    public static void main(String[] args) {
        staticFileLocation("/public");
        dao = new SimpleBlogDao();

        get("/", (req, res) -> {
            Map<String, Object> modelMap = new HashMap<>();
            modelMap.put("entries", dao.findAllEntries());
            return new ModelAndView(modelMap, "index.hbs");
        }, new HandlebarsTemplateEngine());
    }

}
