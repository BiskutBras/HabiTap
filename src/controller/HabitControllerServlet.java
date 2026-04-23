package controller;

import model.Habit;
import model.Habit.Priority;
import service.HabitService;
import service.GoalService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;

@WebServlet(urlPatterns = {"/habits", "/habits/new", "/habits/complete", "/habits/incomplete", "/habits/edit", "/habits/update", "/habits/delete"})
public class HabitControllerServlet extends HttpServlet {

    private HabitService habitService;
    private GoalService goalService;

    @Override
    public void init() throws ServletException {
        this.habitService = new HabitService();
        this.goalService = new GoalService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        if ("/habits/new".equals(path)) {
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_new.jsp");
            return;
        }

        if ("/habits/edit".equals(path)) {
            handleEdit(req, resp);
            return;
        }

        // Check if this is a goal-specific habits view: /goals/{id}/habits
        if (path.startsWith("/goals/") && path.endsWith("/habits")) {
            handleGoalHabits(req, resp);
            return;
        }

        // get userId stored at login
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        // default: /habits
        req.setAttribute("habits", habitService.listHabits(userId));
        forward(req, resp, "/WEB-INF/views/habits.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();

        // buddy creates new habit
        if ("/habits/new".equals(path)) {
            handleCreate(req, resp);
            return;
        }

        // buddy sets habit as complete
        if ("/habits/complete".equals(path)) {
            handleComplete(req, resp);
            return;
        }

        // buddy sets habit as incomplete (optional toggle)
        if ("/habits/incomplete".equals(path)) {
            handleIncomplete(req, resp);
            return;
        }

        if ("/habits/update".equals(path)) {
            handleUpdate(req, resp);
            return;
        }

        if ("/habits/delete".equals(path)) {
            handleDelete(req, resp);
            return;
        }

        resp.sendError(404);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        String name = trim(req.getParameter("name"));
        String description = trim(req.getParameter("description"));
        String dueDateRaw = trim(req.getParameter("dueDate"));   // expected: YYYY-MM-DD
        String priorityRaw = trim(req.getParameter("priority")); // LOW/MEDIUM/HIGH
        Integer goalId = parseOptionalInt(trim(req.getParameter("goalId")));

        if (name == null || dueDateRaw == null || priorityRaw == null) {
            req.setAttribute("error", "Name, due date, and priority are required.");
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_new.jsp");
            return;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateRaw);
        } catch (Exception e) {
            req.setAttribute("error", "Due date must be YYYY-MM-DD (e.g., 2026-01-31).");
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_new.jsp");
            return;
        }

        Priority priority;
        try {
            priority = Priority.valueOf(priorityRaw.toUpperCase());
        } catch (Exception e) {
            req.setAttribute("error", "Priority must be LOW, MEDIUM, or HIGH.");
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_new.jsp");
            return;
        }

        // get userId from session
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        if (goalId != null) habitService.createHabitForGoal(userId, goalId, name, description == null ? "" : description, dueDate, priority);
        else habitService.createHabit(userId, name, description == null ? "" : description, dueDate, priority);
        resp.sendRedirect(req.getContextPath() + "/habits");
    }

    private void handleComplete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        habitService.markComplete(id);
        resp.sendRedirect(req.getContextPath() + "/habits");
    }

    private void handleIncomplete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        habitService.markIncomplete(id);
        resp.sendRedirect(req.getContextPath() + "/habits");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        Habit h = habitService.findHabitById(id);
        if (h == null) {
            resp.sendError(404, "Habit not found.");
            return;
        }

        req.setAttribute("habit", h);
        req.setAttribute("goals", goalService.listGoals());
        forward(req, resp, "/WEB-INF/views/habit_edit.jsp");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        String name = trim(req.getParameter("name"));
        String description = trim(req.getParameter("description"));
        String dueDateRaw = trim(req.getParameter("dueDate"));   // expected: YYYY-MM-DD
        String priorityRaw = trim(req.getParameter("priority")); // LOW/MEDIUM/HIGH

        if (name == null || dueDateRaw == null || priorityRaw == null) {
            req.setAttribute("error", "Name, due date, and priority are required.");
            // re-populate habit for the form
            Habit existing = habitService.findHabitById(id);
            req.setAttribute("habit", existing);
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_edit.jsp");
            return;
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateRaw);
        } catch (Exception e) {
            req.setAttribute("error", "Due date must be YYYY-MM-DD (e.g., 2026-01-31).");
            Habit existing = habitService.findHabitById(id);
            req.setAttribute("habit", existing);
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_edit.jsp");
            return;
        }

        Priority priority;
        try {
            priority = Priority.valueOf(priorityRaw.toUpperCase());
        } catch (Exception e) {
            req.setAttribute("error", "Priority must be LOW, MEDIUM, or HIGH.");
            Habit existing = habitService.findHabitById(id);
            req.setAttribute("habit", existing);
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_edit.jsp");
            return;
        }

        Integer goalId = parseOptionalInt(trim(req.getParameter("goalId")));
        habitService.updateHabit(id, name, description == null ? "" : description, dueDate, priority, goalId);
        resp.sendRedirect(req.getContextPath() + "/habits");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        habitService.deleteHabit(id);
        resp.sendRedirect(req.getContextPath() + "/habits");
    }

    private void handleGoalHabits(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getServletPath();
        // Extract goal ID from the path: /goals/{id}/habits
        String goalIdRaw = path.substring(path.indexOf("/goals/") + 7, path.lastIndexOf("/habits"));
        Integer goalId = parseOptionalInt(goalIdRaw);

        if (goalId == null) {
            resp.sendError(400, "Invalid goal id.");
            return;
        }

        // get userId stored at login
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        req.setAttribute("habits", habitService.listHabitsByGoal(userId, goalId));
        req.setAttribute("goal", goalService.findGoalById(goalId));
        forward(req, resp, "/WEB-INF/views/habits_goal.jsp");
    }

    private int parseIdOrBadRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idRaw = trim(req.getParameter("id"));
        int id;

        try {
            id = Integer.parseInt(idRaw);
        } catch (Exception e) {
            resp.sendError(400, "Invalid id.");
            return -1;
        }
        return id;
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(view);
        rd.forward(req, resp);
    }

    // helper method
    private String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    private Integer parseOptionalInt(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }
}
