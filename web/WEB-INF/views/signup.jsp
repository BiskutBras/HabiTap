<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/login_style.css">
    <title>Sign up • HabiTap</title>
</head>
<body>
<div class="wrap">
    <div class="card">
        <div class="header">
            <div class="title">Create account</div>
            <div class="subtitle">Join HabiTap and start building habits</div>
        </div>

        <%
            String error = (String) request.getAttribute("error");
            if (error != null) {
        %>
        <div class="error"><%= error %></div>
        <%
            }
        %>

        <form method="POST" action="<%=request.getContextPath()%>/signup">
            <div class="row">
                <label>Username</label>
                <input type="text" name="username" required maxlength="60" autocomplete="username" placeholder="Choose a unique username">
            </div>

            <div class="row">
                <label>Email</label>
                <input type="email" name="email" required maxlength="120" autocomplete="email" placeholder="your@email.com">
            </div>

            <div class="row">
                <label>Password</label>
                <input type="password" name="password" required minlength="6" maxlength="120" autocomplete="new-password" placeholder="At least 6 characters">
            </div>

            <div class="row">
                <label>Confirm Password</label>
                <input type="password" name="passwordConfirm" required minlength="6" maxlength="120" autocomplete="new-password" placeholder="Re-enter password">
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Sign up</button>
            </div>
        </form>

        <div class="divider">or</div>

        <div class="login-section">
            <p class="login-text">Already have an account?</p>
            <a href="<%=request.getContextPath()%>/login" class="btn btn-secondary">Login</a>
        </div>
    </div>
</div>
</body>
</html>

