<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page isELIgnored="true" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Habit" %>
<%@ page import="model.CalendarItem" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/calendar_style.css">
    <title>Calendar • HabiTap</title>

    <%!
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
    if (habitsList == null) habitsList = java.util.Collections.emptyList();

    List<CalendarItem> itemsList = (List<CalendarItem>) request.getAttribute("calendarItems");
    if (itemsList == null) itemsList = java.util.Collections.emptyList();
%>

<script>
    const ctx = "<%=request.getContextPath()%>";

    const weekdayLabels = ["Sun","Mon","Tue","Wed","Thu","Fri","Sat"];

    const habits = [
        <% for (int i = 0; i < habitsList.size(); i++) {
             Habit h = habitsList.get(i);
        %>
        {
            id: <%=h.getId()%>,
            name: "<%=escJs(h.getName())%>",
            description: "<%=escJs(h.getDescription())%>",
            dueDate: "<%=h.getDueDate()%>",
            completed: <%=h.isCompleted()%>,
            goalId: <%=h.getGoalId() == null ? "null" : h.getGoalId()%>,
            goalName: <%=h.getGoalName() == null ? "null" : "'" + escJs(h.getGoalName()) + "'" %>,
            goalColor: <%=h.getGoalColor() == null ? "null" : "'" + escJs(h.getGoalColor()) + "'" %>
        }<%= (i < habitsList.size() - 1) ? "," : "" %>
        <% } %>
    ];

    let calendarItems = [
        <% for (int i = 0; i < itemsList.size(); i++) {
             CalendarItem it = itemsList.get(i);
        %>
        {
            id: <%=it.getId()%>,
            type: "<%=it.getType()%>",
            title: "<%=escJs(it.getTitle())%>",
            date: "<%=it.getDate()%>",
            startTime: <%=it.getStartTime() == null ? "null" : "'" + it.getStartTime().toString() + "'" %>,
            endTime: <%=it.getEndTime() == null ? "null" : "'" + it.getEndTime().toString() + "'" %>,
            category: <%=it.getCategory() == null ? "null" : "'" + escJs(it.getCategory()) + "'" %>,
            description: <%=it.getDescription() == null ? "null" : "'" + escJs(it.getDescription()) + "'" %>,
            priority: <%=it.getPriority() == null ? "null" : "'" + it.getPriority().name() + "'" %>
        }<%= (i < itemsList.size() - 1) ? "," : "" %>
        <% } %>
    ];

    function ymd(d) {
        const y = d.getFullYear();
        const m = String(d.getMonth()+1).padStart(2,'0');
        const day = String(d.getDate()).padStart(2,'0');
        return `${y}-${m}-${day}`;
    }

    function sameDay(a, b) { return a === b; }

    function monthName(d) {
        return d.toLocaleDateString(undefined, { month: "long", year: "numeric" });
    }

    function addMonths(d, delta) {
        return new Date(d.getFullYear(), d.getMonth() + delta, 1);
    }

    function startOfGrid(monthDate) {
        const first = new Date(monthDate.getFullYear(), monthDate.getMonth(), 1);
        const day = first.getDay(); // 0..6 (Sun..Sat)
        const gridStart = new Date(first);
        gridStart.setDate(first.getDate() - day);
        return gridStart;
    }

    function isTodayCell(dateStr) {
        return dateStr === ymd(new Date());
    }

    function habitsForDate(dateStr) {
        return habits.filter(h => h.dueDate === dateStr);
    }

    function itemsForDate(dateStr) {
        return calendarItems.filter(i => i.date === dateStr);
    }

    function badgeColorForHabit(h) {
        return h.goalColor || "rgba(148,163,184,.55)";
    }

    // ====== Modal state ======
    let viewMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1);
    let modal = { open: false, mode: "create", activeDate: null, type: "EVENT", editItemId: null };

    function openCreateModal(dateStr) {
        modal = { open: true, mode: "create", activeDate: dateStr, type: "EVENT", editItemId: null };
        render();
        hydrateModalDefaults();
    }

    function openEditModal(itemId) {
        const item = calendarItems.find(x => x.id === itemId);
        if (!item) return;
        modal = { open: true, mode: "edit", activeDate: item.date, type: item.type, editItemId: itemId };
        render();
        hydrateModalForEdit(item);
    }

    function closeModal() {
        modal.open = false;
        render();
    }

    function setModalType(t) {
        modal.type = t;
        render();
        if (modal.mode === "create") hydrateModalDefaults();
        else {
            const item = calendarItems.find(x => x.id === modal.editItemId);
            if (item) hydrateModalForEdit(item);
        }
    }

    // ====== Network ======
    async function postForm(url, data) {
        const fd = new URLSearchParams();
        Object.keys(data).forEach(k => {
            if (data[k] === undefined || data[k] === null) return;
            fd.append(k, data[k]);
        });
        const res = await fetch(url, {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: fd.toString()
        });
        const json = await res.json().catch(() => null);
        if (!res.ok) throw new Error((json && json.error) ? json.error : "Request failed");
        if (json && json.ok === false) throw new Error(json.error || "Request failed");
        return json;
    }

    async function createItemFromModal() {
        const data = readModalValues();
        const payload = { type: modal.type, ...data };
        const out = await postForm(ctx + "/calendar/items/create", payload);
        calendarItems.push(out.item);
        closeModal();
    }

    async function updateItemFromModal() {
        const data = readModalValues();
        const payload = { id: modal.editItemId, ...data };
        const out = await postForm(ctx + "/calendar/items/update", payload);
        calendarItems = calendarItems.map(i => i.id === out.item.id ? out.item : i);
        closeModal();
    }

    async function deleteItem(itemId) {
        if (!confirm("Delete this item?")) return;
        await postForm(ctx + "/calendar/items/delete", { id: itemId });
        calendarItems = calendarItems.filter(i => i.id !== itemId);
        closeModal();
    }

    // ====== Modal helpers ======
    function readModalValues() {
        const title = document.querySelector('[data-field="title"]').value.trim();
        const date = document.querySelector('[data-field="date"]').value;
        const description = document.querySelector('[data-field="description"]').value.trim();

        if (modal.type === "EVENT") {
            const startTime = document.querySelector('[data-field="startTime"]').value;
            const endTime = document.querySelector('[data-field="endTime"]').value;
            const category = document.querySelector('[data-field="category"]').value.trim();
            return {
                title,
                date,
                startTime: startTime || null,
                endTime: endTime || null,
                category: category || null,
                description: description || null
            };
        }

        const priority = document.querySelector('[data-field="priority"]').value;
        return {
            title,
            date,
            priority,
            description: description || null
        };
    }

    function hydrateModalDefaults() {
        const dateInput = document.querySelector('[data-field="date"]');
        if (dateInput && modal.activeDate) dateInput.value = modal.activeDate;
    }

    function hydrateModalForEdit(item) {
        const set = (sel, v) => {
            const el = document.querySelector(sel);
            if (!el) return;
            el.value = v == null ? "" : v;
        };
        set('[data-field="title"]', item.title);
        set('[data-field="date"]', item.date);
        set('[data-field="description"]', item.description);
        if (item.type === "EVENT") {
            set('[data-field="startTime"]', item.startTime);
            set('[data-field="endTime"]', item.endTime);
            set('[data-field="category"]', item.category);
        } else {
            set('[data-field="priority"]', item.priority || "MEDIUM");
        }
    }

    // ====== Rendering ======
    function renderTopbar() {
        return `
          <div class="topbar">
            <div>
              <div class="title">Calendar</div>
              <div class="subtitle">Upcoming habits + your events/tasks</div>
            </div>
            <div class="top-actions">
              <a class="btn btn-secondary" href="${ctx}/habits">Habits</a>
              <a class="btn btn-secondary" href="${ctx}/goals">Goals</a>
              <a class="btn btn-ghost" href="${ctx}/logout">Logout</a>
            </div>
          </div>
        `;
    }

    function renderCalendarHeader() {
        return `
          <div class="cal-head">
            <button class="icon-btn" onclick="viewMonth = addMonths(viewMonth,-1); render()" title="Previous month">‹</button>
            <div class="month-label">${monthName(viewMonth)}</div>
            <button class="icon-btn" onclick="viewMonth = addMonths(viewMonth,1); render()" title="Next month">›</button>
            <div class="spacer"></div>
            <button class="btn btn-secondary" onclick="viewMonth = new Date(new Date().getFullYear(), new Date().getMonth(), 1); render()">Today</button>
          </div>
          <div class="weekdays">
            ${weekdayLabels.map(d => `<div class="weekday">${d}</div>`).join('')}
          </div>
        `;
    }

    function renderCell(dateObj, inMonth) {
        const dateStr = ymd(dateObj);
        const day = dateObj.getDate();

        const dayHabits = habitsForDate(dateStr);
        const dayItems = itemsForDate(dateStr);

        const maxShown = 3;
        const shownHabits = dayHabits.slice(0, maxShown);
        const hiddenCount = Math.max(0, dayHabits.length - maxShown);

        return `
          <div class="cell ${inMonth ? "" : "muted"}" onclick="openCreateModal('${dateStr}')">
            <div class="cell-top">
              <div class="day-num">
                <span class="day-circle ${isTodayCell(dateStr) ? "today" : ""}">${day}</span>
              </div>
            </div>
            <div class="pill-area" onclick="event.stopPropagation()">
              ${shownHabits.map(h => `
                <div class="badge ${h.completed ? "done" : ""}" style="border-left-color:${badgeColorForHabit(h)}" title="${h.goalName ? (h.goalName + " • ") : ""}${h.name}">
                  <a class="badge-link" href="${ctx}/habits/edit?id=${encodeURIComponent(h.id)}">${h.name}</a>
                </div>
              `).join('')}
              ${hiddenCount > 0 ? `<button class="more" onclick="openCreateModal('${dateStr}')">${hiddenCount}+ more</button>` : ``}

              ${dayItems.map(i => `
                <button class="pill ${i.type === "TASK" ? "task" : "event"}" onclick="openEditModal(${i.id})" title="Click to edit">
                  <span class="pill-dot"></span>
                  ${i.title}
                </button>
              `).join('')}
            </div>
          </div>
        `;
    }

    function renderGrid() {
        const start = startOfGrid(viewMonth);
        const days = [];
        for (let i = 0; i < 42; i++) {
            const d = new Date(start);
            d.setDate(start.getDate() + i);
            const inMonth = d.getMonth() === viewMonth.getMonth();
            days.push(renderCell(d, inMonth));
        }

        return `
          <div class="calendar">
            ${renderCalendarHeader()}
            <div class="grid">
              ${days.join('')}
            </div>
          </div>
        `;
    }

    function renderModal() {
        if (!modal.open) return "";

        const isEdit = modal.mode === "edit";
        const activeType = modal.type;
        const title = isEdit ? "Edit" : "Add";

        const eventFields = `
          <div class="field">
            <label>Title</label>
            <input data-field="title" type="text" maxlength="120" required />
          </div>
          <div class="field">
            <label>Date</label>
            <input data-field="date" type="date" required />
          </div>
          <div class="two">
            <div class="field">
              <label>Start Time</label>
              <input data-field="startTime" type="time" />
            </div>
            <div class="field">
              <label>End Time</label>
              <input data-field="endTime" type="time" />
            </div>
          </div>
          <div class="field">
            <label>Category</label>
            <input data-field="category" type="text" maxlength="50" placeholder="e.g., Study / Work / Health" />
          </div>
          <div class="field">
            <label>Description</label>
            <textarea data-field="description" rows="3" maxlength="500" placeholder="Optional"></textarea>
          </div>
        `;

        const taskFields = `
          <div class="field">
            <label>Title</label>
            <input data-field="title" type="text" maxlength="120" required />
          </div>
          <div class="field">
            <label>Due Date</label>
            <input data-field="date" type="date" required />
          </div>
          <div class="field">
            <label>Priority</label>
            <select data-field="priority" required>
              <option value="LOW">Low</option>
              <option value="MEDIUM" selected>Medium</option>
              <option value="HIGH">High</option>
            </select>
          </div>
          <div class="field">
            <label>Description</label>
            <textarea data-field="description" rows="3" maxlength="500" placeholder="Optional"></textarea>
          </div>
        `;

        return `
          <div class="overlay" onclick="closeModal()">
            <div class="modal" onclick="event.stopPropagation()">
              <div class="modal-head">
                <div>
                  <div class="modal-title">${title} ${activeType === "EVENT" ? "Event" : "Task"}</div>
                  <div class="modal-sub">${isEdit ? "Update details or delete" : "Create a new item on this day"}</div>
                </div>
                <button class="x" onclick="closeModal()" aria-label="Close">×</button>
              </div>

              <div class="tabs">
                <button class="tab ${activeType === "EVENT" ? "active" : ""}" onclick="setModalType('EVENT')">Event</button>
                <button class="tab ${activeType === "TASK" ? "active" : ""}" onclick="setModalType('TASK')">Task</button>
              </div>

              <div class="modal-body">
                ${activeType === "EVENT" ? eventFields : taskFields}
              </div>

              <div class="modal-actions">
                ${isEdit ? `<button class="btn btn-danger" onclick="deleteItem(${modal.editItemId})">Delete</button>` : `<div></div>`}
                <div class="right">
                  <button class="btn btn-secondary" onclick="closeModal()">Cancel</button>
                  <button class="btn btn-primary" onclick="${isEdit ? "updateItemFromModal()" : "createItemFromModal()"}">${isEdit ? "Save" : "Add"}</button>
                </div>
              </div>
            </div>
          </div>
        `;
    }

    function render() {
        document.getElementById("app-root").innerHTML = `
          <div class="wrap">
            ${renderTopbar()}
            ${renderGrid()}
            ${renderModal()}
          </div>
        `;
    }

    document.addEventListener("DOMContentLoaded", render);
</script>
</body>
</html>

