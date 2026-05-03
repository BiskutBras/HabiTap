package service;

import dao.HabitDAO;
import model.Habit;
import java.util.List;

public class HabitService {

    private final HabitDAO habitDAO = new HabitDAO();

    public void createNewHabit(Habit newHabit) {

        // handle no name habits
        if (newHabit.getName() == null || newHabit.getName().isBlank()) {
            throw new IllegalArgumentException("Habit name is required");
        }

        habitDAO.insert(newHabit);
    }

    public List<Habit> listHabits(int userId) {
        return habitDAO.findAll(userId);
    }

    public boolean markComplete(int habitId) {
        return habitDAO.setCompleted(habitId, true);
    }

    public boolean markIncomplete(int habitId) {
        return habitDAO.setCompleted(habitId, false);
    }


    public Habit findHabitById(int habitId) {
        return habitDAO.findById(habitId);
    }

    public boolean updateHabit(Habit updatedHabit) {
        return habitDAO.update(updatedHabit);
    }

    public boolean deleteHabit(int id) {
        return habitDAO.deleteById(id);
    }

    public List<Habit> listHabitsByGoal(int userId, int goalId) {
        return habitDAO.findAll(userId).stream()
                .filter(habit -> habit.getGoalId() == goalId)
                .toList();
    }
}
