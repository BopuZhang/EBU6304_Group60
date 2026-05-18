package system;

import java.io.Serializable;
import java.util.List;

/**
 * Represents a TA's application to a job position.
 * <p>
 * An Application links a Teaching Assistant (TA) to a Job and tracks the
 * application status throughout the review process. Applications can have
 * three statuses: PENDING (awaiting review), ACCEPTED (approved by MO),
 * or REJECTED (declined by MO).
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
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Unique identifier for this application */
    private String applicationId;

    /** Timestamp when the application was submitted */
    private String applyTime;

    /** Email of the TA who submitted this application */
    private String taEmail;

    /** ID of the job being applied for */
    private String jobId;

    /** Current status: "PENDING", "ACCEPTED", or "REJECTED" */
    private String status;

    /**
     * Constructs a new Application.
     *
     * @param applicationId unique identifier for this application
     * @param applyTime     timestamp of submission
     * @param taEmail       email of the applicant TA
     * @param jobId         ID of the target job
     * @param status        initial status
     */
    public Application(String applicationId, String applyTime, String taEmail,
            String jobId, String status) {
        this.applicationId = applicationId;
        this.applyTime = applyTime;
        this.taEmail = taEmail;
        this.jobId = jobId;
        this.status = status;
    }

    /**
     * Returns the application ID.
     *
     * @return the application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Returns the application timestamp.
     *
     * @return the apply time string
     */
    public String getApplyTime() {
        return applyTime;
    }

    /**
     * Returns the applicant's email.
     *
     * @return the TA email
     */
    public String getTaEmail() {
        return taEmail;
    }

    /**
     * Returns the target job ID.
     *
     * @return the job ID
     */
    public String getJobId() {
        return jobId;
    }

    /**
     * Returns the current status.
     *
     * @return "PENDING", "ACCEPTED", or "REJECTED"
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the application status.
     *
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Returns a string representation of this application in CSV format.
     * <p>
     * Format: {@code applicationId,applyTime,taEmail,jobId,status}
     * </p>
     *
     * @return the CSV string representation
     */
    @Override
    public String toString() {
        return applicationId + "," + applyTime + "," + taEmail + "," +
                jobId + "," + status;
    }

    /**
     * Parses a CSV string to create an Application object.
     *
     * @param line the CSV string to parse
     * @return the parsed Application, or null if the line format is invalid
     */
    public static Application fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            return new Application(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }

    /**
     * Returns the Job object associated with this application.
     * <p>
     * Loads all jobs from storage and finds the one matching this application's
     * jobId.
     * </p>
     *
     * @return the associated Job, or null if not found
     */
    public Job getJob() {
        List<Job> jobs = FileUtil.loadJobs();
        for (Job job : jobs) {
            if (job.getJobId().equals(this.jobId)) {
                return job;
            }
        }
        return null;
    }
}