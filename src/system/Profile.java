package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a Teaching Assistant's profile information.
 * <p>
 * A Profile contains detailed information about a TA including academic
 * details,
 * contact information, CV, self-description, and skills. Each TA has one
 * profile
 * linked to their email address.
 * </p>
 * <p>
 * The class implements {@link Serializable} to support persistence via file
 * storage.
 * </p>
 *
 * @author EBU6304 Group60
 * @version 1.0
 * @since 2026
 */
public class Profile implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Email of the TA (links to User account) */
    private String email;

    /** Student ID number */
    private String studentId;

    /** Major/degree program */
    private String major;

    /** Academic grade/classification */
    private String grade;

    /** Phone number */
    private String phone;

    /** Path to uploaded CV file */
    private String cvPath;

    /** Self-description/bio */
    private String description;

    /** List of skills */
    private List<String> skills;

    /**
     * Constructs a new Profile without skills.
     * <p>
     * Skills list is initialized as empty.
     * </p>
     *
     * @param email       the TA's email
     * @param studentId   the student ID
     * @param major       the major/degree
     * @param grade       the academic grade
     * @param phone       the phone number
     * @param cvPath      path to CV file
     * @param description self-description
     */
    public Profile(String email, String studentId, String major,
            String grade, String phone, String cvPath, String description) {
        this.email = email;
        this.studentId = studentId;
        this.major = major;
        this.grade = grade;
        this.phone = phone;
        this.cvPath = cvPath;
        this.description = description;
        this.skills = new ArrayList<>();
    }

    /**
     * Constructs a new Profile with skills.
     *
     * @param email       the TA's email
     * @param studentId   the student ID
     * @param major       the major/degree
     * @param grade       the academic grade
     * @param phone       the phone number
     * @param cvPath      path to CV file
     * @param description self-description
     * @param skills      list of skills (null-safe)
     */
    public Profile(String email, String studentId, String major,
            String grade, String phone, String cvPath, String description,
            List<String> skills) {
        this.email = email;
        this.studentId = studentId;
        this.major = major;
        this.grade = grade;
        this.phone = phone;
        this.cvPath = cvPath;
        this.description = description;
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    /**
     * Returns the TA's email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the student ID.
     *
     * @return the student ID
     */
    public String getStudentId() {
        return studentId;
    }

    /**
     * Returns the major.
     *
     * @return the major
     */
    public String getMajor() {
        return major;
    }

    /**
     * Returns the academic grade.
     *
     * @return the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Returns the phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Returns the CV file path.
     *
     * @return the CV path
     */
    public String getCvPath() {
        return cvPath;
    }

    /**
     * Returns the self-description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the list of skills.
     *
     * @return the skills list (never null)
     */
    public List<String> getSkills() {
        return skills;
    }

    /**
     * Sets the major.
     *
     * @param major the new major
     */
    public void setMajor(String major) {
        this.major = major;
    }

    /**
     * Sets the academic grade.
     *
     * @param grade the new grade
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }

    /**
     * Sets the phone number.
     *
     * @param phone the new phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Sets the CV file path.
     *
     * @param cvPath the new CV path
     */
    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    /**
     * Sets the self-description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the skills list.
     *
     * @param skills the new skills list (null-safe)
     */
    public void setSkills(List<String> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    /**
     * Returns a string representation of this profile in CSV format.
     * <p>
     * Format: {@code email,studentId,major,grade,phone,cvPath,description,skills}
     * </p>
     * <p>
     * Skills are joined with semicolons as delimiters.
     * </p>
     *
     * @return the CSV string representation
     */
    @Override
    public String toString() {
        String skillsStr = skills == null ? "" : String.join(";", skills);
        return email + "," + studentId + "," + major + "," + grade + "," +
                phone + "," + cvPath + "," + (description != null ? description : "") +
                "," + skillsStr;
    }

    /**
     * Parses a CSV string to create a Profile object.
     * <p>
     * Supports lines with 6, 7, or 8 comma-separated fields.
     * </p>
     *
     * @param line the CSV string to parse
     * @return the parsed Profile, or null if the line format is invalid
     */
    public static Profile fromString(String line) {
        String[] parts = line.split(",", -1);
        List<String> skills = new ArrayList<>();
        if (parts.length >= 8 && !parts[7].isEmpty()) {
            skills = new ArrayList<>(Arrays.asList(parts[7].split(";")));
        }
        if (parts.length >= 8) {
            return new Profile(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], parts[6], skills);
        } else if (parts.length >= 7) {
            return new Profile(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], parts[6]);
        } else if (parts.length == 6) {
            return new Profile(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], "");
        }
        return null;
    }
}