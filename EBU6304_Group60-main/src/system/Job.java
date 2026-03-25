package system;

import java.io.Serializable;

public class Job implements Serializable {
    private String jobId;
    private String moEmail;
    private String moduleCode;
    private String moduleName;
    private String description;
    private int weeklyHours;
    private String deadline;
    private String requirements;
    private String status;
    private int applicantLimit;  // 新增：人数上限

    // 更新构造方法
    public Job(String jobId, String moEmail, String moduleCode, String moduleName,
               String description, int weeklyHours, String deadline,
               String requirements, String status, int applicantLimit) {
        this.jobId = jobId;
        this.moEmail = moEmail;
        this.moduleCode = moduleCode;
        this.moduleName = moduleName;
        this.description = description;
        this.weeklyHours = weeklyHours;
        this.deadline = deadline;
        this.requirements = requirements;
        this.status = status;
        this.applicantLimit = applicantLimit;
    }

    // Getters
    public String getJobId() { return jobId; }
    public String getMoEmail() { return moEmail; }
    public String getModuleCode() { return moduleCode; }
    public String getModuleName() { return moduleName; }
    public String getDescription() { return description; }
    public int getWeeklyHours() { return weeklyHours; }
    public String getDeadline() { return deadline; }
    public String getRequirements() { return requirements; }
    public String getStatus() { return status; }
    public int getApplicantLimit() { return applicantLimit; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setApplicantLimit(int applicantLimit) { this.applicantLimit = applicantLimit; }

    @Override
    public String toString() {
        return jobId + "," + moEmail + "," + moduleCode + "," + moduleName + "," +
                description + "," + weeklyHours + "," + deadline + "," +
                requirements + "," + status + "," + applicantLimit;
    }

    public static Job fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 10) {
            return new Job(parts[0], parts[1], parts[2], parts[3], parts[4],
                    Integer.parseInt(parts[5]), parts[6], parts[7], parts[8],
                    Integer.parseInt(parts[9]));
        } else if (parts.length == 9) {
            // 兼容旧数据，没有 applicantLimit
            return new Job(parts[0], parts[1], parts[2], parts[3], parts[4],
                    Integer.parseInt(parts[5]), parts[6], parts[7], parts[8], 0);
        }
        return null;
    }
}