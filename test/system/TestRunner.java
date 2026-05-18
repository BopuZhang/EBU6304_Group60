package system;

public class TestRunner {
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("       EBU6304 Group60 — Test Suite");
        System.out.println("===============================================\n");

        try {
            System.out.println("▶ Running Model Classes Tests...\n");
            TestModelClasses.main(args);
        } catch (Exception e) {
            System.out.println("  Model test FAILED: " + e.getMessage());
        }

        System.out.println("\n-----------------------------------------------\n");

        try {
            System.out.println("▶ Running FileUtil Tests...\n");
            TestFileUtil.main(args);
        } catch (Exception e) {
            System.out.println("  FileUtil test FAILED: " + e.getMessage());
        }

        System.out.println("\n-----------------------------------------------\n");

        try {
            System.out.println("▶ Running LoggerUtil Tests...\n");
            TestLoggerUtil.main(args);
        } catch (Exception e) {
            System.out.println("  LoggerUtil test FAILED: " + e.getMessage());
        }

        System.out.println("\n===============================================");
        System.out.println("       All test suites completed.");
        System.out.println("===============================================");
    }
}