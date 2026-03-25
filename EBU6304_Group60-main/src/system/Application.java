package system;

import java.io.Serializable;
import java.util.List;

public class Application implements Serializable {
    private String applicationId;
    private String applyTime;
    private String taEmail;
    private String jobId;
    private String status;

    public Application(String applicationId, String applyTime, String taEmail,
                       String jobId, String status) {
        this.applicationId = applicationId;
        this.applyTime = applyTime;
        this.taEmail = taEmail;
        this.jobId = jobId;
        this.status = status;
    }

    public String getApplicationId() { return applicationId; }
    public String getApplyTime() { return applyTime; }
    public String getTaEmail() { return taEmail; }
    public String getJobId() { return jobId; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return applicationId + "," + applyTime + "," + taEmail + "," +
                jobId + "," + status;
    }

    public static Application fromString(String line) {
        String[] parts = line.split(",");
        if (parts.length >= 5) {
            return new Application(parts[0], parts[1], parts[2], parts[3], parts[4]);
        }
        return null;
    }
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