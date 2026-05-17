package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Profile implements Serializable {
    private String email;
    private String studentId;
    private String major;
    private String grade;
    private String phone;
    private String cvPath;
    private String description;
    private List<String> skills;

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

    public String getEmail() {
        return email;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getMajor() {
        return major;
    }

    public String getGrade() {
        return grade;
    }

    public String getPhone() {
        return phone;
    }

    public String getCvPath() {
        return cvPath;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setCvPath(String cvPath) {
        this.cvPath = cvPath;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    @Override
    public String toString() {
        String skillsStr = skills == null ? "" : String.join(";", skills);
        return email + "," + studentId + "," + major + "," + grade + "," +
                phone + "," + cvPath + "," + (description != null ? description : "") +
                "," + skillsStr;
    }

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
