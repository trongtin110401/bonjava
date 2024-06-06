package game.config;


import java.io.File;

public class VBeePath {
    public static String basePath = "";

    /**
     * init base path of project
     */
    public static String initBasePath(Class cls) {
        basePath = getBasePath(cls);
        return basePath;
    }

    public static String getBasePath(Class cls) {
        String basePath = cls.getResource(cls.getSimpleName() + ".class").getPath();
        basePath = basePath.replace("file:", "");
        int index = basePath.indexOf("build");
        if (index >= 0) {
            basePath = basePath.substring(0, index);
        } else {
            basePath = System.getProperty("user.dir") + File.separator;
        }
        return basePath;
    }
}
