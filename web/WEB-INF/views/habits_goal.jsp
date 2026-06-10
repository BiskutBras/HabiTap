<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="model.Habit" %>
<%@ page import="model.Goal" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/habits_style.css">

    <title>Habits for Goal &bull; HabiTap</title>
</head>

<body>
<%
    List<Habit> habitsList = (List<Habit>) request.getAttribute("habits");
    Goal goal = (Goal) request.getAttribute("goal");
    if (habitsList == null) habitsList = java.util.Collections.emptyList();

    int completedCount = 0;
    for (Habit h : habitsList) {
        if (h.isCompleted()) completedCount++;
    }
    int totalHabits = habitsList.size();
    int completionRate = (totalHabits == 0) ? 0 : Math.round((completedCount * 100f) / totalHabits);

    String dateStr = new SimpleDateFormat("EEEE, yyyy MMM d").format(new java.util.Date());

    String goalDueDateStr = "";
    if (goal != null && goal.getDueDate() != null) {
        goalDueDateStr = DateTimeFormatter.ofPattern("MMM d, yyyy").format(goal.getDueDate());
    }

    String goalColor = (goal != null && goal.getColor() != null) ? goal.getColor() : "#2563eb";

    // SVG icons used in the page
    String ICON_PLUS = "<svg xmlns='http://www.w3.org/2000/svg' width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><line x1='12' y1='5' x2='12' y2='19'/><line x1='5' y1='12' x2='19' y2='12'/></svg>";
    String ICON_STAR = "<svg xmlns='http://www.w3.org/2000/svg' width='20' height='20' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><polygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'/></svg>";
    String ICON_PENCIL = "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><path d='M12 20h9'/><path d='M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z'/></svg>";
    String ICON_TRASH = "<svg xmlns='http://www.w3.org/2000/svg' width='16' height='16' viewBox='0 0 24 24' fill='none' stroke='currentColor' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><polyline points='3 6 5 6 21 6'/><path d='M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6'/><path d='M10 11v6M14 11v6M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2'/></svg>";
    String ICON_CIRCLE = "<svg xmlns='http://www.w3.org/2000/svg' width='28' height='28' viewBox='0 0 24 24' fill='none' stroke='#d1d5db' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'/></svg>";
    String ICON_CIRCLE_CHECK = "<svg xmlns='http://www.w3.org/2000/svg' width='28' height='28' viewBox='0 0 24 24' fill='none' stroke='#10b981' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'><circle cx='12' cy='12' r='10'/><path d='m9 12 2 2 4-4'/></svg>";
%>

