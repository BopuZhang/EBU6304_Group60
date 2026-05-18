package system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestLoggerUtil {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("========== LoggerUtil Tests ==========\n");
        testLogMethods();
        testLogInfo();
        testLogError();
        testLogLoginLogout();
        testLogCreateUpdateDelete();
        System.out.println("\n========== Results: " + passed + " passed, " + failed + " failed ==========");
        if (failed > 0)
            System.exit(1);
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

    private static String readLastLogLine() {
        File logFile = new File("logs/app.log");
        if (!logFile.exists())
            return "";
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String last = "", line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty())
                    last = line;
            }
            return last;
        } catch (IOException e) {
            return "";
        }
    }

    private static int countLogLines() {
        File logFile = new File("logs/app.log");
        if (!logFile.exists())
            return 0;
        int count = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            while (reader.readLine() != null)
                count++;
        } catch (IOException e) {
            return 0;
        }
        return count;
    }

    private static void testLogMethods() {
        System.out.println("--- Basic Logging ---");

        int beforeCount = countLogLines();

        LoggerUtil.logInfo("Test info message");
        String last = readLastLogLine();
        assertTrue("logInfo contains INFO", last.contains("[INFO]"));
        assertTrue("logInfo contains message", last.contains("Test info message"));

        int afterCount = countLogLines();
        assertTrue("log lines increased", afterCount > beforeCount);
    }

    private static void testLogInfo() {
        System.out.println("--- logInfo ---");

        LoggerUtil.logInfo("Another info");
        String last = readLastLogLine();
        assertTrue("logInfo works", last.contains("[INFO]"));
        assertTrue("logInfo message correct", last.contains("Another info"));
    }

    private static void testLogError() {
        System.out.println("--- logError ---");

        LoggerUtil.logError("TestError", "Something went wrong");
        String last = readLastLogLine();
        assertTrue("logError contains ERROR", last.contains("[ERROR]"));
        assertTrue("logError contains type", last.contains("TestError"));
        assertTrue("logError contains message", last.contains("Something went wrong"));
    }

    private static void testLogLoginLogout() {
        System.out.println("--- logLogin / logLogout / logRegistration ---");

        LoggerUtil.logLogin("ta01@qmul.ac.uk", "TA");
        String last = readLastLogLine();
        assertTrue("logLogin contains LOGIN", last.contains("[LOGIN]"));
        assertTrue("logLogin contains email", last.contains("ta01@qmul.ac.uk"));
        assertTrue("logLogin contains role", last.contains("TA"));

        LoggerUtil.logLogout("mo01@qmul.ac.uk", "MO");
        last = readLastLogLine();
        assertTrue("logLogout contains LOGOUT", last.contains("[LOGOUT]"));
        assertTrue("logLogout contains email", last.contains("mo01@qmul.ac.uk"));

        LoggerUtil.logRegistration("newuser@qmul.ac.uk", "TA");
        last = readLastLogLine();
        assertTrue("logRegistration contains REGISTER", last.contains("[REGISTER]"));
        assertTrue("logRegistration contains email", last.contains("newuser@qmul.ac.uk"));
    }

    private static void testLogCreateUpdateDelete() {
        System.out.println("--- logCreate / logUpdate / logDelete ---");

        LoggerUtil.logCreate("Profile", "ta01@qmul.ac.uk profile created");
        String last = readLastLogLine();
        assertTrue("logCreate contains CREATE", last.contains("[CREATE]"));
        assertTrue("logCreate contains entity", last.contains("Profile"));

        LoggerUtil.logUpdate("Job", "J001 status changed to CLOSED");
        last = readLastLogLine();
        assertTrue("logUpdate contains UPDATE", last.contains("[UPDATE]"));
        assertTrue("logUpdate contains entity", last.contains("Job"));

        LoggerUtil.logDelete("Application", "A001 deleted");
        last = readLastLogLine();
        assertTrue("logDelete contains DELETE", last.contains("[DELETE]"));
        assertTrue("logDelete contains entity", last.contains("Application"));
    }
}