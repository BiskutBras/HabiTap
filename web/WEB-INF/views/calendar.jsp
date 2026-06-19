<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="java.util.List" %>

<%@ page import="java.util.Calendar" %>

<%@ page import="java.text.SimpleDateFormat" %>

<%@ page import="model.Habit" %>

<%@ page import="model.CalendarItem" %>


<%!

    public String ymd(Calendar c) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return df.format(c.getTime());

    }


    public String monthYearName(Calendar c) {

        SimpleDateFormat df = new SimpleDateFormat("MMMM yyyy");

        return df.format(c.getTime());

    }


    public List<CalendarItem> itemsForDate(String dateStr, List<CalendarItem> itemsList) {

        List<CalendarItem> result = new java.util.ArrayList<>();

        for (CalendarItem item : itemsList) {

            if (item.getDate().toString().equals(dateStr)) {

                result.add(item);

            }

        }

        return result;

    }


    public String getGoalName(int goalId, List<Habit> habitList) {

        for (Habit h : habitList) {

            if (h.getGoalId() == goalId) {

                return h.getGoalName() != null ? h.getGoalName() : "Goal " + goalId;

            }

        }

        return "Goal " + goalId;

    }


    public boolean isTodayCell(String dateStr) {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        return dateStr.equals(df.format(Calendar.getInstance().getTime()));

    }


    public Calendar getStartOfGrid(Calendar monthCal) {

        Calendar first = Calendar.getInstance();

        first.set(monthCal.get(Calendar.YEAR), monthCal.get(Calendar.MONTH), 1);


        Calendar gridStart = Calendar.getInstance();

        gridStart.setTime(first.getTime());

        gridStart.add(Calendar.DAY_OF_MONTH, -(gridStart.get(Calendar.DAY_OF_WEEK) - 1));

        return gridStart;

    }

%>


<!DOCTYPE html>

<html lang="en">

<head>

    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>

    <title>Calendar • HabiTap</title>

</head>

<body class="bg-light">


<%

    String viewMode = request.getParameter("view") != null ? request.getParameter("view") : "month";

    String monthParam = request.getParameter("month");

    String goalParam = request.getParameter("goal");

    String selectedDateStr = request.getParameter("date");

    String selectedTimeStr = request.getParameter("time");

    String actionParam = request.getParameter("action");


    List<Habit> habitList = (List<Habit>) request.getAttribute("habitList");

    if (habitList == null) habitList = java.util.Collections.emptyList();


    List<CalendarItem> itemsList = (List<CalendarItem>) request.getAttribute("calendarItems");

    if (itemsList == null) itemsList = java.util.Collections.emptyList();


    Calendar viewCalendar = Calendar.getInstance();

    if (monthParam != null && !monthParam.isEmpty()) {

        try {

            String[] parts = monthParam.split("-");

            viewCalendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, 1);

        } catch (Exception e) {
        }

    } else {

        viewCalendar.set(Calendar.DAY_OF_MONTH, 1);

    }


    Integer selectedGoalId = null;

    if (goalParam != null && !goalParam.isEmpty()) {

        try {

            selectedGoalId = Integer.parseInt(goalParam);

        } catch (Exception e) {
        }

    }


    List<Habit> filteredHabits = new java.util.ArrayList<>();

    java.util.Set<Integer> availableGoalIds = new java.util.LinkedHashSet<>();

    for (Habit h : habitList) {

        availableGoalIds.add(h.getGoalId());

        if (selectedGoalId == null || h.getGoalId() == selectedGoalId) {

            filteredHabits.add(h);

        }

    }

    String monthYearStr = (viewCalendar.get(Calendar.YEAR)) + "-" +

            String.format("%02d", (viewCalendar.get(Calendar.MONTH) + 1));

    String goalQuery = selectedGoalId != null ? "&goal=" + selectedGoalId : "";

%>


<jsp:include page="/WEB-INF/views/includes/navbar.jsp">

    <jsp:param name="active" value="calendar"/>

</jsp:include>

