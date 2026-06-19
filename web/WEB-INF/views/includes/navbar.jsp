<%@ page contentType="text/html; charset=UTF-8" %>
<%
    String active = request.getParameter("active");
    if (active == null) active = "";
    String ctx = request.getContextPath();
%>
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="<%= ctx %>/habits">HabiTap</a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#mainNav"
                aria-controls="mainNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="mainNav">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link<%= "habits".equals(active) ? " active" : "" %>" href="<%= ctx %>/habits">Habits</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link<%= "goals".equals(active) ? " active" : "" %>" href="<%= ctx %>/goals">Goals</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link<%= "calendar".equals(active) ? " active" : "" %>" href="<%= ctx %>/calendar">Calendar</a>
                </li>
            </ul>
            <div class="d-flex gap-2">
                <a class="btn btn-outline-light btn-sm" href="<%= ctx %>/logout">Logout</a>
            </div>
        </div>
    </div>
</nav>
