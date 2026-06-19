<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Login • HabiTap</title>
</head>
<body class="auth-page d-flex align-items-center min-vh-100 py-4">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-sm-10 col-md-6 col-lg-5 col-xl-4">
            <div class="card auth-card shadow-lg rounded-4">
                <div class="position-relative card-body p-4">
                    <div class="position-absolute top-0 start-50 translate-middle h2">HabiTap</div>
                    <div class="h6 lead">Plan Your Day</div>
                    <div class="mb-4">
                        <h1 class="h4 fw-bold mb-1">Welcome back</h1>
                        <p class="auth-subtitle mb-0">Sign in to continue</p>
                    </div>

                    <%
                        String error = (String) request.getAttribute("error");
                        if (error != null) {
                    %>
                    <div class="alert alert-danger py-2" role="alert"><%= error %></div>
                    <%
                        }
                    %>

                    <form method="POST" action="<%=request.getContextPath()%>/login">
                        <div class="mb-3">
                            <label class="form-label" for="username">Username</label>
                            <input class="form-control" type="text" id="username" name="username" required maxlength="30" autocomplete="username">
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="email">Email</label>
                            <input class="form-control" type="email" id="email" name="email" required maxlength="120" autocomplete="email">
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="password">Password</label>
                            <input class="form-control" type="password" id="password" name="password" required minlength="6" maxlength="120" autocomplete="current-password">
                        </div>

                        <button class="btn btn-primary w-100 fw-semibold" type="submit">Login</button>
                    </form>

                    <div class="text-center mt-3">
                        <a href="<%=request.getContextPath()%>/forgot-password" class="link-primary text-decoration-none small">Forgot password?</a>
                    </div>

                    <div class="text-center auth-divider my-3">or</div>

                    <p class="text-center auth-subtitle small mb-2">Don't have an account?</p>
                    <a href="<%=request.getContextPath()%>/signup" class="btn btn-outline-light w-100 fw-semibold">Create new account</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
