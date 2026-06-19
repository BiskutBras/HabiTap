<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Forgot Password • HabiTap</title>
</head>
<body class="auth-page d-flex align-items-center min-vh-100 py-4">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-sm-10 col-md-6 col-lg-5 col-xl-4">
            <div class="card auth-card shadow-lg rounded-4">
                <div class="card-body p-4">
                    <div class="mb-4">
                        <h1 class="h4 fw-bold mb-1">Reset password</h1>
                        <p class="auth-subtitle mb-0">Enter your email to recover your account</p>
                    </div>

                    <%
                        String message = (String) request.getAttribute("message");
                        String error = (String) request.getAttribute("error");
                        if (message != null) {
                    %>
                    <div class="alert alert-success py-2" role="alert"><%= message %></div>
                    <%
                        } else if (error != null) {
                    %>
                    <div class="alert alert-danger py-2" role="alert"><%= error %></div>
                    <%
                        }
                    %>

                    <form method="POST" action="<%=request.getContextPath()%>/forgot-password">
                        <div class="mb-3">
                            <label class="form-label" for="email">Email</label>
                            <input class="form-control" type="text" id="email" name="email" required maxlength="60" autocomplete="email" placeholder="Enter your email">
                        </div>

                        <button class="btn btn-primary w-100 fw-semibold" type="submit">Send reset link</button>
                    </form>

                    <div class="text-center mt-3">
                        <a href="<%=request.getContextPath()%>/login" class="link-primary text-decoration-none small">Back to login</a>
                    </div>

                    <div class="text-center auth-divider my-3">or</div>

                    <p class="text-center auth-subtitle small mb-2">Don't have an account?</p>
                    <a href="<%=request.getContextPath()%>/signup" class="btn btn-outline-light w-100 fw-semibold">Sign up</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
