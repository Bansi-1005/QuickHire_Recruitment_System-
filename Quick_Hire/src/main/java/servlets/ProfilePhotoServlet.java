package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/profilephotos/*")
public class ProfilePhotoServlet extends HttpServlet {

    private static final String PHOTO_DIR =
            "D:/QuickHireUploads/profilephotos/";

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        String fileName = request.getPathInfo();

        if (fileName == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File file = new File(PHOTO_DIR, fileName.substring(1));

        if (!file.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String mimeType =
                getServletContext().getMimeType(file.getName());

        response.setContentType(
                mimeType != null ? mimeType : "image/jpeg"
        );

        Files.copy(
                file.toPath(),
                response.getOutputStream()
        );
    }
}