package controller;

import model.Habit;
import model.Habit.Priority;
import model.Goal;
import service.GoalService;
import service.HabitService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/goals", "/goals/new"})
public class GoalControllerServlet extends HttpServlet {

    private GoalService goalService;
    private HabitService habitService;

    @Override
    public void init() throws ServletException {
        this.goalService = new GoalService();
        this.habitService = new HabitService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/goals/new".equals(path)) {
            forward(req, resp, "/WEB-INF/views/goal_new.jsp");
            return;
        }

        // get userId from session
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        List<Goal> goals = goalService.listGoalsByUser(userId);
        req.setAttribute("goals", goals);
        forward(req, resp, "/WEB-INF/views/goals.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if (!"/goals/new".equals(path)) {
            resp.sendError(404);
            return;
        }

        String goalName = trim(req.getParameter("goalName"));
        String goalColor = trim(req.getParameter("goalColor"));
        String goalDueDateRaw = trim(req.getParameter("goalDueDate"));
        String goalPriorityRaw = trim(req.getParameter("goalPriority"));

        if (goalName == null || goalColor == null) {
            req.setAttribute("error", "Goal name and color are required.");
            forward(req, resp, "/WEB-INF/views/goal_new.jsp");
            return;
        }

        // Parse goal due date (optional)
        LocalDate goalDueDate = null;
        if (goalDueDateRaw != null && !goalDueDateRaw.isEmpty()) {
            try {
                goalDueDate = LocalDate.parse(goalDueDateRaw);
            } catch (Exception e) {
                req.setAttribute("error", "Goal due date must be a valid date.");
                forward(req, resp, "/WEB-INF/views/goal_new.jsp");
                return;
            }
        }

        // Parse goal priority
        Goal.Priority goalPriority;
        try {
            goalPriority = Goal.Priority.valueOf(goalPriorityRaw != null ? goalPriorityRaw.toUpperCase() : "MEDIUM");
        } catch (Exception e) {
            req.setAttribute("error", "Goal priority must be LOW, MEDIUM, or HIGH.");
            forward(req, resp, "/WEB-INF/views/goal_new.jsp");
            return;
        }

        // get userId from session
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        Goal created = goalService.createGoal(goalName, goalColor, userId, goalDueDate.atStartOfDay(), goalPriority);

        // Subtasks (habits)
        String[] habitNames = req.getParameterValues("habitName");
        String[] habitDescriptions = req.getParameterValues("habitDescription");
        String[] habitDueDates = req.getParameterValues("habitDueDate");
        String[] habitPriorities = req.getParameterValues("habitPriority");

        if (habitNames != null) {
            for (int i = 0; i < habitNames.length; i++) {
                String hName = trim(habitNames[i]);
                String hDesc = habitDescriptions != null && i < habitDescriptions.length ? trim(habitDescriptions[i]) : null;
                String dueRaw = habitDueDates != null && i < habitDueDates.length ? trim(habitDueDates[i]) : null;
                String prRaw = habitPriorities != null && i < habitPriorities.length ? trim(habitPriorities[i]) : null;

                // Ignore completely blank rows
                if (hName == null && dueRaw == null && prRaw == null && hDesc == null) continue;

                if (hName == null || dueRaw == null || prRaw == null) {
                    req.setAttribute("error", "Each subtask requires a title, due date, and priority (or remove the row).");
                    forward(req, resp, "/WEB-INF/views/goal_new.jsp");
                    return;
                }

                LocalDate dueDate;
                try {
                    dueDate = LocalDate.parse(dueRaw);
                } catch (Exception e) {
                    req.setAttribute("error", "Subtask due date must be YYYY-MM-DD.");
                    forward(req, resp, "/WEB-INF/views/goal_new.jsp");
                    return;
                }

                Priority priority;
                try {
                    priority = Habit.Priority.valueOf(prRaw.toUpperCase());
                } catch (Exception e) {
                    req.setAttribute("error", "Subtask priority must be LOW, MEDIUM, or HIGH.");
                    forward(req, resp, "/WEB-INF/views/goal_new.jsp");
                    return;
                }

                habitService.createHabitForGoal(userId, created.getId(), hName, hDesc == null ? "" : hDesc, dueDate, priority);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/goals");
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
