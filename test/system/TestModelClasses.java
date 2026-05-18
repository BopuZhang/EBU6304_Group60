package system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestModelClasses {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== Model Classes Tests ==========\n");
        testUser();
        testJob();
        testApplication();
        testProfile();
        testNotification();
        System.out.println("\n========== Results: " + passed + " passed, " + failed + " failed ==========");
        if (failed > 0) System.exit(1);
    }

    private static void assertEquals(String testName, Object expected, Object actual) {
        if (expected == null && actual == null) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else if (expected != null && expected.equals(actual)) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else {
            System.out.println("  [FAIL] " + testName + " — expected: " + expected + ", actual: " + actual);
            failed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else {
            System.out.println("  [FAIL] " + testName + " — expected true");
            failed++;
        }
    }

    private static void assertFalse(String testName, boolean condition) {
        if (!condition) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else {
            System.out.println("  [FAIL] " + testName + " — expected false");
            failed++;
        }
    }

    private static void assertNotNull(String testName, Object obj) {
        if (obj != null) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else {
            System.out.println("  [FAIL] " + testName + " — expected non-null");
            failed++;
        }
    }

    // ==================== User Tests ====================
    private static void testUser() {
        System.out.println("--- User ---");

        User u = new User("ta01@qmul.ac.uk", "pass123", "TA", "Alice");
        assertEquals("getEmail", "ta01@qmul.ac.uk", u.getEmail());
        assertEquals("getPassword", "pass123", u.getPassword());
        assertEquals("getRole", "TA", u.getRole());
        assertEquals("getName", "Alice", u.getName());

        u.setPassword("newPass");
        assertEquals("setPassword", "newPass", u.getPassword());

        u.setName("Alice Wang");
        assertEquals("setName", "Alice Wang", u.getName());

        String str = u.toString();
        assertTrue("toString contains email", str.contains("ta01@qmul.ac.uk"));
        assertTrue("toString contains role", str.contains("TA"));

        User u2 = new User("mo01@qmul.ac.uk", "moPass", "MO", "Bob");
        assertEquals("MO role", "MO", u2.getRole());
        assertEquals("MO name", "Bob", u2.getName());

        User u3 = new User("admin@qmul.ac.uk", "admin", "ADMIN", "Admin");
        assertEquals("Admin role", "ADMIN", u3.getRole());
    }

    // ==================== Job Tests ====================
    private static void testJob() {
        System.out.println("--- Job ---");

        List<String> skills = Arrays.asList("Java", "Python", "SQL");
        Job job = new Job("J001", "mo01@qmul.ac.uk", "COMP2201",
                "Software Engineering", "Teach labs", 10,
                "2025-06-30", "Java experience", "OPEN", 3, skills);

        assertEquals("getJobId", "J001", job.getJobId());
        assertEquals("getMoEmail", "mo01@qmul.ac.uk", job.getMoEmail());
        assertEquals("getModuleCode", "COMP2201", job.getModuleCode());
        assertEquals("getModuleName", "Software Engineering", job.getModuleName());
        assertEquals("getDescription", "Teach labs", job.getDescription());
        assertEquals("getWeeklyHours", 10, job.getWeeklyHours());
        assertEquals("getDeadline", "2025-06-30", job.getDeadline());
        assertEquals("getRequirements", "Java experience", job.getRequirements());
        assertEquals("getStatus", "OPEN", job.getStatus());
        assertEquals("getApplicantLimit", 3, job.getApplicantLimit());
        assertEquals("getSkills size", 3, job.getSkills().size());
        assertTrue("has Java skill", job.getSkills().contains("Java"));
        assertTrue("has Python skill", job.getSkills().contains("Python"));

        job.setStatus("CLOSED");
        assertEquals("setStatus", "CLOSED", job.getStatus());

        job.setApplicantLimit(5);
        assertEquals("setApplicantLimit", 5, job.getApplicantLimit());

        job.setSkills(Arrays.asList("C++", "Rust"));
        assertEquals("setSkills size", 2, job.getSkills().size());
        assertTrue("has C++", job.getSkills().contains("C++"));

        String str = job.toString();
        assertTrue("toString contains jobId", str.startsWith("J001"));
        assertTrue("toString contains CLOSED", str.contains("CLOSED"));

        Job jobNoSkills = new Job("J002", "mo02@qmul.ac.uk", "COMP2202",
                "Data Structures", "Tutorials", 8,
                "2025-07-15", "Algorithms", "OPEN", 2);
        assertEquals("no skills size", 0, jobNoSkills.getSkills().size());
        assertNotNull("skills not null", jobNoSkills.getSkills());

        Job parsed = Job.fromString(job.toString());
        assertNotNull("fromString not null", parsed);
        assertEquals("fromString jobId", "J001", parsed.getJobId());
        assertEquals("fromString weeklyHours", 10, parsed.getWeeklyHours());
        assertEquals("fromString status", "CLOSED", parsed.getStatus());

        String parts9 = "J003,mo03@qmul.ac.uk,COMP2203,Networks,Lab help,6,2025-08-01,TCP/IP,OPEN";
        Job parsed9 = Job.fromString(parts9);
        assertNotNull("fromString 9 parts", parsed9);
        assertEquals("fromString 9 parts status", "OPEN", parsed9.getStatus());
        assertEquals("fromString 9 parts limit", 0, parsed9.getApplicantLimit());
    }

    // ==================== Application Tests ====================
    private static void testApplication() {
        System.out.println("--- Application ---");

        Application app = new Application("A001", "2025-05-10 14:30:00",
                "ta01@qmul.ac.uk", "J001", "PENDING");

        assertEquals("getApplicationId", "A001", app.getApplicationId());
        assertEquals("getApplyTime", "2025-05-10 14:30:00", app.getApplyTime());
        assertEquals("getTaEmail", "ta01@qmul.ac.uk", app.getTaEmail());
        assertEquals("getJobId", "J001", app.getJobId());
        assertEquals("getStatus", "PENDING", app.getStatus());

        app.setStatus("ACCEPTED");
        assertEquals("setStatus", "ACCEPTED", app.getStatus());

        app.setStatus("REJECTED");
        assertEquals("setStatus rejected", "REJECTED", app.getStatus());

        String str = app.toString();
        assertTrue("toString contains A001", str.startsWith("A001"));
        assertTrue("toString contains REJECTED", str.contains("REJECTED"));

        Application parsed = Application.fromString(str);
        assertNotNull("fromString not null", parsed);
        assertEquals("fromString applicationId", "A001", parsed.getApplicationId());
        assertEquals("fromString status", "REJECTED", parsed.getStatus());

        Application nullParsed = Application.fromString("short,line");
        assertEquals("short line returns null", null, nullParsed);

        Application nullParsed2 = Application.fromString("");
        assertEquals("empty line returns null", null, nullParsed2);
    }

    // ==================== Profile Tests ====================
    private static void testProfile() {
        System.out.println("--- Profile ---");

        List<String> skills = Arrays.asList("Java", "Communication", "Teamwork");
        Profile p = new Profile("ta01@qmul.ac.uk", "210123456",
                "Computer Science", "2:1", "1234567890",
                "cv_ta01.pdf", "Hardworking TA", skills);

        assertEquals("getEmail", "ta01@qmul.ac.uk", p.getEmail());
        assertEquals("getStudentId", "210123456", p.getStudentId());
        assertEquals("getMajor", "Computer Science", p.getMajor());
        assertEquals("getGrade", "2:1", p.getGrade());
        assertEquals("getPhone", "1234567890", p.getPhone());
        assertEquals("getCvPath", "cv_ta01.pdf", p.getCvPath());
        assertEquals("getDescription", "Hardworking TA", p.getDescription());
        assertEquals("getSkills size", 3, p.getSkills().size());

        p.setMajor("Mathematics");
        assertEquals("setMajor", "Mathematics", p.getMajor());

        p.setGrade("1st");
        assertEquals("setGrade", "1st", p.getGrade());

        p.setPhone("0987654321");
        assertEquals("setPhone", "0987654321", p.getPhone());

        p.setCvPath("new_cv.pdf");
        assertEquals("setCvPath", "new_cv.pdf", p.getCvPath());

        p.setDescription("Very hardworking");
        assertEquals("setDescription", "Very hardworking", p.getDescription());

        p.setSkills(Arrays.asList("Python", "C++"));
        assertEquals("setSkills size", 2, p.getSkills().size());

        p.setSkills(null);
        assertNotNull("setSkills null safe", p.getSkills());
        assertEquals("setSkills null empty", 0, p.getSkills().size());

        String str = p.toString();
        assertTrue("toString contains email", str.startsWith("ta01@qmul.ac.uk"));

        Profile parsed = Profile.fromString(str);
        assertNotNull("fromString not null", parsed);
        assertEquals("fromString email", "ta01@qmul.ac.uk", parsed.getEmail());

        String noSkillsLine = "ta02@qmul.ac.uk,210654321,Physics,2:2,1111111111,cv2.pdf,desc";
        Profile parsed2 = Profile.fromString(noSkillsLine);
        assertNotNull("fromString no skills", parsed2);
        assertEquals("fromString no skills email", "ta02@qmul.ac.uk", parsed2.getEmail());
        assertEquals("fromString no skills size", 0, parsed2.getSkills().size());

        String minParts = "ta03@qmul.ac.uk,210999999,Maths,1st,2222222222,cv3.pdf";
        Profile parsed3 = Profile.fromString(minParts);
        assertNotNull("fromString 6 parts", parsed3);
        assertEquals("fromString 6 parts email", "ta03@qmul.ac.uk", parsed3.getEmail());
    }

    // ==================== Notification Tests ====================
    private static void testNotification() {
        System.out.println("--- Notification ---");

        Notification n = new Notification("NOT001", "ta01@qmul.ac.uk",
                "Application Accepted", "Your application for COMP2201 was accepted.",
                "ACCEPT", "2025-05-15 10:00:00", false);

        assertEquals("getNotificationId", "NOT001", n.getNotificationId());
        assertEquals("getRecipientEmail", "ta01@qmul.ac.uk", n.getRecipientEmail());
        assertEquals("getTitle", "Application Accepted", n.getTitle());
        assertEquals("getType", "ACCEPT", n.getType());
        assertEquals("getCreateTime", "2025-05-15 10:00:00", n.getCreateTime());
        assertFalse("isRead false", n.isRead());

        n.setRead(true);
        assertTrue("setRead true", n.isRead());

        n.setRead(false);
        assertFalse("setRead false again", n.isRead());

        String str = n.toString();
        assertTrue("toString contains NOT001", str.startsWith("NOT001"));
        assertTrue("toString contains false", str.endsWith("false"));

        Notification parsed = Notification.fromString(str);
        assertNotNull("fromString not null", parsed);
        assertEquals("fromString id", "NOT001", parsed.getNotificationId());
        assertFalse("fromString isRead", parsed.isRead());

        String readStr = "NOT002,mo01@qmul.ac.uk,Title,Content,TYPE,2025-06-01 09:00:00,true";
        Notification parsed2 = Notification.fromString(readStr);
        assertNotNull("fromString read not null", parsed2);
        assertTrue("fromString read isRead", parsed2.isRead());

        Notification nullParsed = Notification.fromString("short");
        assertEquals("short line null", null, nullParsed);
    }
}