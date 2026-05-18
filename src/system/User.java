package system;

import java.io.Serializable;

/**
 * Represents a user in the TA Management System.
 * <p>
 * This class stores basic user information including authentication credentials
 * and role-based access control. Users can have one of three roles: TA
 * (Teaching Assistant),
 * MO (Module Organizer), or ADMIN (Administrator).
 * </p>
 * <p>
 * The class implements {@link Serializable} to support persistence via file
 * storage.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2025
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /** The user's email address, used as the unique identifier */
    private String email;

    /** The user's password for authentication */
    private String password;

    /** The user's role: "TA", "MO", or "ADMIN" */
    private String role;

    /** The user's display name */
    private String name;

    /**
     * Constructs a new User with the specified credentials and information.
     *
     * @param email    the user's email address (unique identifier)
     * @param password the user's password for authentication
     * @param role     the user's role ("TA", "MO", or "ADMIN")
     * @param name     the user's display name
     */
    public User(String email, String password, String role, String name) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the user's password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the user's role.
     *
     * @return the role ("TA", "MO", or "ADMIN")
     */
    public String getRole() {
        return role;
    }

    /**
     * Returns the user's display name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the user's password.
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Sets the user's display name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns a string representation of this user in CSV format.
     * <p>
     * Format: {@code email,password,role,name}
     * </p>
     *
     * @return the CSV string representation
     */
    @Override
    public String toString() {
        return email + "," + password + "," + role + "," + name;
    }
}