<div class="habit-tracker">
    <div class="header">
        <div class="header-top">
            <div>
                <p class="header-date"><%= dateStr %></p>
                <h1 class="header-title">Habits for Goal</h1>
                <p class="header-subtitle"><%= completedCount %> of <%= totalHabits %> completed</p>
            </div>
            <div class="header-actions">
                <a class="nav-button" href="<%=request.getContextPath()%>/goals">Back to Goals</a>
                <button class="nav-button" onclick="location.href='<%=request.getContextPath()%>/calendar'">Calendar</button>
                <button class="nav-button ghost" onclick="location.href='<%=request.getContextPath()%>/logout'">Logout</button>
                <div class="header-icon"><%= ICON_STAR %></div>
            </div>
        </div>

        <% if (goal != null) { %>
        <div class="goal-info">
            <div class="goal-color" style="background: <%= goalColor %>"></div>
            <div class="goal-details">
                <h2 class="goal-name"><%= goal.getName() %></h2>
                <p class="goal-meta">
                    Priority: <%= goal.getPriority() %><% if (goal.getDueDate() != null) { %> &bull; Due: <%= goalDueDateStr %><% } %>
                </p>
            </div>
        </div>
        <% } else { %>
        <div class="goal-info">
            <div class="goal-details">
                <h2 class="goal-name">Goal unavailable</h2>
                <p class="goal-meta">This goal may have been deleted. <a href="<%=request.getContextPath()%>/goals">Back to Goals</a>.</p>
            </div>
        </div>
        <% } %>

        <div class="stats-container">
            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Completion</span>
                </div>
                <p class="stat-value"><%= completionRate %>%</p>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Total Habits</span>
                </div>
                <p class="stat-value"><%= totalHabits %></p>
            </div>
        </div>
    </div>

    <div class="main-content">
        <section class="section">
            <div class="section-header">
                <div>
                    <h2 class="section-title">Goal Habits</h2>
                    <p class="section-subtitle">Tap a habit to toggle completion</p>
                </div>
                <form method="GET" action="<%=request.getContextPath()%>/habits/new" style="display:inline">
                    <% if (goal != null) { %>
                    <input type="hidden" name="goalId" value="<%= goal.getId() %>">
                    <% } %>
                    <button class="add-button" type="submit"><%= ICON_PLUS %></button>
                </form>
            </div>

            <div class="habits-list">
                <% if (habitsList.isEmpty()) { %>
                    <div style="color:#6b7280;">No habits found for this goal. Create one using the + button.</div>
                <% } else { %>
                    <% for (Habit h : habitsList) { %>
                    <div class="habit-card">
                        <div class="habit-icon <%= h.isCompleted() ? "completed" : "" %>">
                            <%
                                if (h.getFrequency() == null) {
                            %>
                                <span>📝</span>
                            <%
                                } else {
                                    String f = h.getFrequency().name();
                                    if ("DAILY".equals(f)) {
                            %>
                                        <span>🌱</span>
                            <%
                                    } else if ("WEEKLY".equals(f)) {
                            %>
                                        <span>⚡</span>
                            <%
                                    } else if ("MONTHLY".equals(f)) {
                            %>
                                        <span>🔥</span>
                            <%
                                    } else {
                            %>
                                        <span>📝</span>
                            <%
                                    }
                                }
                            %>
                        </div>

                        <div class="habit-info">
                            <h3 class="habit-name <%= h.isCompleted() ? "completed" : "" %>"><%= h.getName() %></h3>
                            <p class="habit-streak">Frequency: <%= h.getFrequency() %> &bull; Streak: <%= h.getStreak() %></p>
                            <% if (h.getDescription() != null) { %>
                            <p class="habit-description"><%= h.getDescription() %></p>
                            <% } %>
                        </div>

                        <div class="habit-actions">
                            <a class="habit-action-button" title="Edit"
                               href="<%=request.getContextPath()%>/habits/edit?id=<%= h.getId() %>&goalId=<%= goal != null ? goal.getId() : "" %>">
                                <%= ICON_PENCIL %>
                            </a>
                            <form method="POST" action="<%=request.getContextPath()%>/habits/delete" style="display:inline"
                                  onsubmit="return confirm('Delete this habit? This action cannot be undone.');">
                                <input type="hidden" name="id" value="<%= h.getId() %>">
                                <input type="hidden" name="goalId" value="<%= goal != null ? goal.getId() : "" %>">
                                <button type="submit" class="habit-action-button" title="Delete"><%= ICON_TRASH %></button>
                            </form>
                            <form method="POST"
                                  action="<%=request.getContextPath()%>/habits/<%= h.isCompleted() ? "incomplete" : "complete" %>"
                                  style="display:inline">
                                <input type="hidden" name="id" value="<%= h.getId() %>">
                                <input type="hidden" name="goalId" value="<%= goal != null ? goal.getId() : "" %>">
                                <button type="submit" class="habit-check" title="Toggle">
                                    <%= h.isCompleted() ? ICON_CIRCLE_CHECK : ICON_CIRCLE %>
                                </button>
                            </form>
                        </div>
                    </div>
                    <% } %>
                <% } %>
            </div>
        </section>
    </div>
</div>

</body>
</html>
