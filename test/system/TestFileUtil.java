package system;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFileUtil {
    private static int passed = 0;
    private static int failed = 0;
    private static final File backupDir = new File("data_backup_test/");

    public static void main(String[] args) {
        System.out.println("========== FileUtil Tests ==========\n");
        try {
            backupData();
            testLoadSaveUsers();
            testProfileOperations();
            testJobOperations();
            testApplicationOperations();
            testNotificationOperations();
            restoreData();
        } catch (Exception e) {
            System.out.println("  [ERROR] Test aborted: " + e.getMessage());
            e.printStackTrace();
            restoreData();
        }
        System.out.println("\n========== Results: " + passed + " passed, " + failed + " failed ==========");
        if (failed > 0) System.exit(1);
    }

    private static void backupData() {
        File dataDir = new File("data");
        if (dataDir.exists() && dataDir.isDirectory()) {
            backupDir.mkdirs();
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.renameTo(new File(backupDir, f.getName()));
                }
            }
        }
    }

    private static void restoreData() {
        File dataDir = new File("data");
        if (dataDir.exists()) {
            File[] files = dataDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
        if (backupDir.exists() && backupDir.isDirectory()) {
            File[] files = backupDir.listFiles();
            if (files != null) {
                new File("data").mkdirs();
                for (File f : files) {
                    f.renameTo(new File("data", f.getName()));
                }
            }
            backupDir.delete();
        }
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
            System.out.println("  [FAIL] " + testName);
            failed++;
        }
    }

    private static void assertNotNull(String testName, Object obj) {
        if (obj != null) {
            System.out.println("  [PASS] " + testName);
            passed++;
        } else {
            System.out.println("  [FAIL] " + testName);
            failed++;
        }
    }

    // ==================== User I/O Tests ====================
    private static void testLoadSaveUsers() {
        System.out.println("--- User I/O ---");

        List<User> testUsers = new ArrayList<>();
        testUsers.add(new User("testta@qmul.ac.uk", "taPass", "TA", "Test TA"));
        testUsers.add(new User("testmo@qmul.ac.uk", "moPass", "MO", "Test MO"));
        testUsers.add(new User("testadmin@qmul.ac.uk", "adminPass", "ADMIN", "Test Admin"));
        FileUtil.saveUsers(testUsers);

        List<User> loaded = FileUtil.loadUsers();
        assertNotNull("loaded not null", loaded);
        assertEquals("loaded size", 3, loaded.size());
        assertEquals("loaded[0] email", "testta@qmul.ac.uk", loaded.get(0).getEmail());
        assertEquals("loaded[0] role", "TA", loaded.get(0).getRole());
        assertEquals("loaded[1] email", "testmo@qmul.ac.uk", loaded.get(1).getEmail());
        assertEquals("loaded[2] role", "ADMIN", loaded.get(2).getRole());

        FileUtil.saveUsers(new ArrayList<>());
        List<User> empty = FileUtil.loadUsers();
        assertEquals("empty users", 0, empty.size());
    }

    // ==================== Profile I/O Tests ====================
    private static void testProfileOperations() {
        System.out.println("--- Profile I/O ---");

        List<Profile> profiles = new ArrayList<>();
        profiles.add(new Profile("testta@qmul.ac.uk", "21000001",
                "CS", "1st", "111111", "cv1.pdf",
                "Test desc", Arrays.asList("Java", "Python")));
        profiles.add(new Profile("testta2@qmul.ac.uk", "21000002",
                "Maths", "2:1", "222222", "cv2.pdf",
                "Another desc", Arrays.asList("C++")));
        FileUtil.saveProfiles(profiles);

        List<Profile> loaded = FileUtil.loadProfiles();
        assertEquals("profiles size", 2, loaded.size());

        Profile found = FileUtil.getProfileByEmail("testta@qmul.ac.uk");
        assertNotNull("getProfileByEmail found", found);
        assertEquals("profile email", "testta@qmul.ac.uk", found.getEmail());
        assertEquals("profile studentId", "21000001", found.getStudentId());
        assertEquals("profile skills size", 2, found.getSkills().size());

        Profile notFound = FileUtil.getProfileByEmail("nonexistent@qmul.ac.uk");
        assertEquals("getProfileByEmail not found", null, notFound);

        profiles.get(0).setGrade("Distinction");
        FileUtil.saveProfiles(profiles);
        Profile updated = FileUtil.getProfileByEmail("testta@qmul.ac.uk");
        assertEquals("updated grade", "Distinction", updated.getGrade());
    }

    // ==================== Job I/O Tests ====================
    private static void testJobOperations() {
        System.out.println("--- Job I/O ---");

        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job("JTEST001", "testmo@qmul.ac.uk", "TEST101",
                "Test Module", "Test lab", 8,
                "2025-12-31", "None", "OPEN", 5,
                Arrays.asList("Java", "Git")));
        jobs.add(new Job("JTEST002", "testmo@qmul.ac.uk", "TEST102",
                "Test Module 2", "Test tutorial", 4,
                "2025-11-30", "Python", "OPEN", 3));
        FileUtil.saveJobs(jobs);

        List<Job> loaded = FileUtil.loadJobs();
        assertEquals("jobs size", 2, loaded.size());
        assertEquals("job[0] id", "JTEST001", loaded.get(0).getJobId());
        assertEquals("job[0] hours", 8, loaded.get(0).getWeeklyHours());
        assertEquals("job[0] skills", 2, loaded.get(0).getSkills().size());
        assertEquals("job[1] skills", 0, loaded.get(1).getSkills().size());

        loaded.get(0).setStatus("CLOSED");
        FileUtil.saveJobs(loaded);
        List<Job> reloaded = FileUtil.loadJobs();
        assertEquals("closed status", "CLOSED", reloaded.get(0).getStatus());
    }

    // ==================== Application I/O Tests ====================
    private static void testApplicationOperations() {
        System.out.println("--- Application I/O ---");

        List<Application> apps = new ArrayList<>();
        apps.add(new Application("ATEST001", "2025-05-01 10:00:00",
                "testta@qmul.ac.uk", "JTEST001", "PENDING"));
        apps.add(new Application("ATEST002", "2025-05-02 11:00:00",
                "testta2@qmul.ac.uk", "JTEST001", "ACCEPTED"));
        apps.add(new Application("ATEST003", "2025-05-03 12:00:00",
                "testta@qmul.ac.uk", "JTEST002", "REJECTED"));
        FileUtil.saveApplications(apps);

        List<Application> loaded = FileUtil.loadApplications();
        assertEquals("apps size", 3, loaded.size());
        assertEquals("app[0] status", "PENDING", loaded.get(0).getStatus());
        assertEquals("app[1] status", "ACCEPTED", loaded.get(1).getStatus());
        assertEquals("app[2] status", "REJECTED", loaded.get(2).getStatus());

        loaded.get(0).setStatus("ACCEPTED");
        FileUtil.saveApplications(loaded);
        List<Application> reloaded = FileUtil.loadApplications();
        assertEquals("updated status", "ACCEPTED", reloaded.get(0).getStatus());
    }

    // ==================== Notification I/O Tests ====================
    private static void testNotificationOperations() {
        System.out.println("--- Notification I/O ---");

        List<Notification> notifs = new ArrayList<>();
        notifs.add(new Notification("NOTEST001", "testta@qmul.ac.uk",
                "Welcome", "Welcome to the system", "INFO",
                "2025-05-01 09:00:00", false));
        notifs.add(new Notification("NOTEST002", "testta@qmul.ac.uk",
                "Accepted", "You were accepted", "ACCEPT",
                "2025-05-15 14:00:00", true));
        notifs.add(new Notification("NOTEST003", "testmo@qmul.ac.uk",
                "New Application", "TA applied", "APPLY",
                "2025-05-10 08:00:00", false));
        FileUtil.saveNotifications(notifs);

        List<Notification> loaded = FileUtil.loadNotifications();
        assertEquals("notifs size", 3, loaded.size());

        List<Notification> taNotifs = FileUtil.getNotificationsByRecipient("testta@qmul.ac.uk");
        assertEquals("ta notifications size", 2, taNotifs.size());

        int unread = FileUtil.getUnreadNotificationCount("testta@qmul.ac.uk");
        assertEquals("unread count", 1, unread);

        int unreadMo = FileUtil.getUnreadNotificationCount("testmo@qmul.ac.uk");
        assertEquals("unread count mo", 1, unreadMo);

        FileUtil.sendNotification("testta@qmul.ac.uk", "New Title", "New Content", "UPDATE");
        List<Notification> afterSend = FileUtil.getNotificationsByRecipient("testta@qmul.ac.uk");
        assertEquals("after send size", 3, afterSend.size());
        assertEquals("after send title", "New Title", afterSend.get(2).getTitle());
    }
}