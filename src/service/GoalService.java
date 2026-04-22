package service;

import dao.GoalDAO;
import model.Goal;

import java.util.List;

public class GoalService {
    private final GoalDAO goalDAO = new GoalDAO();

    public Goal createGoal(String name, String color, int userId) {
        Goal g = new Goal(0, name, color, userId );
        int id = goalDAO.insertAndReturnId(g);
        return new Goal(id, name, color, userId);
    }

    public List<Goal> listGoals() {
        return goalDAO.findAll();
    }
}

