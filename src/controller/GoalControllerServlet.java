package controller;

import model.Habit;
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
import java.util.ArrayList;
import java.util.List;

// handle /goals/${goalId}/habits, /goals/new and /goals/${goalId}/next
@WebServlet(urlPatterns = {"/goals/*", "/goals/new",})
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
        String path = "/goals" + req.getPathInfo();

        if ("/goals/new".equals(path)) {
            forward(req, resp, "/WEB-INF/views/goal_new.jsp");
            return;
        }

        // Check if this is a goal-specific habits view: /goals/{id}/habits
        if (path.startsWith("/goals/") && path.endsWith("/habits")) {
            handleGoalHabits(req, resp);
            return;
        }

        // get userId from session
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        if (path.startsWith("/goals") && (path.endsWith("/next") || path.endsWith("/previous"))) {
            // /goals/${goalId}/action
            String[] parts = path.split("/");
            int goalId = Integer.parseInt(parts[2]);
            String action = parts[3];

            if (action.equals("next")) {
                goalId += 1;
            } else if (action.equals("previous")) {
                goalId -= 1;
            }

            List<Habit> habitList = habitService.listHabitsByGoal(userId, goalId);

            req.setAttribute("habitList", habitList);
            req.setAttribute("goal", goalService.findGoalById(goalId));
            forward(req, resp, "/WEB-INF/views/habits_goal.jsp");
        }

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
        String[] habitName = req.getParameterValues("habitName");
        String[] habitDescriptions = req.getParameterValues("habitDescription");


        if (habitName != null) {
            for (int i = 0; i < habitName.length; i++) {
                String hName = trim(habitName[i]);
                String hDesc = habitDescriptions != null && i < habitDescriptions.length ? trim(habitDescriptions[i]) : null;


                // Ignore completely blank rows


                Habit newHabit = new Habit(hName, hDesc, Habit.Frequency.daily, 0, 0);
                habitService.createNewHabit(newHabit);
            }
        }

        resp.sendRedirect(req.getContextPath() + "/goals");
    }

    private void handleGoalHabits(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String path = req.getRequestURI().replace(req.getContextPath(), "");
        // Extract goal ID from the path: /goals/{id}/habits
        String goalIdRaw = path.substring(path.indexOf("/goals/") + 7, path.lastIndexOf("/habits"));
        int goalId = Integer.parseInt(goalIdRaw);

        // get userId stored at login
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        req.setAttribute("habitList", habitService.listHabitsByGoal(userId, goalId));
        req.setAttribute("goal", goalService.findGoalById(goalId));
        forward(req, resp, "/WEB-INF/views/habits_goal.jsp");
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
}
