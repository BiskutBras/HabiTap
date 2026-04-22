package service;

import dao.GoalDAO;
import model.Goal;

import java.util.List;

public class GoalService {
    private final GoalDAO goalDAO = new GoalDAO();

    public Goal createGoal(String name, String color) {
        Goal g = new Goal(0, name, color);
        int id = goalDAO.insertAndReturnId(g);
        return new Goal(id, name, color);
    }

    public List<Goal> listGoals() {
        return goalDAO.findAll();
    }
}

