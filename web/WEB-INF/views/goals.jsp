<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Goal" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Goals • HabiTap</title>
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/views/includes/navbar.jsp">
    <jsp:param name="active" value="goals"/>
</jsp:include>

<%
    List<Goal> goals = (List<Goal>) request.getAttribute("goals");
    if (goals == null) goals = java.util.Collections.emptyList();
%>

<div class="container py-4">
    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">Goals</h1>
            <p class="text-muted mb-0">Create goals and track their habit subtasks</p>
        </div>
        <a class="btn btn-primary" href="<%=request.getContextPath()%>/goals/new">Create Goal</a>
    </div>

    <div class="row g-3">
        <%
            if (goals.isEmpty()) {
        %>
        <div class="col-12">
            <div class="alert alert-secondary mb-0" role="alert">
                No goals yet. Create one to group habits as subtasks.
            </div>
        </div>
        <%
            } else {
                for (Goal g : goals) {
        %>
        <div class="col-sm-6 col-lg-4">
            <div class="card goal-card-link shadow-sm h-100"
                 onclick="location.href='<%=request.getContextPath()%>/goals/<%=g.getId()%>/habits'">
                <div class="d-flex h-100">
                    <div class="goal-color-bar" style="background:<%=g.getColor()%>"></div>
                    <div class="card-body">
                        <h2 class="h6 fw-bold mb-1"><%=g.getName()%></h2>
                        <p class="text-muted small mb-0">Color: <code><%=g.getColor()%></code></p>
                    </div>
                </div>
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
