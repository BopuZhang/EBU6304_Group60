package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    private int applicantLimit;
    private List<String> skills;

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
        this.skills = new ArrayList<>();
    }

    public Job(String jobId, String moEmail, String moduleCode, String moduleName,
            String description, int weeklyHours, String deadline,
            String requirements, String status, int applicantLimit,
            List<String> skills) {
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
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    public String getJobId() {
        return jobId;
    }

    public String getMoEmail() {
        return moEmail;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getDescription() {
        return description;
    }

    public int getWeeklyHours() {
        return weeklyHours;
    }

    public String getDeadline() {
        return deadline;
    }

    public String getRequirements() {
        return requirements;
    }

    public String getStatus() {
        return status;
    }

    public int getApplicantLimit() {
        return applicantLimit;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setApplicantLimit(int applicantLimit) {
        this.applicantLimit = applicantLimit;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    @Override
    public String toString() {
        String skillsStr = skills == null ? "" : String.join(";", skills);
        return jobId + "," + moEmail + "," + moduleCode + "," + moduleName + "," +
                description + "," + weeklyHours + "," + deadline + "," +
                requirements + "," + status + "," + applicantLimit + "," + skillsStr;
    }

    public static Job fromString(String line) {
        String[] parts = line.split(",", -1);
        List<String> skills = new ArrayList<>();
        if (parts.length >= 11 && !parts[10].isEmpty()) {
            skills = new ArrayList<>(Arrays.asList(parts[10].split(";")));
        }
        if (parts.length >= 11) {
            return new Job(parts[0], parts[1], parts[2], parts[3], parts[4],
                    Integer.parseInt(parts[5]), parts[6], parts[7], parts[8],
                    Integer.parseInt(parts[9]), skills);
        } else if (parts.length >= 10) {
            return new Job(parts[0], parts[1], parts[2], parts[3], parts[4],
                    Integer.parseInt(parts[5]), parts[6], parts[7], parts[8],
                    Integer.parseInt(parts[9]));
        } else if (parts.length == 9) {
            return new Job(parts[0], parts[1], parts[2], parts[3], parts[4],
                    Integer.parseInt(parts[5]), parts[6], parts[7], parts[8], 0);
        }
        return null;
    }
}
