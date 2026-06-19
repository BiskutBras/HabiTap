<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Habit" %>
<%@ page import="model.Goal" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Goal Habits • HabiTap</title>
</head>
<body class="bg-light">
<%
    List<Habit> habitList = (List<Habit>) request.getAttribute("habitList");
    if (habitList == null) habitList = java.util.Collections.emptyList();

    Goal goal = (Goal) request.getAttribute("goal");

    String goalIdStr = "";
    String goalNameStr = "";
    if (goal != null) {
        goalIdStr = String.valueOf(goal.getId());
        goalNameStr = (goal.getName() != null) ? goal.getName() : "Unnamed Goal";
    } else {
        goalNameStr = "All Goals";
    }

    int completedCount = 0;
    for (Habit h : habitList) {
        if (h.isCompleted()) completedCount++;
    }
    int totalHabits = habitList.size();
    int completionRate = (totalHabits == 0) ? 0 : Math.round((completedCount * 100f) / totalHabits);

    java.util.Date now = new java.util.Date();
    String dateStr = new java.text.SimpleDateFormat("EEEE, yyyy MMM d").format(now);

    String ICON_PLUS = "<svg xmlns='http://www.w3.org/2000/svg' width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><line x1='12' y1='5' x2='12' y2='19'/><line x1='5' y1='12' x2='19' y2='12'/></svg>";
    String ICON_PENCIL = "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M12 20h9'/><path d='M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z'/></svg>";
    String ICON_TRASH = "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><polyline points='3 6 5 6 21 6'/><path d='M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6'/><path d='M10 11v6M14 11v6M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2'/></svg>";
    String ICON_CIRCLE = "<svg xmlns='http://www.w3.org/2000/svg' width='28' height='28' viewBox='0 0 24 24' fill='none' stroke='#d1d5db' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'/></svg>";
    String ICON_CIRCLE_CHECK = "<svg xmlns='http://www.w3.org/2000/svg' width='28' height='28' viewBox='0 0 24 24' fill='none' stroke='#10b981' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'/><path d='m9 12 2 2 4-4'/></svg>";
%>

<div class="habit-app-shell">
    <jsp:include page="/WEB-INF/views/includes/navbar.jsp">
        <jsp:param name="active" value="goals"/>
    </jsp:include>

    <div class="app-hero px-4 pt-4 pb-4 mb-0">
        <p class="text-muted-hero small mb-1"><%=dateStr%></p>
        <h1 class="h4 fw-bold mb-1">Goal Overview</h1>
        <p class="text-muted-hero small mb-3"><%=completedCount%> of <%=totalHabits%> completed</p>

        <div class="d-flex align-items-center justify-content-center gap-2 mb-3">
            <% if (!goalIdStr.isEmpty()) { %>
            <form method="get" action="<%=request.getContextPath()%>/goals/<%=goalIdStr%>/previous" class="d-inline">
                <button type="submit" class="btn btn-sm btn-outline-light">&larr;</button>
            </form>
            <% } %>
            <span class="fw-semibold"><%= goalNameStr %></span>
            <% if (!goalIdStr.isEmpty()) { %>
            <form method="get" action="<%=request.getContextPath()%>/goals/<%=goalIdStr%>/next" class="d-inline">
                <button type="submit" class="btn btn-sm btn-outline-light">&rarr;</button>
            </form>
            <% } %>
        </div>

        <div class="row g-2">
            <div class="col-6">
                <div class="stat-tile p-3">
                    <div class="small text-muted-hero">Goal Completion</div>
                    <div class="fs-4 fw-bold"><%=completionRate%>%</div>
                </div>
            </div>
            <div class="col-6">
                <div class="stat-tile p-3">
                    <div class="small text-muted-hero">Total Habits</div>
                    <div class="fs-4 fw-bold"><%=totalHabits%></div>
                </div>
            </div>
        </div>
    </div>

    <div class="p-3">
        <div class="d-flex justify-content-between align-items-center mb-3">
            <div>
                <h2 class="h5 fw-bold mb-0">Your Habits</h2>
                <p class="text-muted small mb-0">Tap a habit to toggle completion</p>
            </div>
            <a class="btn btn-primary rounded-circle d-flex align-items-center justify-content-center"
               style="width:2.5rem;height:2.5rem;"
               href="<%=request.getContextPath()%>/habits/new"
               title="Add habit">
                <%=ICON_PLUS%>
            </a>
        </div>

        <div class="d-flex flex-column gap-2">
            <%
                if (habitList.isEmpty()) {
            %>
            <div class="text-muted">No habits found. Create one using the + button.</div>
            <%
            } else {
                for (Habit h : habitList) {
            %>
            <div class="card habit-card-item shadow-sm">
                <div class="card-body d-flex align-items-center gap-3 py-3">
                    <a class="habit-body-link"
                       href="<%=request.getContextPath()%>/<%= h.isCompleted() ? "habits/incomplete" : "habits/complete" %>?id=<%=h.getId()%>">
                        <div class="habit-icon-box <%= h.isCompleted() ? "completed" : "" %>"></div>
                        <div>
                            <h3 class="h6 mb-1 habit-name <%= h.isCompleted() ? "completed" : "" %>"><%=h.getName()%></h3>
                            <p class="text-muted small mb-1">Frequency: <%=h.getFrequency()%> &bull; Streak: <%=h.getStreak()%></p>
                            <p class="text-muted small mb-0"><%=h.getDescription()%></p>
                        </div>
                    </a>

                    <div class="d-flex align-items-center gap-1 flex-shrink-0">
                        <a class="habit-action-btn" title="Edit" href="<%=request.getContextPath()%>/habits/edit?id=<%=h.getId()%>">
                            <%=ICON_PENCIL%>
                        </a>
                        <form method="post" action="<%=request.getContextPath()%>/habits/delete" class="d-inline m-0">
                            <input type="hidden" name="id" value="<%=h.getId()%>" />
                            <button class="habit-action-btn" title="Delete" type="submit">
                                <%=ICON_TRASH%>
                            </button>
                        </form>
                        <div class="ms-1">
                            <%= h.isCompleted() ? ICON_CIRCLE_CHECK : ICON_CIRCLE %>
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
</div>
</body>
</html>
