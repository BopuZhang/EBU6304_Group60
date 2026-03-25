package system;

import java.io.Serializable;

/**
 * User class - stores user basic information
 */
public class User implements Serializable {
    private String email;
    private String password;
    private String role;
    private String name;

    public User(String email, String password, String role, String name) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }

    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return email + "," + password + "," + role + "," + name;
    }
}