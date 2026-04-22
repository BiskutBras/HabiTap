package service;

import dao.GoalDAO;
import model.Goal;
import model.Goal.Priority;
import model.Goal.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GoalService {
    private final GoalDAO goalDAO = new GoalDAO();

    public Goal createGoal(String name, String color, int userId) {
        Goal g = new Goal(0, name, color, userId);
        int id = goalDAO.insertAndReturnId(g);
        return new Goal(id, name, color, userId, g.getCreatedDate(), g.getDueDate(), g.getPriority(), g.getStatus());
    }

    public Goal createGoal(String name, String color, int userId, LocalDateTime dueDate, Priority priority) {
        Goal g = new Goal(0, name, color, userId);
        g.setDueDate(dueDate);
        g.setPriority(priority);
        int id = goalDAO.insertAndReturnId(g);
        return new Goal(id, name, color, userId, g.getCreatedDate(), g.getDueDate(), g.getPriority(), g.getStatus());
    }

    public List<Goal> listGoals() {
        return goalDAO.findAll();
    }

    public List<Goal> listGoalsByUser(int userId) {
        return goalDAO.findAll().stream()
                .filter(goal -> goal.getUserId() == userId)
                .toList();
    }
}
