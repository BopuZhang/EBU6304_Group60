package system;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a TA job position in the TA Management System.
 * <p>
 * A Job contains all information about a teaching assistant position including
 * the module details, workload hours, deadline, required skills, and
 * application
 * settings. Jobs are created by Module Organizers (MO) and can be applied for
 * by Teaching Assistants (TA).
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
public class Job implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Unique identifier for this job */
    private String jobId;

    /** Email of the Module Organizer who created this job */
    private String moEmail;

    /** Module code (e.g., "COMP2201") */
    private String moduleCode;

    /** Full module name (e.g., "Software Engineering") */
    private String moduleName;

    /** Description of the TA duties */
    private String description;

    /** Weekly hours required for this position */
    private int weeklyHours;

    /** Application deadline in "yyyy-MM-dd" format */
    private String deadline;

    /** Requirements for applicants */
    private String requirements;

    /** Current status: "OPEN" or "CLOSED" */
    private String status;

    /** Maximum number of applicants allowed */
    private int applicantLimit;

    /** List of required/preferred skills */
    private List<String> skills;

    /**
     * Constructs a new Job without skills.
     * <p>
     * Skills list is initialized as empty.
     * </p>
     *
     * @param jobId          unique identifier for this job
     * @param moEmail        email of the Module Organizer
     * @param moduleCode     module code
     * @param moduleName     full module name
     * @param description    description of TA duties
     * @param weeklyHours    weekly hours required
     * @param deadline       application deadline
     * @param requirements   requirements for applicants
     * @param status         current status ("OPEN" or "CLOSED")
     * @param applicantLimit maximum number of applicants
     */
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

    /**
     * Constructs a new Job with skills.
     *
     * @param jobId          unique identifier for this job
     * @param moEmail        email of the Module Organizer
     * @param moduleCode     module code
     * @param moduleName     full module name
     * @param description    description of TA duties
     * @param weeklyHours    weekly hours required
     * @param deadline       application deadline
     * @param requirements   requirements for applicants
     * @param status         current status ("OPEN" or "CLOSED")
     * @param applicantLimit maximum number of applicants
     * @param skills         list of required skills (null-safe)
     */
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

    /**
     * Returns the unique job identifier.
     *
     * @return the job ID
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Returns the Module Organizer's email.
     *
     * @return the MO email
     */
    public String getMoEmail() {
        return moEmail;
    }

    /**
     * Returns the module code.
     *
     * @return the module code
     */
    public String getModuleCode() {
        return moduleCode;
    }

    /**
     * Returns the full module name.
     *
     * @return the module name
     */
    public String getModuleName() {
        return moduleName;
    }

    /**
     * Returns the job description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the weekly hours required.
     *
     * @return the weekly hours
     */
    public int getWeeklyHours() {
        return weeklyHours;
    }

    /**
     * Returns the application deadline.
     *
     * @return the deadline string
     */
    public String getDeadline() {
        return deadline;
    }

    /**
     * Returns the requirements.
     *
     * @return the requirements string
     */
    public String getRequirements() {
        return requirements;
    }

    /**
     * Returns the current status.
     *
     * @return "OPEN" or "CLOSED"
     */
    public String getStatus() {
        return status;
    }

    /**
     * Returns the applicant limit.
     *
     * @return the maximum number of applicants
     */
    public int getApplicantLimit() {
        return applicantLimit;
    }

    /**
     * Returns the list of required skills.
     *
     * @return the skills list (never null)
     */
    public List<String> getSkills() {
        return skills;
    }

    /**
     * Sets the job status.
     *
     * @param status the new status ("OPEN" or "CLOSED")
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Sets the applicant limit.
     *
     * @param applicantLimit the new limit
     */
    public void setApplicantLimit(int applicantLimit) {
        this.applicantLimit = applicantLimit;
    }

    /**
     * Sets the required skills.
     *
     * @param skills the skills list (null-safe, converts null to empty list)
     */
    public void setSkills(List<String> skills) {
        this.skills = skills != null ? skills : new ArrayList<>();
    }

    /**
     * Returns a string representation of this job in CSV format.
     * <p>
     * Format: {@code jobId,moEmail,moduleCode,moduleName,description,weeklyHours,
     * deadline,requirements,status,applicantLimit,skills}
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
        return jobId + "," + moEmail + "," + moduleCode + "," + moduleName + "," +
                description + "," + weeklyHours + "," + deadline + "," +
                requirements + "," + status + "," + applicantLimit + "," + skillsStr;
    }

    /**
     * Parses a CSV string to create a Job object.
     * <p>
     * Supports lines with 9, 10, or 11 comma-separated fields.
     * For 9 fields, applicantLimit defaults to 0.
     * For 10 fields, skills defaults to empty list.
     * </p>
     *
     * @param line the CSV string to parse
     * @return the parsed Job, or null if the line format is invalid
     */
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