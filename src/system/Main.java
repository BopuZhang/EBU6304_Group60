package system;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static List<User> users;
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 加载用户数据
        users = FileUtil.loadUsers();

        // 如果没有用户，创建默认管理员
        if (users.isEmpty()) {
            User admin = new User("admin@bupt.edu", "admin123", "Admin", "系统管理员");
            users.add(admin);
            FileUtil.saveUsers(users);
            System.out.println("已创建默认管理员账号: admin@bupt.edu / admin123");
        }

        // 显示主菜单
        showMainMenu();
    }

    private static void showMainMenu() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("   国际学院助教招聘系统");
            System.out.println("========================================");
            System.out.println("1. 登录");
            System.out.println("2. 注册");
            System.out.println("3. 退出");
            System.out.print("请选择: ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    register();
                    break;
                case "3":
                    System.out.println("感谢使用，再见！");
                    return;
                default:
                    System.out.println("无效选择");
            }
        }
    }

    private static void login() {
        System.out.println("\n--- 登录 ---");
        System.out.print("邮箱: ");
        String email = scanner.nextLine();
        System.out.print("密码: ");
        String password = scanner.nextLine();

        for (User user : users) {
            if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                currentUser = user;
                System.out.println("登录成功！欢迎 " + user.getName());
                showDashboard();
                return;
            }
        }
        System.out.println("登录失败！邮箱或密码错误");
    }

    private static void register() {
        System.out.println("\n--- 注册 ---");
        System.out.print("邮箱: ");
        String email = scanner.nextLine();

        // 检查邮箱是否已存在
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                System.out.println("该邮箱已注册");
                return;
            }
        }

        System.out.print("密码: ");
        String password = scanner.nextLine();
        System.out.print("确认密码: ");
        String confirmPassword = scanner.nextLine();

        if (!password.equals(confirmPassword)) {
            System.out.println("两次密码不一致");
            return;
        }

        if (password.length() < 8) {
            System.out.println("密码长度至少8位");
            return;
        }

        System.out.print("姓名: ");
        String name = scanner.nextLine();

        System.out.println("选择角色: 1. TA  2. MO(课程负责人)");
        String roleChoice = scanner.nextLine();
        String role = roleChoice.equals("2") ? "MO" : "TA";

        User newUser = new User(email, password, role, name);
        users.add(newUser);
        FileUtil.saveUsers(users);

        System.out.println("注册成功！请登录");
    }

    private static void showDashboard() {
        while (true) {
            System.out.println("\n========================================");
            System.out.println("  欢迎, " + currentUser.getName() + " (" + currentUser.getRole() + ")");
            System.out.println("========================================");

            if (currentUser.getRole().equals("TA")) {
                System.out.println("1. 查看个人资料");
                System.out.println("2. 编辑个人资料");
                System.out.println("3. 查看可用职位");
                System.out.println("4. 我的申请状态");
                System.out.println("5. 退出登录");
                System.out.print("请选择: ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1": viewProfile(); break;
                    case "2": editProfile(); break;
                    case "3": viewJobs(); break;
                    case "4": viewApplications(); break;
                    case "5":
                        currentUser = null;
                        return;
                    default: System.out.println("无效选择");
                }
            }
            else if (currentUser.getRole().equals("MO")) {
                System.out.println("1. 发布职位");
                System.out.println("2. 查看我发布的职位");
                System.out.println("3. 查看申请人");
                System.out.println("4. 退出登录");
                System.out.print("请选择: ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1": postJob(); break;
                    case "2": viewMyJobs(); break;
                    case "3": viewApplicants(); break;
                    case "4":
                        currentUser = null;
                        return;
                    default: System.out.println("无效选择");
                }
            }
            else {  // Admin
                System.out.println("1. 查看所有TA");
                System.out.println("2. 查看TA工作量");
                System.out.println("3. 查看所有职位");
                System.out.println("4. 退出登录");
                System.out.print("请选择: ");

                String choice = scanner.nextLine();
                switch (choice) {
                    case "1": viewAllTAs(); break;
                    case "2": viewWorkload(); break;
                    case "3": viewAllJobs(); break;
                    case "4":
                        currentUser = null;
                        return;
                    default: System.out.println("无效选择");
                }
            }
        }
    }

    // TA功能
    private static void viewProfile() {
        System.out.println("\n--- 个人资料 ---");
        System.out.println("邮箱: " + currentUser.getEmail());
        System.out.println("姓名: " + currentUser.getName());
        System.out.println("角色: " + currentUser.getRole());
    }

    private static void editProfile() {
        System.out.println("\n--- 编辑资料 ---");
        System.out.print("新姓名 (留空不变): ");
        String newName = scanner.nextLine();
        if (!newName.isEmpty()) {
            currentUser.setName(newName);
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getEmail().equals(currentUser.getEmail())) {
                    users.set(i, currentUser);
                    break;
                }
            }
            FileUtil.saveUsers(users);
            System.out.println("更新成功！");
        }
    }

    private static void viewJobs() {
        System.out.println("\n--- 可用职位 ---");
        System.out.println("迭代2实现");
    }

    private static void viewApplications() {
        System.out.println("\n--- 我的申请 ---");
        System.out.println("迭代2实现");
    }

    // MO功能
    private static void postJob() {
        System.out.println("\n--- 发布职位 ---");
        System.out.println("迭代2实现");
    }

    private static void viewMyJobs() {
        System.out.println("\n--- 我发布的职位 ---");
        System.out.println("迭代2实现");
    }

    private static void viewApplicants() {
        System.out.println("\n--- 申请人列表 ---");
        System.out.println("迭代2实现");
    }

    // Admin功能
    private static void viewAllTAs() {
        System.out.println("\n--- 所有TA ---");
        for (User user : users) {
            if (user.getRole().equals("TA")) {
                System.out.println("- " + user.getName() + " (" + user.getEmail() + ")");
            }
        }
    }

    private static void viewWorkload() {
        System.out.println("\n--- TA工作量 ---");
        System.out.println("迭代2实现");
    }

    private static void viewAllJobs() {
        System.out.println("\n--- 所有职位 ---");
        System.out.println("迭代2实现");
    }
}