<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/goal_new_style.css">
    <title>Create Goal • HabiTap</title>
</head>
<body>
<div class="wrap">
    <div class="topbar">
        <div>
            <div class="title">Create Goal</div>
            <div class="subtitle">Add a goal, then add habits as subtasks</div>
        </div>
        <div class="actions">
            <a class="btn btn-secondary" href="<%=request.getContextPath()%>/goals">Back</a>
        </div>
    </div>

    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
    <div class="error"><%= error %></div>
    <%
        }
    %>

    <form class="card" method="POST" action="<%=request.getContextPath()%>/goals/new">
        <div class="section">
            <div class="section-title">Goal details</div>
            <div class="row">
                <label>Goal name</label>
                <input type="text" name="goalName" required maxlength="80" placeholder="e.g., Get fitter in 30 days">
            </div>
            <div class="row">
                <label>Color</label>
                <input type="color" name="goalColor" value="#2563eb" required>
            </div>
            <div class="row">
                <label>Due date (optional)</label>
                <input type="date" name="goalDueDate">
            </div>
            <div class="row">
                <label>Priority</label>
                <select name="goalPriority">
                    <option value="LOW">Low</option>
                    <option value="MEDIUM" selected>Medium</option>
                    <option value="HIGH">High</option>
                </select>
            </div>
        </div>

        <div class="section">
            <div class="section-head">
                <div class="section-title">Habit subtasks</div>
                <button type="button" class="btn btn-secondary" onclick="addRow()">Add subtask</button>
            </div>

            <div id="rows" class="rows"></div>
            <div class="hint">Each subtask creates a habit linked to this goal. You can leave extra rows empty.</div>
        </div>

        <div class="footer">
            <button type="submit" class="btn btn-primary">Create goal</button>
        </div>
    </form>
</div>

<template id="rowTpl">
    <div class="subtask">
        <div class="subtask-grid">
            <div class="row">
                <label>Title</label>
                <input type="text" name="habitName" maxlength="50" placeholder="e.g., Morning run">
            </div>
            <div class="row">
                <label>Due date</label>
                <input type="date" name="habitDueDate">
            </div>
            <div class="row">
                <label>Priority</label>
                <select name="habitPriority">
                    <option value="">--</option>
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                </select>
            </div>
        </div>
        <div class="row">
            <label>Description</label>
            <textarea name="habitDescription" rows="2" maxlength="500" placeholder="Optional"></textarea>
        </div>
        <div class="subtask-actions">
            <button type="button" class="btn btn-ghost danger" onclick="removeRow(this)">Remove</button>
        </div>
    </div>
</template>

<script>
    // Set constraints for goal due date input when page loads
    document.addEventListener('DOMContentLoaded', function() {
        const goalDateInput = document.querySelector('input[name="goalDueDate"]');
        if (goalDateInput) {
            const today = new Date();
            const oneYearFromNow = new Date();
            oneYearFromNow.setFullYear(today.getFullYear() + 1);

            const formatDate = (d) => {
                const year = d.getFullYear();
                const month = String(d.getMonth() + 1).padStart(2, '0');
                const day = String(d.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            };

            goalDateInput.min = formatDate(today);
            goalDateInput.max = formatDate(oneYearFromNow);
        }
    });

    function addRow() {
        const tpl = document.getElementById('rowTpl');
        const node = tpl.content.cloneNode(true);

        // Set date constraints for the new date input
        const dateInput = node.querySelector('input[type="date"]');
        if (dateInput) {
            const today = new Date();

            const tomorrow = new Date();
            tomorrow.setDate(today.getDate() + 1);

            const oneYearFromNow = new Date();
            oneYearFromNow.setFullYear(today.getFullYear() + 1);

            const formatDate = (d) => {
                const year = d.getFullYear();
                const month = String(d.getMonth() + 1).padStart(2, '0');
                const day = String(d.getDate()).padStart(2, '0');
                return `${year}-${month}-${day}`;
            };

            dateInput.min = formatDate(today);
            dateInput.max = formatDate(oneYearFromNow);
            dateInput.value = formatDate(tomorrow);
        }

        document.getElementById('rows').appendChild(node);
    }

    function removeRow(btn) {
        const row = btn.closest('.subtask');
        if (row) row.remove();
    }

    // start with 3 rows
    addRow(); addRow(); addRow();
</script>
</body>
</html>

