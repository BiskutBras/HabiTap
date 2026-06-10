package controller;

import model.Habit;
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

        // get userId from session
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Habit newHabit = getParameter(req);
        newHabit.setId(userId);

        try {
            habitService.createNewHabit(newHabit);
            resp.sendRedirect(req.getContextPath() + "/habits");
        } catch (IllegalArgumentException e) {
            req.setAttribute("error", e.getMessage());
            req.setAttribute("goals", goalService.listGoals());
            forward(req, resp, "/WEB-INF/views/habit_new.jsp");
        }
    }

    private void handleComplete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        habitService.markComplete(id);
        resp.sendRedirect(goalRedirect(req));
    }

    private void handleIncomplete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        habitService.markIncomplete(id);
        resp.sendRedirect(goalRedirect(req));
    }

    // redirect to edit page
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

        int habitId = parseIdOrBadRequest(req, resp);
        if (habitId == -1) return;

        Habit updatedHabit = getParameter(req);
        updatedHabit.setId(habitId);

        habitService.updateHabit(updatedHabit);
        resp.sendRedirect(goalRedirect(req));
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = parseIdOrBadRequest(req, resp);
        if (id == -1) return;

        habitService.deleteHabit(id);
        resp.sendRedirect(goalRedirect(req));
    }

    // helper method
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

    // Send user back to the goal page if the request carried a goalId,
    // otherwise to the global habit dashboard.
    private String goalRedirect(HttpServletRequest req) {
        String gid = trim(req.getParameter("goalId"));
        return req.getContextPath() + (gid != null ? "/goals/" + gid + "/habits" : "/habits");
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

    private Integer parseOptionalInt(String s) {
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return null;
        }
    }

    private Habit getParameter(HttpServletRequest req) {
        String name = trim(req.getParameter("name"));
        String description = trim(req.getParameter("description"));
        Habit.Frequency frequency = Habit.Frequency.valueOf(trim(req.getParameter("frequency"))); // daily, weekly or monthly
        String streakRaw = req.getParameter("streak");
        int streak = 0;
        if (!streakRaw.isBlank()) {
            streak = Integer.parseInt(streakRaw);
        }
        String goalIdRaw = req.getParameter("goalId");
        int goalId = 0;
        if (!goalIdRaw.isBlank()) {
            goalId = Integer.parseInt(goalIdRaw);
        }
        return new Habit(name, description, frequency, streak, goalId);
    }

}
