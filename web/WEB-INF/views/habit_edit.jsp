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
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Edit Habit • HabiTap</title>
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/views/includes/navbar.jsp">
    <jsp:param name="active" value="habits"/>
</jsp:include>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-7 col-xl-6">
            <div class="mb-4">
                <h1 class="h3 fw-bold mb-1">Edit Habit</h1>
                <p class="text-muted mb-0">Update habit details and save changes</p>
            </div>

            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <%
                        if (error != null) {
                    %>
                    <div class="alert alert-danger py-2" role="alert"><%= error %></div>
                    <%
                        }
                    %>

                    <form method="post" action="<%=request.getContextPath()%>/habits/update">
                        <input type="hidden" name="id" value="<%=habit.getId()%>"/>

                        <div class="mb-3">
                            <label class="form-label" for="name">Habit Name</label>
                            <input class="form-control" id="name" name="name" type="text" required maxlength="50"
                                   value="<%=habit.getName()%>"/>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="description">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" maxlength="500"><%=habit.getDescription()%></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="frequency">Frequency</label>
                            <select class="form-select" id="frequency" name="frequency" required>
                                <option value="daily" <%= habit.getFrequency() != null && habit.getFrequency().name().equals("daily") ? "selected" : "" %>>Daily</option>
                                <option value="weekly" <%= habit.getFrequency() != null && habit.getFrequency().name().equals("weekly") ? "selected" : "" %>>Weekly</option>
                                <option value="monthly" <%= habit.getFrequency() != null && habit.getFrequency().name().equals("monthly") ? "selected" : "" %>>Monthly</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="streak">Streak</label>
                            <input class="form-control" id="streak" name="streak" type="number" value="<%= habit.getStreak() %>">
                        </div>

                        <div class="mb-4">
                            <label class="form-label" for="goalId">Goal (optional)</label>
                            <select class="form-select" id="goalId" name="goalId">
                                <option value="0" <%= habit.getGoalId() == 0 ? "selected" : "" %>>-- No Goal --</option>
                                <% for (Goal g : goals) { %>
                                <option value="<%=g.getId()%>" <%= habit.getGoalId() != 0 && habit.getGoalId() == g.getId() ? "selected" : "" %>>
                                    <%=g.getName()%>
                                </option>
                                <% } %>
                            </select>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary">Save</button>
                            <a class="btn btn-outline-secondary" href="<%=request.getContextPath()%>/habits">Cancel</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
