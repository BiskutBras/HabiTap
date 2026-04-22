package service;

import dao.CalendarItemDAO;
import model.CalendarItem;

import java.util.List;

public class CalendarService {
    private final CalendarItemDAO dao = new CalendarItemDAO();

    public List<CalendarItem> listItems() {
        return dao.findAll();
    }

    public CalendarItem findById(int id) {
        return dao.findById(id);
    }

    public CalendarItem create(CalendarItem item) {
        int id = dao.insertAndReturnId(item);
        CalendarItem created = new CalendarItem(id, item.getType(), item.getTitle(), item.getDate());
        created.setStartTime(item.getStartTime());
        created.setEndTime(item.getEndTime());
        created.setCategory(item.getCategory());
        created.setDescription(item.getDescription());
        created.setPriority(item.getPriority());
        return created;
    }

    public boolean update(CalendarItem item) {
        return dao.update(item);
    }

    public boolean delete(int id) {
        return dao.deleteById(id);
    }
}

