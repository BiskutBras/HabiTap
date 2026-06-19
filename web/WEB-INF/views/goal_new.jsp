<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Create Goal • HabiTap</title>
</head>
<body class="bg-light">
<jsp:include page="/WEB-INF/views/includes/navbar.jsp">
    <jsp:param name="active" value="goals"/>
</jsp:include>

<div class="container py-4">
    <div class="d-flex flex-wrap justify-content-between align-items-end gap-3 mb-4">
        <div>
            <h1 class="h3 fw-bold mb-1">Create Goal</h1>
            <p class="text-muted mb-0">Add a goal, then add habits as subtasks</p>
        </div>
        <a class="btn btn-outline-secondary" href="<%=request.getContextPath()%>/goals">Back</a>
    </div>

    <%
        String error = (String) request.getAttribute("error");
        if (error != null) {
    %>
    <div class="alert alert-danger" role="alert"><%= error %></div>
    <%
        }
    %>

    <form method="POST" action="<%=request.getContextPath()%>/goals/new">
        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <h2 class="h5 fw-semibold mb-3">Goal details</h2>

                <div class="mb-3">
                    <label class="form-label" for="goalName">Goal name</label>
                    <input class="form-control" type="text" id="goalName" name="goalName" required maxlength="80" placeholder="e.g., Get fitter in 30 days">
                </div>

                <div class="mb-3">
                    <label class="form-label" for="goalColor">Color</label>
                    <input class="form-control form-control-color" type="color" id="goalColor" name="goalColor" value="#2563eb" required>
                </div>

                <div class="mb-3">
                    <label class="form-label" for="goalDueDate">Due date (optional)</label>
                    <input class="form-control" type="date" id="goalDueDate" name="goalDueDate">
                </div>

                <div class="mb-0">
                    <label class="form-label" for="goalPriority">Priority</label>
                    <select class="form-select" id="goalPriority" name="goalPriority">
                        <option value="LOW">Low</option>
                        <option value="MEDIUM" selected>Medium</option>
                        <option value="HIGH">High</option>
                    </select>
                </div>
            </div>
        </div>

        <div class="card shadow-sm mb-4">
            <div class="card-body p-4">
                <div class="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
                    <h2 class="h5 fw-semibold mb-0">Habit subtasks</h2>
                    <button type="button" class="btn btn-outline-primary btn-sm" onclick="addRow()">Add subtask</button>
                </div>

                <div id="rows"></div>
                <p class="text-muted small mb-0">Each subtask creates a habit linked to this goal. You can leave extra rows empty.</p>
            </div>
        </div>

        <button type="submit" class="btn btn-primary">Create goal</button>
    </form>
</div>

<template id="rowTpl">
    <div class="subtask-block">
        <div class="row g-3">
            <div class="col-md-4">
                <label class="form-label">Title</label>
                <input class="form-control" type="text" name="habitName" maxlength="50" placeholder="e.g., Morning run">
            </div>
            <div class="col-md-4">
                <label class="form-label">Due date</label>
                <input class="form-control" type="date" name="habitDueDate">
            </div>
            <div class="col-md-4">
                <label class="form-label">Priority</label>
                <select class="form-select" name="habitPriority">
                    <option value="">--</option>
                    <option value="LOW">Low</option>
                    <option value="MEDIUM">Medium</option>
                    <option value="HIGH">High</option>
                </select>
            </div>
            <div class="col-12">
                <label class="form-label">Description</label>
                <textarea class="form-control" name="habitDescription" rows="2" maxlength="500" placeholder="Optional"></textarea>
            </div>
            <div class="col-12 text-end">
                <button type="button" class="btn btn-outline-danger btn-sm" onclick="removeRow(this)">Remove</button>
            </div>
        </div>
    </div>
</template>

<script>
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
        const row = btn.closest('.subtask-block');
        if (row) row.remove();
    }

    addRow(); addRow(); addRow();
</script>
</body>
</html>
