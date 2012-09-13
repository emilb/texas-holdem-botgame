package se.cygni.texasholdem.webclient;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class StaticFileServlet extends HttpServlet {

    // Warning! If this value is changed be sure to update Launcher as well.
    public static String SYS_PROP_STATIC_DIR = "static.dir";

    private static Logger log = LoggerFactory
            .getLogger(StaticFileServlet.class);

    private static final String BASE_URL = "/player/";

    private String basePath = "/tmp/";

    protected void serve(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String filePath = getFilePath(request.getRequestURI());

        try {
            File file = getBestFileMatch(filePath);

            writeFileToResponse(file, response);

        } catch (FileNotFoundException fnfe) {
            log.warn("{} not found.", filePath);
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private void writeFileToResponse(File f, HttpServletResponse response) throws IOException {

        String mimeType = getServletContext().getMimeType(f.getName());

        response.setContentType(mimeType);

        ServletOutputStream output = response.getOutputStream();
        InputStream input = null;
        try {

            input = new FileInputStream(f);

            byte[] buffer = new byte[2048];
            int bytesRead;
            while ((bytesRead = input.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }

        } finally {
            output.close();
            if (input != null) {
                input.close();
            }
        }
    }

    /**
     * Returns a File handle for requested file. If the file is not found, is not readable
     * or is a directory attempts are made to get index.htm(l) under the same path instead.
     *
     * @param filePath
     *
     * @return File to requested path
     *
     * @throws FileNotFoundException
     */
    private File getBestFileMatch(String filePath) throws FileNotFoundException {
        try {
            return getFile(filePath);
        } catch (FileNotFoundException e) {
        }

        try {
            return getFile(filePath + File.separator + "index.html");
        } catch (FileNotFoundException e) {
        }

        try {
            return getFile(filePath + File.separator + "index.htm");
        } catch (FileNotFoundException e) {
        }

        throw new FileNotFoundException("Could not find " + filePath);
    }

    private File getFile(String filePath) throws FileNotFoundException {

        File f = new File(filePath);

        if (!f.exists() || !f.canRead() || !f.isFile()) {
            throw new FileNotFoundException("Could not find " + filePath);
        }

        return f;
    }

    private String getFilePath(String requestURI) {

        return basePath + StringUtils.substringAfter(requestURI, BASE_URL);
    }


    @Override
    public void init() throws ServletException {
        super.init();
        basePath = System.getProperty(SYS_PROP_STATIC_DIR);

        if (!StringUtils.endsWith(basePath, File.separator)) {
            basePath = basePath + File.separator;
        }

        log.info("StaticFileServlet initialized, static directory: {}", basePath);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        serve(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        serve(request, response);
    }

}
