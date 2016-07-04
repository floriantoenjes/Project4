package com.floriantoenjes.blog;

import com.floriantoenjes.blog.dao.SimpleBlogDao;

import static spark.Spark.*;

public class Main {
    SimpleBlogDao dao;

    public static void main(String[] args) {
        staticFileLocation("/public");

        get("/", (req, res) -> "Hello World");
    }

}
