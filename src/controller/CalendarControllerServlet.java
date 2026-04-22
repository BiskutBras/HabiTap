package controller;

import model.CalendarItem;
import model.CalendarItem.Priority;
import model.CalendarItem.Type;
import service.CalendarService;
import service.HabitService;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;

@WebServlet(urlPatterns = {"/calendar", "/calendar/items/create", "/calendar/items/update", "/calendar/items/delete"})
public class CalendarControllerServlet extends HttpServlet {

    private HabitService habitService;
    private CalendarService calendarService;

    @Override
    public void init() throws ServletException {
        this.habitService = new HabitService();
        this.calendarService = new CalendarService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        // get userId from current session
        HttpSession session = req.getSession(false);
        Integer userId = (Integer) session.getAttribute("userId");

        req.setAttribute("habits", habitService.listHabits(userId));
        req.setAttribute("calendarItems", calendarService.listItems());
        forward(req, resp, "/WEB-INF/views/calendar.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();

        if ("/calendar/items/create".equals(path)) {
            handleCreate(req, resp);
            return;
        }
        if ("/calendar/items/update".equals(path)) {
            handleUpdate(req, resp);
            return;
        }
        if ("/calendar/items/delete".equals(path)) {
            handleDelete(req, resp);
            return;
        }

        resp.sendError(404);
    }

    private void handleCreate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String typeRaw = trim(req.getParameter("type"));
        String title = trim(req.getParameter("title"));
        String dateRaw = trim(req.getParameter("date"));

        if (typeRaw == null || title == null || dateRaw == null) {
            json(resp, 400, "{\"ok\":false,\"error\":\"Type, title, and date are required.\"}");
            return;
        }

        Type type;
        try { type = Type.valueOf(typeRaw.toUpperCase()); }
        catch (Exception e) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid type.\"}"); return; }

        LocalDate date;
        try { date = LocalDate.parse(dateRaw); }
        catch (Exception e) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid date.\"}"); return; }

        CalendarItem item = new CalendarItem(0, type, title, date);

        if (type == Type.EVENT) {
            item.setCategory(trim(req.getParameter("category")));
            item.setDescription(trim(req.getParameter("description")));
            item.setStartTime(parseTime(trim(req.getParameter("startTime"))));
            item.setEndTime(parseTime(trim(req.getParameter("endTime"))));
        } else {
            item.setDescription(trim(req.getParameter("description")));
            String prRaw = trim(req.getParameter("priority"));
            if (prRaw == null) {
                json(resp, 400, "{\"ok\":false,\"error\":\"Priority is required for tasks.\"}");
                return;
            }
            try { item.setPriority(Priority.valueOf(prRaw.toUpperCase())); }
            catch (Exception e) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid priority.\"}"); return; }
        }

        CalendarItem created = calendarService.create(item);
        json(resp, 200, "{\"ok\":true,\"item\":" + itemToJson(created) + "}");
    }

    private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = parseId(req);
        if (id <= 0) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid id.\"}"); return; }

        CalendarItem existing = calendarService.findById(id);
        if (existing == null) { json(resp, 404, "{\"ok\":false,\"error\":\"Not found.\"}"); return; }

        String title = trim(req.getParameter("title"));
        String dateRaw = trim(req.getParameter("date"));
        if (title == null || dateRaw == null) { json(resp, 400, "{\"ok\":false,\"error\":\"Title and date are required.\"}"); return; }

        LocalDate date;
        try { date = LocalDate.parse(dateRaw); }
        catch (Exception e) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid date.\"}"); return; }

        existing.setTitle(title);
        existing.setDate(date);

        if (existing.getType() == Type.EVENT) {
            existing.setCategory(trim(req.getParameter("category")));
            existing.setDescription(trim(req.getParameter("description")));
            existing.setStartTime(parseTime(trim(req.getParameter("startTime"))));
            existing.setEndTime(parseTime(trim(req.getParameter("endTime"))));
            existing.setPriority(null);
        } else {
            existing.setDescription(trim(req.getParameter("description")));
            String prRaw = trim(req.getParameter("priority"));
            if (prRaw == null) { json(resp, 400, "{\"ok\":false,\"error\":\"Priority is required for tasks.\"}"); return; }
            try { existing.setPriority(Priority.valueOf(prRaw.toUpperCase())); }
            catch (Exception e) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid priority.\"}"); return; }
            existing.setCategory(null);
            existing.setStartTime(null);
            existing.setEndTime(null);
        }

        boolean ok = calendarService.update(existing);
        if (!ok) { json(resp, 500, "{\"ok\":false,\"error\":\"Update failed.\"}"); return; }

        json(resp, 200, "{\"ok\":true,\"item\":" + itemToJson(existing) + "}");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = parseId(req);
        if (id <= 0) { json(resp, 400, "{\"ok\":false,\"error\":\"Invalid id.\"}"); return; }

        boolean ok = calendarService.delete(id);
        if (!ok) { json(resp, 404, "{\"ok\":false,\"error\":\"Not found.\"}"); return; }

        json(resp, 200, "{\"ok\":true}");
    }

    private void forward(HttpServletRequest req, HttpServletResponse resp, String view)
            throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher(view);
        rd.forward(req, resp);
    }

    private int parseId(HttpServletRequest req) {
        String idRaw = trim(req.getParameter("id"));
        if (idRaw == null) return -1;
        try { return Integer.parseInt(idRaw); } catch (Exception e) { return -1; }
    }

    private LocalTime parseTime(String raw) {
        if (raw == null) return null;
        try { return LocalTime.parse(raw); } catch (Exception e) { return null; }
    }

    private void json(HttpServletResponse resp, int status, String body) throws IOException {
        resp.setStatus(status);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        try (PrintWriter out = resp.getWriter()) {
            out.write(body);
        }
    }

    private String itemToJson(CalendarItem item) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"id\":").append(item.getId()).append(",");
        sb.append("\"type\":\"").append(esc(item.getType().name())).append("\",");
        sb.append("\"title\":\"").append(esc(item.getTitle())).append("\",");
        sb.append("\"date\":\"").append(esc(item.getDate().toString())).append("\",");
        sb.append("\"startTime\":").append(item.getStartTime() == null ? "null" : "\"" + esc(item.getStartTime().toString()) + "\"").append(",");
        sb.append("\"endTime\":").append(item.getEndTime() == null ? "null" : "\"" + esc(item.getEndTime().toString()) + "\"").append(",");
        sb.append("\"category\":").append(item.getCategory() == null ? "null" : "\"" + esc(item.getCategory()) + "\"").append(",");
        sb.append("\"description\":").append(item.getDescription() == null ? "null" : "\"" + esc(item.getDescription()) + "\"").append(",");
        sb.append("\"priority\":").append(item.getPriority() == null ? "null" : "\"" + esc(item.getPriority().name()) + "\"");
        sb.append("}");
        return sb.toString();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "")
                .replace("\n", "\\n");
    }

    private String trim(String s) {
        if (s == null) return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }
}

