<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/login_style.css">
    <title>Login • HabiTap</title>
</head>
<body>
<div class="wrap">
    <div class="card">
        <div class="header">
            <div class="title">Welcome back</div>
            <div class="subtitle">Sign in to continue</div>
        </div>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
        <div class="error"><%= error %></div>
        <%
            }
        %>

        <form method="POST" action="<%=request.getContextPath()%>/login">
            <div class="row">
                <label>Username</label>
                <input type="text" name="username" required maxlength="30" autocomplete="username">
            </div>

            <div class="row">
                <label>Email</label>
                <input type="email" name="email" required maxlength="120" autocomplete="email">
            </div>

            <div class="row">
                <label>Password</label>
                <input type="password" name="password" required minlength="6" maxlength="120" autocomplete="current-password">
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Login</button>
            </div>
        </form>
    </div>
</div>
</body>
</html>

