<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Goal" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/goals_style.css">
    <title>Goals • HabiTap</title>
</head>
<body>
<%
    List<Goal> goals = (List<Goal>) request.getAttribute("goals");
    if (goals == null) goals = java.util.Collections.emptyList();
%>

<div class="wrap">
    <div class="topbar">
        <div>
            <div class="title">Goals</div>
            <div class="subtitle">Create goals and track their habit subtasks</div>
        </div>
        <div class="actions">
            <a class="btn btn-secondary" href="<%=request.getContextPath()%>/habits">Habits</a>
            <a class="btn btn-secondary" href="<%=request.getContextPath()%>/calendar">Calendar</a>
            <a class="btn btn-primary" href="<%=request.getContextPath()%>/goals/new">Create Goal</a>
            <a class="btn btn-ghost" href="<%=request.getContextPath()%>/logout">Logout</a>
        </div>
    </div>

    <div class="grid">
        <%
            if (goals.isEmpty()) {
        %>
        <div class="empty">
            No goals yet. Create one to group habits as subtasks.
        </div>
        <%
            } else {
                for (Goal g : goals) {
        %>
        <div class="card" onclick="location.href='<%=request.getContextPath()%>/goals/<%=g.getId()%>/habits'">
            <div class="color" style="background:<%=g.getColor()%>"></div>
            <div class="card-body">
                <div class="card-title"><%=g.getName()%></div>
                <div class="card-sub">Color: <span class="mono"><%=g.getColor()%></span></div>
            </div>
        </div>
        <%
                }
            }
        %>
    </div>
</div>
</body>
</html>

