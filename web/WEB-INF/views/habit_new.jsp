<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Goal" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/habit_new_style.css">
    <title>Create Habit</title>


</head>
<body>
<div class="wrap">
    <div class="header">
        <div class="title">Create New Habit</div>
        <div class="subtitle">Fill in details and save to database</div>
    </div>

    <div class="card">
        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
        <div class="error"><%= error %></div>
        <%
            }
        %>

        <%
            List<Goal> goals = (List<Goal>) request.getAttribute("goals");
            if (goals == null) goals = java.util.Collections.emptyList();
        %>

        <form method="POST" action="<%=request.getContextPath()%>/habits/new">
            <div class="row">
                <label>Habit Name</label>
                <input type="text" name="name" required maxlength="50">
            </div>

            <div class="row">
                <label>Description</label>
                <textarea name="description" rows="4" maxlength="500"></textarea>
            </div>

            <div class="row">
                <label>Due Date</label>
                <input type="date" name="dueDate" required>
            </div>

            <div class="row">
                <label>Priority</label>
                <select name="priority" required>
                    <option value="">-- Select Priority --</option>
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                </select>
            </div>

            <div class="row">
                <label>Goal (optional)</label>
                <select name="goalId">
                    <option value="">-- No Goal --</option>
                    <% for (Goal g : goals) { %>
                    <option value="<%=g.getId()%>"><%=g.getName()%></option>
                    <% } %>
                </select>
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Create</button>
                <a class="btn btn-secondary" href="<%=request.getContextPath()%>/habits">Cancel</a>
            </div>
        </form>
    </div>
</div>
</body>
</html>
