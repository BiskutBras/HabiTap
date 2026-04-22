package model;

public class AuthUser {
    private final Integer id;
    private final String username;
    private final String email;

    public AuthUser(String username, String email) {
        this(null, username, email);
    }

    public AuthUser(Integer id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    public Integer getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
}

