package controllers;

import com.typesafe.config.Config;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.File;

public class ImageController extends WiFreeController {

    @Inject
    Config config;

    public Result image(String name) {
        String pathPrefix = config.getString("images.path");
        File file = new File(pathPrefix + name);
        return ok(file);
    }

}
