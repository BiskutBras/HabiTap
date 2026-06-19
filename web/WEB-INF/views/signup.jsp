<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="/WEB-INF/views/includes/head.jsp"/>
    <title>Sign up • HabiTap</title>
</head>
<body class="auth-page d-flex align-items-center min-vh-100 py-4">
<div class="container">
    <div class="row justify-content-center">
        <div class="col-sm-10 col-md-6 col-lg-5 col-xl-4">
            <div class="card auth-card shadow-lg rounded-4">
                <div class="card-body p-4">
                    <div class="mb-4">
                        <h1 class="h4 fw-bold mb-1">Create account</h1>
                        <p class="auth-subtitle mb-0">Join HabiTap and start building habits</p>
                    </div>

                    <%
                        String error = (String) request.getAttribute("error");
                        if (error != null) {
                    %>
                    <div class="alert alert-danger py-2" role="alert"><%= error %></div>
                    <%
                        }
                    %>

                    <form method="POST" action="<%=request.getContextPath()%>/signup">
                        <div class="mb-3">
                            <label class="form-label" for="username">Username</label>
                            <input class="form-control" type="text" id="username" name="username" required maxlength="60" autocomplete="username" placeholder="Choose a unique username">
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="email">Email</label>
                            <input class="form-control" type="email" id="email" name="email" required maxlength="120" autocomplete="email" placeholder="your@email.com">
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="password">Password</label>
                            <input class="form-control" type="password" id="password" name="password" required minlength="6" maxlength="120" autocomplete="new-password" placeholder="At least 6 characters">
                        </div>

                        <div class="mb-3">
                            <label class="form-label" for="passwordConfirm">Confirm Password</label>
                            <input class="form-control" type="password" id="passwordConfirm" name="passwordConfirm" required minlength="6" maxlength="120" autocomplete="new-password" placeholder="Re-enter password">
                        </div>

                        <button class="btn btn-primary w-100 fw-semibold" type="submit">Sign up</button>
                    </form>

                    <div class="text-center auth-divider my-3">or</div>

                    <p class="text-center auth-subtitle small mb-2">Already have an account?</p>
                    <a href="<%=request.getContextPath()%>/login" class="btn btn-outline-light w-100 fw-semibold">Login</a>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
