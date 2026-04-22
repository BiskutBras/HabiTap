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

@WebServlet(urlPatterns = {"/login", "/logout", "/signup", "/forgot-password"})
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

        if ("/signup".equals(path)) {
            forward(req, resp, "/WEB-INF/views/signup.jsp");
            return;
        }

        if ("/forgot-password".equals(path)) {
            forward(req, resp, "/WEB-INF/views/forgot_password.jsp");
            return;
        }

        forward(req, resp, "/WEB-INF/views/login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/signup".equals(path)) {
            handleSignup(req, resp);
            return;
        }

        if ("/forgot-password".equals(path)) {
            handleForgotPassword(req, resp);
            return;
        }

        // /login
        handleLogin(req, resp);
    }

    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
                // User doesn't exist on login page - show error
                req.setAttribute("error", "User not found. Please sign up first.");
                forward(req, resp, "/WEB-INF/views/login.jsp");
                return;
            } else {
                // validate credentials
                boolean valid = UserDAO.validateCredentials(username, password);
                if (!valid) {
                    req.setAttribute("error", "Invalid username or password.");
                    forward(req, resp, "/WEB-INF/views/login.jsp");
                    return;
                }
            }

            // store user id at login
            HttpSession session = req.getSession();
            session.setAttribute("userId", existing.getId());

            // set session user from DB
            req.getSession(true).setAttribute("user", existing);
            resp.sendRedirect(req.getContextPath() + "/habits");
        } catch (SQLException e) {
            throw new ServletException("Database error during authentication", e);
        }
    }

    private void handleSignup(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = trim(req.getParameter("username"));
        String email = trim(req.getParameter("email"));
        String password = trim(req.getParameter("password"));
        String passwordConfirm = trim(req.getParameter("passwordConfirm"));

        if (username == null || email == null || password == null || passwordConfirm == null) {
            req.setAttribute("error", "All fields are required.");
            forward(req, resp, "/WEB-INF/views/signup.jsp");
            return;
        }

        if (!password.equals(passwordConfirm)) {
            req.setAttribute("error", "Passwords do not match.");
            forward(req, resp, "/WEB-INF/views/signup.jsp");
            return;
        }

        try {
            AuthUser existing = UserDAO.findByUsername(username);
            if (existing != null) {
                req.setAttribute("error", "Username already taken. Choose another.");
                forward(req, resp, "/WEB-INF/views/signup.jsp");
                return;
            }

            // create new user
            boolean ok = UserDAO.createUser(username, email, password);
            if (!ok) {
                req.setAttribute("error", "Unable to create user. Try again.");
                forward(req, resp, "/WEB-INF/views/signup.jsp");
                return;
            }

            // retrieve and set session
            AuthUser newUser = UserDAO.findByUsername(username);
            HttpSession session = req.getSession(true);
            session.setAttribute("userId", newUser.getId());
            session.setAttribute("user", newUser);
            resp.sendRedirect(req.getContextPath() + "/habits");
        } catch (SQLException e) {
            throw new ServletException("Database error during signup", e);
        }
    }

    private void handleForgotPassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = trim(req.getParameter("email"));

        if (email == null) {
            req.setAttribute("error", "Email is required.");
            forward(req, resp, "/WEB-INF/views/forgot_password.jsp");
            return;
        }

        try {
            AuthUser user = UserDAO.findByEmail(email);
            if (user == null) {
                req.setAttribute("error", "User not found.");
                forward(req, resp, "/WEB-INF/views/forgot_password.jsp");
                return;
            }

            // Show a message that a reset link was sent.
            req.setAttribute("message", "If an account exists with that email, a password reset link has been sent.");
            forward(req, resp, "/WEB-INF/views/forgot_password.jsp");
        } catch (SQLException e) {
            throw new ServletException("Database error during password reset", e);
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

