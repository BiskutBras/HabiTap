<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/login_style.css">
    <title>Forgot Password • HabiTap</title>
</head>
<body>
<div class="wrap">
    <div class="card">
        <div class="header">
            <div class="title">Reset password</div>
            <div class="subtitle">Enter your email to recover your account</div>
        </div>

        <%
            String message = (String) request.getAttribute("message");
            String error = (String) request.getAttribute("error");
            if (message != null) {
        %>
        <div class="success"><%= message %></div>
        <%
            } else if (error != null) {
        %>
        <div class="error"><%= error %></div>
        <%
            }
        %>

        <form method="POST" action="<%=request.getContextPath()%>/forgot-password">
            <div class="row">
                <label>Email</label>
                <input type="text" name="email" required maxlength="60" autocomplete="email" placeholder="Enter your email">
            </div>

            <div class="actions">
                <button class="btn btn-primary" type="submit">Send reset link</button>
            </div>
        </form>

        <div class="footer-actions">
            <a href="<%=request.getContextPath()%>/login" class="link">Back to login</a>
        </div>

        <div class="divider">or</div>

        <div class="signup-section">
            <p class="signup-text">Don't have an account?</p>
            <a href="<%=request.getContextPath()%>/signup" class="btn btn-secondary">Sign up</a>
        </div>
    </div>
</div>
</body>
</html>

