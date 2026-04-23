<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Habit" %>
<%@ page import="model.Goal" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/habits_style.css">

    <title>Habits for Goal • HabiTap</title>



    <%!
        // Escape Java strings safely into JS string literals
        private String escJs(String s) {
            if (s == null) return "";
            return s.replace("\\", "\\\\")
                    .replace("'", "\\'")
                    .replace("\"", "\\\"")
                    .replace("\r", "")
                    .replace("\n", "\\n");
        }
    %>
</head>

<body>
<div id="app-root"></div>

<%
    List<Habit> habitsList = (List<Habit>) request.getAttribute("habits");
    Goal goal = (Goal) request.getAttribute("goal");
    if (habitsList == null) habitsList = java.util.Collections.emptyList();
%>


<script>
    // Icons SVG (only the ones used on dashboard)
    const icons = {
        plus: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>',
        star: '<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polygon points="12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2"/></svg>',
        circle: '<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#d1d5db" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/></svg>',
        circleCheck: '<svg xmlns="http://www.w3.org/2000/svg" width="28" height="28" viewBox="0 0 24 24" fill="none" stroke="#10b981" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><path d="m9 12 2 2 4-4"/></svg>',
        pencil: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4L16.5 3.5z"/></svg>',
        trash: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><polyline points="3 6 5 6 21 6"/><path d="M19 6l-1 14a2 2 0 0 1-2 2H8a2 2 0 0 1-2-2L5 6"/><path d="M10 11v6M14 11v6M9 6V4a1 1 0 0 1 1-1h4a1 1 0 0 1 1 1v2"/></svg>',
        arrowLeft: '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M19 12H5"/><path d="M12 19l-7-7 7-7"/></svg>'
    };

    // ====== HABITS DATA COMES FROM SERVER (JSP -> JS) ======
    let habits = [
        <% for (int i = 0; i < habitsList.size(); i++) {
             Habit h = habitsList.get(i);
             String icon = "📝"; // default
             // Simple icon mapping by priority
             if (h.getPriority() != null) {
               String p = h.getPriority().name();
               if ("HIGH".equals(p)) icon = "🔥";
               else if ("MEDIUM".equals(p)) icon = "⚡";
               else icon = "🌱";
             }
        %>
        {
            id: "<%=h.getId()%>",
            name: "<%=escJs(h.getName())%>",
            icon: "<%=icon%>",
            completed: <%=h.isCompleted()%>,
            dueDate: "<%=h.getDueDate()%>",
            priority: "<%=h.getPriority()%>"
        }<%= (i < habitsList.size() - 1) ? "," : "" %>
        <% } %>
    ];

    function getCompletedCount() {
        return habits.filter(h => h.completed).length;
    }
    function getTotalHabits() {
        return habits.length;
    }
    function getCompletionRate() {
        const total = getTotalHabits();
        if (total === 0) return 0;
        return Math.round((getCompletedCount() / total) * 100);
    }

    // ====== IMPORTANT CHANGE ======
    // Toggle now calls your SERVLET endpoints, then reloads the page.
    function toggleHabit(id) {
        const habit = habits.find(h => h.id === id);
        if (!habit) return;

        const endpoint = habit.completed ? "habits/incomplete" : "habits/complete";
        const url = "<%=request.getContextPath()%>/" + endpoint;

        // POST form (works on old browsers; no fetch needed)
        const form = document.createElement("form");
        form.method = "POST";
        form.action = url;

        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "id";
        input.value = id;

        form.appendChild(input);
        document.body.appendChild(form);
        form.submit();
    }

    function goCreateHabit() {
        window.location.href = "<%=request.getContextPath()%>/habits/new";
    }

    function goGoals() {
        window.location.href = "<%=request.getContextPath()%>/goals";
    }

    function goCalendar() {
        window.location.href = "<%=request.getContextPath()%>/calendar";
    }

    function logout() {
        window.location.href = "<%=request.getContextPath()%>/logout";
    }

    // NEW: navigate to edit page
    function goEdit(id) {
        window.location.href = "<%=request.getContextPath()%>/habits/edit?id=" + encodeURIComponent(id);
    }

    // NEW: delete habit via POST (with confirmation)
    function deleteHabit(id) {
        if (!confirm("Delete this habit? This action cannot be undone.")) return;

        const url = "<%=request.getContextPath()%>/habits/delete";
        const form = document.createElement("form");
        form.method = "POST";
        form.action = url;

        const input = document.createElement("input");
        input.type = "hidden";
        input.name = "id";
        input.value = id;
        form.appendChild(input);

        document.body.appendChild(form);
        form.submit();
    }

    function renderDashboard() {
        const completedCount = getCompletedCount();
        const totalHabits = getTotalHabits();
        const completionRate = getCompletionRate();

        // Use real date
        const now = new Date();
        const dateStr = now.toLocaleDateString(undefined, { weekday: "long", year: "numeric", month: "short", day: "numeric" });

        return `
      <div class="habit-tracker">
        <div class="header">
          <div class="header-top">
            <div>
              <p class="header-date">${dateStr}</p>
              <h1 class="header-title">Habits for Goal</h1>
              <p class="header-subtitle">${completedCount} of ${totalHabits} completed</p>
            </div>
            <div class="header-actions">
              <button class="nav-button" onclick="goBackToGoals()">Back to Goals</button>
              <button class="nav-button" onclick="goCalendar()">Calendar</button>
              <button class="nav-button ghost" onclick="logout()">Logout</button>
              <div class="header-icon">${icons.star}</div>
            </div>
          </div>

          <div class="goal-info">
            <div class="goal-color" style="background: <%= goal != null ? goal.getColor() : "#2563eb" %>"></div>
            <div class="goal-details">
              <h2 class="goal-name"><%= goal != null ? goal.getName() : "Unknown Goal" %></h2>
              <p class="goal-meta">
                Priority: <%= goal != null ? goal.getPriority() : "N/A" %>
                ${goal && goal.dueDate ? ` • Due: ${goal.dueDate}` : ''}
              </p>
            </div>
          </div>

          <div class="stats-container">
            <div class="stat-card">
              <div class="stat-header">
                <span class="stat-label">Completion</span>
              </div>
              <p class="stat-value">${completionRate}%</p>
            </div>

            <div class="stat-card">
              <div class="stat-header">
                <span class="stat-label">Total Habits</span>
              </div>
              <p class="stat-value">${totalHabits}</p>
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
              <button class="add-button" onclick="goCreateHabit()">
                ${icons.plus}
              </button>
            </div>

            <div class="habits-list">
              ${
                habits.length === 0
                ? `<div style="color:#6b7280;">No habits found for this goal. Create one using the + button.</div>`
                : habits.map((habit, index) => `
                  <div class="habit-card" onclick="toggleHabit('${habit.id}')">
                    <div class="habit-icon ${habit.completed ? 'completed' : ''}">
                      <span>${habit.icon}</span>
                    </div>

                    <div class="habit-info">
                      <h3 class="habit-name ${habit.completed ? 'completed' : ''}">${habit.name}</h3>
                      <p class="habit-streak">Due: ${habit.dueDate} • Priority: ${habit.priority}</p>
                    </div>

                    <div class="habit-actions">
                      <button class="habit-action-button" title="Edit" onclick="event.stopPropagation(); goEdit('${habit.id}')">
                        ${icons.pencil}
                      </button>
                      <button class="habit-action-button" title="Delete" onclick="event.stopPropagation(); deleteHabit('${habit.id}')">
                        ${icons.trash}
                      </button>
                      <div class="habit-check">
                        ${habit.completed ? icons.circleCheck : icons.circle}
                      </div>
                    </div>
                  </div>
                `).join('')
              }
            </div>
          </section>
        </div>
      </div>
    `;
    }

    function goBackToGoals() {
        window.location.href = "<%=request.getContextPath()%>/goals";
    }

    function render() {
        document.getElementById("app-root").innerHTML = renderDashboard();
    }

    document.addEventListener("DOMContentLoaded", render);
</script>
</body>
</html>
