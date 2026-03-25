package system;

import java.io.Serializable;

public class Profile implements Serializable {
    private String email;
    private String studentId;
    private String major;
    private String grade;
    private String phone;
    private String cvPath;
    private String description;  // 新增：个人描述

    // 更新构造方法
    public Profile(String email, String studentId, String major,
                   String grade, String phone, String cvPath, String description) {
        this.email = email;
        this.studentId = studentId;
        this.major = major;
        this.grade = grade;
        this.phone = phone;
        this.cvPath = cvPath;
        this.description = description;
    }

    // Getters and Setters
    public String getEmail() { return email; }
    public String getStudentId() { return studentId; }
    public String getMajor() { return major; }
    public String getGrade() { return grade; }
    public String getPhone() { return phone; }
    public String getCvPath() { return cvPath; }
    public String getDescription() { return description; }

    public void setMajor(String major) { this.major = major; }
    public void setGrade(String grade) { this.grade = grade; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setCvPath(String cvPath) { this.cvPath = cvPath; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return email + "," + studentId + "," + major + "," + grade + "," +
                phone + "," + cvPath + "," + (description != null ? description : "");
    }

    public static Profile fromString(String line) {
        String[] parts = line.split(",", -1);  // 保留空字段
        if (parts.length >= 7) {
            return new Profile(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], parts[6]);
        } else if (parts.length == 6) {
            // 兼容旧数据，没有 description
            return new Profile(parts[0], parts[1], parts[2], parts[3],
                    parts[4], parts[5], "");
        }
        return null;
    }
}