<div class="container-fluid py-4">

    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">

        <div>

            <h1 class="h3 fw-bold mb-1">Calendar</h1>

            <p class="text-muted mb-0">Events, tasks, and habits</p>

        </div>


        <div class="d-flex flex-wrap align-items-center gap-2">

            <form method="get" action="<%= request.getContextPath() %>/calendar" class="m-0">

                <input type="hidden" name="view" value="<%= viewMode %>"/>

                <input type="hidden" name="month" value="<%= monthYearStr %>"/>

                <select name="goal" class="form-select form-select-sm" onchange="this.form.submit();"
                        style="min-width:10rem;">

                    <option value="">All Goals</option>

                    <% for (Integer gid : availableGoalIds) {

                        String selected = (selectedGoalId != null && selectedGoalId == gid) ? " selected" : "";

                    %>

                    <option value="<%= gid %>"<%= selected %>><%= getGoalName(gid, habitList) %>
                    </option>

                    <% } %>

                </select>

            </form>


            <div class="btn-group btn-group-sm" role="group" aria-label="View mode">

                <% String[] views = {"day", "week", "month", "schedule"};

                    for (String v : views) {

                        String activeClass = viewMode.equals(v) ? "btn-primary" : "btn-outline-secondary";

                %>

                <a href="<%= request.getContextPath() %>/calendar?view=<%= v %>&month=<%= monthYearStr %><%= goalQuery %>"

                   class="btn <%= activeClass %>">

                    <%= v.substring(0, 1).toUpperCase() + v.substring(1) %>

                </a>

                <% } %>

            </div>

        </div>

    </div>


    <% if (viewMode.equals("month")) { %>

    <div class="card shadow-sm">

        <div class="card-body">

            <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">

                <%

                    Calendar prevMonth = Calendar.getInstance();

                    prevMonth.setTime(viewCalendar.getTime());

                    prevMonth.add(Calendar.MONTH, -1);

                    String prevMonthStr = prevMonth.get(Calendar.YEAR) + "-" +

                            String.format("%02d", (prevMonth.get(Calendar.MONTH) + 1));


                    Calendar nextMonth = Calendar.getInstance();

                    nextMonth.setTime(viewCalendar.getTime());

                    nextMonth.add(Calendar.MONTH, 1);

                    String nextMonthStr = nextMonth.get(Calendar.YEAR) + "-" +

                            String.format("%02d", (nextMonth.get(Calendar.MONTH) + 1));

                %>

                <a href="<%= request.getContextPath() %>/calendar?view=month&month=<%= prevMonthStr %><%= goalQuery %>"

                   class="btn btn-outline-secondary btn-sm">&lsaquo;</a>


                <h2 class="h5 fw-semibold mb-0"><%= monthYearName(viewCalendar) %>
                </h2>


                <div class="d-flex gap-2">

                    <a href="<%= request.getContextPath() %>/calendar?view=month&month=<%= nextMonthStr %><%= goalQuery %>"

                       class="btn btn-outline-secondary btn-sm">&rsaquo;</a>

                    <a href="<%= request.getContextPath() %>/calendar?view=month<%= goalQuery %>"

                       class="btn btn-primary btn-sm">Today</a>

                </div>

            </div>


            <div class="cal-grid mb-1">

                <% String[] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

                    for (String wd : weekdays) { %>

                <div class="cal-weekday"><%= wd %>
                </div>

                <% } %>

            </div>


            <div class="cal-grid">

                <%

                    Calendar gridStart = getStartOfGrid(viewCalendar);

                    for (int i = 0; i < 42; i++) {

                        Calendar cellDate = Calendar.getInstance();

                        cellDate.setTime(gridStart.getTime());

                        cellDate.add(Calendar.DAY_OF_MONTH, i);


                        boolean inMonth = cellDate.get(Calendar.MONTH) == viewCalendar.get(Calendar.MONTH);

                        String dateStr = ymd(cellDate);

                        int dayNum = cellDate.get(Calendar.DAY_OF_MONTH);

                        List<CalendarItem> dayItems = itemsForDate(dateStr, itemsList);

                        boolean isToday = isTodayCell(dateStr);

                %>

                <div class="cal-cell<%= inMonth ? "" : " out-month" %>">

                    <div class="fw-semibold mb-1">

                        <% if (isToday) { %>

                        <span class="cal-today-badge"><%= dayNum %></span>

                        <% } else { %>

                        <%= dayNum %>

                        <% } %>

                    </div>

                    <%

                        int shown = 0;

                        for (CalendarItem item : dayItems) {

                            if (shown >= 2) break;

                            String eventClass = item.getType().name().equals("TASK") ? "cal-event-task" : "cal-event-event";

                    %>

                    <div class="cal-event <%= eventClass %>">

                        <a href="<%= request.getContextPath() %>/calendar?view=<%= viewMode %>&month=<%= monthYearStr %>&action=edit&id=<%= item.getId() %><%= goalQuery %>"

                           class="text-decoration-none text-dark fw-medium">

                            <%= item.getTitle().length() > 12 ? item.getTitle().substring(0, 12) + "..." : item.getTitle() %>

                        </a>

                    </div>

                    <%

                            shown++;

                        }

                        if (dayItems.size() > 2) {

                    %>

                    <div class="small text-muted"><%= (dayItems.size() - 2) %>+ more</div>

                    <% } %>

                    <a href="<%= request.getContextPath() %>/calendar?view=<%= viewMode %>&month=<%= monthYearStr %>&action=create&date=<%= dateStr %><%= goalQuery %>"

                       class="small text-primary text-decoration-none">+ Add</a>

                </div>

                <% } %>

            </div>

        </div>

    </div>


    <% } else if (viewMode.equals("week")) { %>

    <div class="card shadow-sm">

        <div class="card-body">

            <%

                Calendar today = Calendar.getInstance();

                Calendar sunday = Calendar.getInstance();

                sunday.setTime(today.getTime());

                sunday.add(Calendar.DAY_OF_WEEK, -(sunday.get(Calendar.DAY_OF_WEEK) - 1));

            %>

            <h2 class="h5 fw-semibold mb-3">Week of <%= new SimpleDateFormat("MMM d, yyyy").format(sunday.getTime()) %>
            </h2>


            <div class="week-grid">

                <%

                    for (int i = 0; i < 7; i++) {

                        Calendar dayForWeek = Calendar.getInstance();

                        dayForWeek.setTime(sunday.getTime());

                        dayForWeek.add(Calendar.DAY_OF_MONTH, i);

                        String weekDateStr = ymd(dayForWeek);

                        List<CalendarItem> weekDayItems = itemsForDate(weekDateStr, itemsList);

                %>

                <div class="card h-100">

                    <div class="card-body p-2">

                        <strong class="small d-block mb-2">

                            <%= new SimpleDateFormat("EEE, M/d").format(dayForWeek.getTime()) %>

                        </strong>


                        <% for (CalendarItem item : weekDayItems) { %>

                        <div class="cal-event cal-event-event mb-1">

                            <a href="<%= request.getContextPath() %>/calendar?view=week&action=edit&id=<%= item.getId() %><%= goalQuery %>"

                               class="text-decoration-none text-primary fw-medium small">

                                <%= item.getTitle() %>

                            </a>

                        </div>

                        <% } %>


                        <a href="<%= request.getContextPath() %>/calendar?view=week&action=create&date=<%= weekDateStr %><%= goalQuery %>"

                           class="btn btn-outline-secondary btn-sm w-100 mt-2">+ Add</a>

                    </div>

                </div>

                <% } %>

            </div>

        </div>

    </div>


    <% } else if (viewMode.equals("day")) { %>

    <div class="card shadow-sm">

        <div class="card-body">

            <%

                Calendar dayView = Calendar.getInstance();

                String dayDateStr = ymd(dayView);

                List<CalendarItem> dayViewItems = itemsForDate(dayDateStr, itemsList);

            %>

            <h2 class="h5 fw-semibold mb-3">

                <%= new SimpleDateFormat("EEEE, MMMM d, yyyy").format(dayView.getTime()) %>

            </h2>


            <h3 class="h6 text-secondary mb-2">Events &amp; Tasks</h3>

            <% if (dayViewItems.isEmpty()) { %>

            <p class="text-muted">No events or tasks for today</p>

            <% } else {

                for (CalendarItem item : dayViewItems) {

            %>

            <div class="card bg-light mb-2">

                <div class="card-body py-2 d-flex justify-content-between align-items-center">

                    <div>

                        <strong><%= item.getTitle() %>
                        </strong>

                        <span class="text-muted"> - <%= item.getType().name() %></span>

                        <% if (item.getStartTime() != null) { %>

                        <span class="text-muted"> @ <%= item.getStartTime() %></span>

                        <% } %>

                    </div>

                    <a href="<%= request.getContextPath() %>/calendar?view=day&action=edit&id=<%= item.getId() %><%= goalQuery %>"

                       class="btn btn-sm btn-outline-primary">Edit</a>

                </div>

            </div>

            <% }
            } %>


            <h3 class="h6 text-secondary mt-4 mb-2">Time Slots</h3>

            <div class="list-group" style="max-height:60vh; overflow-y:auto;">

                <% for (int hour = 0; hour < 24; hour++) {

                    String hourStr = String.format("%02d", hour);

                %>

                <div class="list-group-item d-flex justify-content-between align-items-center">

                    <strong><%= hourStr %>:00</strong>

                    <a href="<%= request.getContextPath() %>/calendar?view=day&action=create&date=<%= dayDateStr %>&time=<%= hourStr %>:00<%= goalQuery %>"

                       class="btn btn-sm btn-link">+ Add Event</a>

                </div>

                <% } %>

            </div>

        </div>

    </div>


    <% } else if (viewMode.equals("schedule")) { %>

    <div class="card shadow-sm">

        <div class="card-body">

            <h2 class="h5 fw-semibold mb-3">Schedule</h2>


            <h3 class="h6 text-secondary mb-2">Your Habits (<%= filteredHabits.size() %>)</h3>

            <% if (filteredHabits.isEmpty()) { %>

            <p class="text-muted">No habits</p>

            <% } else {

                for (Habit h : filteredHabits) {

            %>

            <div class="card border-start border-success border-4 mb-2">

                <div class="card-body py-2">

                    <div class="fw-semibold"><%= h.getName() %>
                    </div>

                    <div class="small text-muted">

                        Frequency: <%= h.getFrequency() %> |

                        Goal: <%= h.getGoalName() != null ? h.getGoalName() : "None" %> |

                        <%= h.isCompleted() ? "&#10003; Done" : "Pending" %>

                    </div>

                    <div class="small text-secondary mt-1"><%= h.getDescription() %>
                    </div>

                    <a href="<%= request.getContextPath() %>/habits/edit?id=<%= h.getId() %>"

                       class="small text-primary text-decoration-none">Edit &rarr;</a>

                </div>

            </div>

            <% }
            } %>


            <h3 class="h6 text-secondary mt-4 mb-2">Upcoming Events/Tasks (Next 2 Weeks)</h3>

            <%

                Calendar scheduleToday = Calendar.getInstance();

                for (int dayOffset = 0; dayOffset < 14; dayOffset++) {

                    Calendar scheduleDate = Calendar.getInstance();

                    scheduleDate.setTime(scheduleToday.getTime());

                    scheduleDate.add(Calendar.DAY_OF_MONTH, dayOffset);

                    String scheduleDateStr = ymd(scheduleDate);

                    List<CalendarItem> scheduleDayItems = itemsForDate(scheduleDateStr, itemsList);


                    if (!scheduleDayItems.isEmpty()) {

            %>

            <div class="mb-3">

                <div class="fw-semibold mb-1">

                    <%= new SimpleDateFormat("EEE, MMM d").format(scheduleDate.getTime()) %>

                </div>

                <% for (CalendarItem item : scheduleDayItems) { %>

                <div class="card border-start border-primary border-3 mb-1">

                    <div class="card-body py-2">

                        <a href="<%= request.getContextPath() %>/calendar?view=schedule&action=edit&id=<%= item.getId() %><%= goalQuery %>"

                           class="text-decoration-none text-primary fw-medium small">

                            <strong><%= item.getTitle() %>
                            </strong>

                        </a>

                        <% if (item.getStartTime() != null) { %>

                        <span class="small text-muted"> @ <%= item.getStartTime() %></span>

                        <% } %>

                        <span class="small text-muted"> - <%= item.getType() %></span>

                    </div>

                </div>

                <% } %>

            </div>

            <% }
            } %>

        </div>

    </div>

    <% } %>


    <% if ("create".equals(actionParam) || "edit".equals(actionParam)) {

        CalendarItem editItem = null;

        if ("edit".equals(actionParam) && request.getParameter("id") != null) {

            try {

                int itemId = Integer.parseInt(request.getParameter("id"));

                for (CalendarItem item : itemsList) {

                    if (item.getId() == itemId) {

                        editItem = item;

                        break;

                    }

                }

            } catch (Exception e) {
            }

        }


        String formDate = selectedDateStr != null ? selectedDateStr : ymd(Calendar.getInstance());

        String formTime = selectedTimeStr != null ? selectedTimeStr : "";

        String formType = editItem != null ? editItem.getType().name() : "EVENT";

    %>

    <div class="card shadow-sm mt-4">

        <div class="card-body">

            <h3 class="h5 fw-semibold mb-3">

                <%= "edit".equals(actionParam) ? "Edit Item" : "Create New Item" %>

            </h3>


            <form method="post"
                  action="<%= request.getContextPath() %>/calendar/items/<%= "edit".equals(actionParam) ? "update" : "create" %>"
                  class="row g-3" style="max-width:540px;">

                <% if ("edit".equals(actionParam)) { %>

                <input type="hidden" name="id" value="<%= editItem.getId() %>"/>

                <% } %>

                <input type="hidden" name="returnView" value="<%= viewMode %>"/>

                <% if (selectedGoalId != null) { %>

                <input type="hidden" name="goal" value="<%= selectedGoalId %>"/>

                <% } %>


                <div class="col-12">

                    <label class="form-label fw-semibold">Item Type</label>

                    <select name="type" class="form-select">

                        <option value="EVENT" <%= "EVENT".equals(formType) ? "selected" : "" %>>Event</option>

                        <option value="TASK" <%= "TASK".equals(formType) ? "selected" : "" %>>Task</option>

                    </select>

                </div>


                <div class="col-12">

                    <label class="form-label fw-semibold">Title</label>

                    <input type="text" name="title" class="form-control"

                           value="<%= editItem != null ? editItem.getTitle() : "" %>"

                           maxlength="120" required/>

                </div>


                <div class="col-12">

                    <label class="form-label fw-semibold">Date</label>

                    <input type="date" name="date" class="form-control"

                           value="<%= editItem != null ? editItem.getDate().toString() : formDate %>" required/>

                </div>


                <div class="col-md-6">

                    <label class="form-label fw-semibold">Start Time</label>

                    <input type="time" name="startTime" class="form-control"

                           value="<%= editItem != null && editItem.getStartTime() != null ? editItem.getStartTime().toString() : formTime %>"/>

                </div>

                <div class="col-md-6">

                    <label class="form-label fw-semibold">End Time</label>

                    <input type="time" name="endTime" class="form-control"

                           value="<%= editItem != null && editItem.getEndTime() != null ? editItem.getEndTime().toString() : "" %>"/>

                </div>


                <div class="col-12">

                    <label class="form-label fw-semibold">Category</label>

                    <input type="text" name="category" class="form-control"

                           value="<%= editItem != null && editItem.getCategory() != null ? editItem.getCategory() : "" %>"

                           maxlength="50" placeholder="e.g., Study / Work / Health"/>

                </div>


                <div class="col-12">

                    <label class="form-label fw-semibold">Priority (for tasks)</label>

                    <select name="priority" class="form-select">

                        <option value="LOW" <%= editItem != null && editItem.getPriority() != null && editItem.getPriority().name().equals("LOW") ? "selected" : "" %>>
                            Low
                        </option>

                        <option value="MEDIUM" <%= editItem == null || editItem.getPriority() == null || editItem.getPriority().name().equals("MEDIUM") ? "selected" : "" %>>
                            Medium
                        </option>

                        <option value="HIGH" <%= editItem != null && editItem.getPriority() != null && editItem.getPriority().name().equals("HIGH") ? "selected" : "" %>>
                            High
                        </option>

                    </select>

                </div>


                <div class="col-12">

                    <label class="form-label fw-semibold">Description</label>

                    <textarea name="description" class="form-control" rows="3" maxlength="500"
                              placeholder="Optional"><%= editItem != null && editItem.getDescription() != null ? editItem.getDescription() : "" %></textarea>

                </div>


                <div class="col-12 d-flex flex-wrap gap-2">

                    <button type="submit" class="btn btn-primary">

                        <%= "edit".equals(actionParam) ? "Update" : "Create" %>

                    </button>

                    <a href="<%= request.getContextPath() %>/calendar?view=<%= viewMode %><%= goalQuery %>"

                       class="btn btn-outline-secondary">Cancel</a>

                </div>

            </form>

            <% if ("edit".equals(actionParam)) { %>

            <form method="post" action="<%= request.getContextPath() %>/calendar/items/delete" class="mt-2">

                <input type="hidden" name="id" value="<%= editItem.getId() %>"/>

                <button type="submit" class="btn btn-danger"

                        onclick="return confirm('Delete this item?');">Delete
                </button>

            </form>

            <% } %>

        </div>

    </div>

    <% } %>

</div>


</body>

</html>

