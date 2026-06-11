<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Habit" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/habits_style.css">

    <title>Habit Tracker App</title>



</head>

<body>
<%
    List<Habit> habitList = (List<Habit>) request.getAttribute("habitList");
    if (habitList == null) habitList = java.util.Collections.emptyList();

    int completedCount = 0;
    for (Habit h : habitList) {
        if (h.isCompleted()) completedCount++;
    }
    int totalHabits = habitList.size();
    int completionRate = (totalHabits == 0) ? 0 : Math.round((completedCount * 100f) / totalHabits);

    java.util.Date now = new java.util.Date();
    String dateStr = new java.text.SimpleDateFormat("EEEE, yyyy MMM d").format(now);

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
                <p class="header-date"><%=dateStr%>
                </p>
                <h1 class="header-title">Habit Dashboard</h1>
                <p class="header-subtitle"><%=completedCount%> of <%=totalHabits%> completed</p>
            </div>
            <div class="header-actions">
                <a class="nav-button" href="<%=request.getContextPath()%>/goals">Goals</a>
                <a class="nav-button" href="<%=request.getContextPath()%>/calendar">Calendar</a>
                <a class="nav-button ghost" href="<%=request.getContextPath()%>/logout">Logout</a>
                <div class="header-icon"><%=ICON_STAR%>
                </div>
            </div>
        </div>

        <div class="stats-container">
            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Completion</span>
                </div>
                <p class="stat-value"><%=completionRate%>%</p>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-label">Total Habits</span>
                </div>
                <p class="stat-value"><%=totalHabits%>
                </p>
            </div>
        </div>
    </div>

    <div class="main-content">
        <section class="section">
            <div class="section-header">
                <div>
                    <h2 class="section-title">Your Habits</h2>
                    <p class="section-subtitle">Tap a habit to toggle completion</p>
                </div>
                <a class="add-button" href="<%=request.getContextPath()%>/habits/new">
                    <%=ICON_PLUS%>
                </a>
            </div>

            <div class="habits-list">
                <%
                    if (habitList.isEmpty()) {
                %>
                <div style="color:#6b7280;">No habits found. Create one using the + button.</div>
                <%
                } else {
                    for (Habit h : habitList) {
                %>
                <div class="habit-card">
                    <%-- Make the left part of the card a plain link that performs the toggle via server-side endpoint --%>
                    <a class="habit-body" href="<%=request.getContextPath()%>/<%= h.isCompleted() ? "habits/incomplete" : "habits/complete" %>?id=<%=h.getId()%>">
                        <div class="habit-icon <%= h.isCompleted() ? "completed" : "" %>"></div>

                        <%--name, frequency and streak--%>
                        <div class="habit-info">
                            <h3 class="habit-name <%= h.isCompleted() ? "completed" : "" %>"><%=h.getName()%></h3>
                            <p class="habit-streak">Frequency: <%=h.getFrequency()%> &bull; Streak: <%=h.getStreak()%></p>
                            <p class="habit-description"><%=h.getDescription()%></p>
                        </div>
                    </a>

                    <div class="habit-actions">
                        <a class="habit-action-button" title="Edit" href="<%=request.getContextPath()%>/habits/edit?id=<%=h.getId()%>">
                            <%=ICON_PENCIL%>
                        </a>
                        <form method="post" action="<%=request.getContextPath()%>/habits/delete" style="display:inline; margin:0; padding:0;">
                            <input type="hidden" name="id" value="<%=h.getId()%>" />
                            <button class="habit-action-button" title="Delete" type="submit" style="background:none;border:none;padding:0;">
                                <%=ICON_TRASH%>
                            </button>
                        </form>
                        <div class="habit-check">
                            <%= h.isCompleted() ? ICON_CIRCLE_CHECK : ICON_CIRCLE %>
                        </div>
                    </div>
                </div>
                <%
                        }
                    }
                %>
            </div>
        </section>
    </div>
</div>
</body>
</html>
