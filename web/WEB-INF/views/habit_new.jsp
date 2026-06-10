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
                <label>Frequency</label>
                <select name="frequency">
                    <option value="daily">Daily</option>
                    <option value="weekly">Weekly</option>
                    <option value="monthly">Monthly</option>
                </select>
            </div>

            <div class="row">
                <label for="streak">Streak
                    <input id="streak" name="streak" type="number" value="0">
                </label>
            </div>

            <div class="row">
                <label>Goal (optional)</label>
                <select name="goalId">
                    <option value="0">-- No Goal --</option>
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

<script>
    function decrementStreak() {
        var input = document.getElementById('streak');
        var value = parseInt(input.value);
        if (value > 0) {
            input.value = value - 1;
        }
    }

    function incrementStreak() {
        var input = document.getElementById('streak');
        var value = parseInt(input.value);
        input.value = value + 1;
    }
</script>
</body>
</html>
