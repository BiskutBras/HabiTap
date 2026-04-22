package controller;

import model.AuthUser;
import dao.UserDAO;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(urlPatterns = {"/login", "/logout"})
public class AuthControllerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/logout".equals(path)) {
            HttpSession session = req.getSession(false);
            if (session != null) session.invalidate();
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        forward(req, resp, "/WEB-INF/views/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String email = trim(req.getParameter("email"));
        String password = trim(req.getParameter("password"));

        if (username == null || email == null || password == null) {
            req.setAttribute("error", "Username, email, and password are required.");
            forward(req, resp, "/WEB-INF/views/login.jsp");
            return;
        }

        try {
            AuthUser existing = UserDAO.findByUsername(username);
            if (existing == null) {
                // register new user
                boolean ok = UserDAO.createUser(username, email, password);
                if (!ok) {
                    req.setAttribute("error", "Unable to create user. Try again.");
                    forward(req, resp, "/WEB-INF/views/login.jsp");
                    return;
                }
                existing = UserDAO.findByUsername(username);
            } else {
                // validate credentials
                boolean valid = UserDAO.validateCredentials(username, password);
                if (!valid) {
                    req.setAttribute("error", "Invalid username or password.");
                    forward(req, resp, "/WEB-INF/views/login.jsp");
                    return;
                }
            }

            // set session user from DB
            req.getSession(true).setAttribute("user", existing);
            resp.sendRedirect(req.getContextPath() + "/habits");
        } catch (SQLException e) {
            throw new ServletException("Database error during authentication", e);
        }
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(view);
        rd.forward(req, resp);
    }

    private String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

