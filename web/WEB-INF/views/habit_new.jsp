<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Goal" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Create Habit • HabiTap</title>
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/views/includes/navbar.jsp">
    <jsp:param name="active" value="habits"/>
</jsp:include>

<div class="container py-4">
    <div class="row justify-content-center">
        <div class="col-lg-7 col-xl-6">
            <div class="mb-4">
                <h1 class="h3 fw-bold mb-1">Create New Habit</h1>
                <p class="text-muted mb-0">Fill in details and save to database</p>
            </div>

            <div class="card shadow-sm">
                <div class="card-body p-4">
                    <%
                        String error = (String) request.getAttribute("error");
                        if (error != null) {
                    %>
                    <div class="alert alert-danger py-2" role="alert"><%= error %></div>
                    <%
                        }

                        List<Goal> goals = (List<Goal>) request.getAttribute("goals");
                        if (goals == null) goals = java.util.Collections.emptyList();
                    %>

                    <form method="POST" action="<%=request.getContextPath()%>/habits/new">
                        <div class="mb-3">
                            <label class="form-label" for="name">Habit Name</label>
                            <input class="form-control" type="text" id="name" name="name" required maxlength="50">
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="description">Description</label>
                            <textarea class="form-control" id="description" name="description" rows="4" maxlength="500"></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="frequency">Frequency</label>
                            <select class="form-select" id="frequency" name="frequency">
                                <option value="daily">Daily</option>
                                <option value="weekly">Weekly</option>
                                <option value="monthly">Monthly</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="streak">Streak</label>
                            <input class="form-control" id="streak" name="streak" type="number" value="0">
                        </div>

                        <div class="mb-4">
                            <label class="form-label" for="goalId">Goal (optional)</label>
                            <select class="form-select" id="goalId" name="goalId">
                                <option value="0">-- No Goal --</option>
                                <% for (Goal g : goals) { %>
                                <option value="<%=g.getId()%>"><%=g.getName()%></option>
                                <% } %>
                            </select>
                        </div>

                        <div class="d-flex gap-2">
                            <button class="btn btn-primary" type="submit">Create</button>
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
