package br.com.batata.testespark;

import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.Inet4Address;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletException;
import javax.servlet.http.Part;
import static spark.Spark.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import static spark.debug.DebugScreen.enableDebugScreen;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

/**
 *
 * @author r0g0
 */
public class ChatBatata {

    static final Logger logger = LoggerFactory.getLogger(ChatBatata.class);
    static File uploadDir;
    static int port = 4567;
    static String ip = "";

    static {
        try {
             ip = Inet4Address.getLocalHost().getHostAddress();
        } catch(Exception e) {
            logger.error("Erro ao obter o ip da maquina.", e);
        }
    }

    private static void config(String[] args) {

        configUploadDirectory(args);

        staticFiles.location("/public");

        staticFiles.expireTime(1000);

        boolean localhost = true;

        if(localhost) {
            enableDebugScreen();
        } else {
            exception(Exception.class, (e, req, res) -> logger.error(e.getMessage()) );
        }

        port(port);

        int maxThreads = 8;
        int minThreads = 2;
        int timeOutMillis = 30000;
        threadPool(maxThreads, minThreads, timeOutMillis);

        after((request, response) -> {
            response.header("Content-Encoding", "gzip");
        });

    }

    public static void main(String[] args) {
        
        webSocket("/echo", ChatWebSocket.class);
        
        config(args);

        configurarRotas();

        init();

        logger.info("http://" + ip + ":" + port + "/chatbatata.html");

        configScheduler();
    }

    private static void configScheduler() {
        Timer timer = new Timer();
        timer.schedule(new CleanTask(), 1000, 60 * 2000); // tempo de 2 minutos
    }

    private static Map<String, Object> model(String key, Object value) {
        Map<String, Object> model = new HashMap<>();
        model.put(key, value);
        return model;
    }

    private static void configurarRotas() {
        Gson gson = new Gson();

        get("/upload", (req,resp) -> {
           return new ModelAndView(null, "upload.html");
        }, new MustacheTemplateEngine());

        post("/upload", (req, resp) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("/temp"));
            String[] name;
            Path tempFile;
            try (InputStream input = req.raw().getPart("uploaded_file").getInputStream()) {
                name = getFileName(req.raw().getPart("uploaded_file")).split("\\.");
                tempFile = Files.createTempFile(uploadDir.toPath(), "", "." + name[name.length - 1]);
                Files.copy(input, tempFile, StandardCopyOption.REPLACE_EXISTING);
            }

            logInfo(req, tempFile);

            String comp = getComponent(name[name.length - 1]);
           
            return new ModelAndView(model("comp",
                    comp.replace("0", tempFile.getFileName().toString())), "upload.view.html");

        }, new MustacheTemplateEngine());
    }

    private static String getComponent(String mimetype) {
        String tag = "";
        if(mimetype != null) {
            switch (mimetype.toLowerCase()) {
                case "png":
                case "jpg":
                case "jpeg":
                case "gif":
                case "bitmap":
                    tag = "<img src=\"0\" />";
                    break;
                case "mp3":
                    tag =
                        "<audio controls> " +
                        " <source src=\"0\" type=\"audio/mpeg\">" +
                        " O browser não possui suporte para tag audio." +
                        "</audio>";
                    break;
                case "ogg":
                    tag =
                        "<audio controls> " +
                        " <source src=\"0\" type=\"audio/ogg\">" +
                        " O browser não possui suporte para tag audio." +
                        "</audio>";
                    break;
                case "mp4":
                    tag = 
                        "<video width=\"320\" height=\"240\" controls>" +
                        "  <source src=\"0\" type=\"video/mp4\">" +
                        "  O browser não possui suporte para tag video." +
                        "</video>";
                    break;
                default: tag = "<a target=\"_blank\" href=\"0\">Download</a>";
                    break;
            }
        }
        return tag;
    }

    // methods used for logging
    private static void logInfo(Request req, Path tempFile) throws IOException, ServletException {
        System.out.println("Uploaded file '" + 
                getFileName(req.raw().getPart("uploaded_file")) +
                "' saved as '" + tempFile.toAbsolutePath() + "'");
    }

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    private static void configUploadDirectory(String[] args) {
        uploadDir = new File("upload");
        uploadDir.mkdir();
        staticFiles.externalLocation("upload");

    }
}