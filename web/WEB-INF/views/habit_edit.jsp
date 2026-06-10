<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ page import="model.Habit" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Goal" %>

<%
    Habit habit = (Habit) request.getAttribute("habit");
    if (habit == null) {
        response.sendError(404);
        return;
    }
    String error = (String) request.getAttribute("error");
    List<Goal> goals = (List<Goal>) request.getAttribute("goals");
    if (goals == null) goals = java.util.Collections.emptyList();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/habit_edit_style.css">
    <title>Edit Habit</title>


</head>

<body>
<div class="wrap">

    <div class="header">
        <div class="title">Edit Habit</div>
        <div class="subtitle">Update habit details and save changes</div>
    </div>

    <div class="card">

        <%
            if (error != null) {
        %>
        <div class="error"><%= error %></div>
        <%
            }
        %>

        <form method="post" action="<%=request.getContextPath()%>/habits/update">
            <input type="hidden" name="id" value="<%=habit.getId()%>"/>

            <div class="row">
                <label for="name">Habit Name</label>
                <input id="name" name="name" type="text" required maxlength="50"
                       value="<%=habit.getName()%>"/>
            </div>

            <div class="row">
                <label for="description">Description</label>
                <textarea id="description" name="description" rows="4" maxlength="500"><%=habit.getDescription()%></textarea>
            </div>

            <div class="row">
                <label for="frequency">Frequency</label>
                <select id="frequency" name="frequency" required>
                    <option value="daily" <%= habit.getFrequency() != null && habit.getFrequency().name().equals("daily") ? "selected" : "" %>>Daily</option>
                    <option value="weekly" <%= habit.getFrequency() != null && habit.getFrequency().name().equals("weekly") ? "selected" : "" %>>Weekly</option>
                    <option value="monthly" <%= habit.getFrequency() != null && habit.getFrequency().name().equals("monthly") ? "selected" : "" %>>Monthly</option>
                </select>
            </div>

            <div class="row">
                <label for="streak">Streak
                <input name="streak" type="number">
                </label>
            </div>

            <div class="row">
                <label for="goalId">Goal (optional)</label>
                <select id="goalId" name="goalId">
                    <option value="" <%= habit.getGoalId() == 0 ? "selected" : "" %>>-- No Goal --</option>
                    <% for (Goal g : goals) { %>
                    <option value="<%=g.getId()%>" <%= habit.getGoalId() != 0 && habit.getGoalId() == g.getId() ? "selected" : "" %>>
                        <%=g.getName()%>
                    </option>
                    <% } %>
                </select>
            </div>

            <div class="actions">
                <button type="submit" class="btn btn-primary">Save</button>
                <a class="btn btn-secondary" href="<%=request.getContextPath()%>/habits">Cancel</a>
            </div>
        </form>
    </div>

</div>
</body>
</html>
