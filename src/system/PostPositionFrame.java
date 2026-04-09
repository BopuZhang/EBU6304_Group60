package system;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

// 发布职位界面，对应MO-01
public class PostPositionFrame extends JFrame {
    private User currentUser;

    // 输入组件
    private JTextField moduleCodeField;
    private JTextField moduleNameField;
    private JTextArea descArea;
    private JTextField weeklyHoursField;
    private JTextField deadlineField;
    private JTextField requirementsField;
    private JTextField applicantLimitField;

    public PostPositionFrame(User user) {
        this.currentUser = user;
        initUI();
    }

    private void initUI() {
        setTitle("Post Position");
        setSize(550, 580);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // 表单面板
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Module Code
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Module Code:"), gbc);
        gbc.gridx = 1;
        moduleCodeField = new JTextField(20);
        panel.add(moduleCodeField, gbc);

        // Module Name
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Module Name:"), gbc);
        gbc.gridx = 1;
        moduleNameField = new JTextField(20);
        panel.add(moduleNameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        descArea = new JTextArea(4, 20);
        descArea.setLineWrap(true);
        panel.add(new JScrollPane(descArea), gbc);

        // Weekly Hours
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Weekly Hours:"), gbc);
        gbc.gridx = 1;
        weeklyHoursField = new JTextField(20);
        panel.add(weeklyHoursField, gbc);

        // Deadline
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Deadline (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        deadlineField = new JTextField(20);
        panel.add(deadlineField, gbc);

        // Requirements
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(new JLabel("Requirements:"), gbc);
        gbc.gridx = 1;
        requirementsField = new JTextField(20);
        panel.add(requirementsField, gbc);

        // Applicant Limit
        gbc.gridx = 0; gbc.gridy = 6;
        panel.add(new JLabel("Applicant Limit:"), gbc);
        gbc.gridx = 1;
        applicantLimitField = new JTextField(20);
        panel.add(applicantLimitField, gbc);

        // Submit Button
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton submitBtn = new JButton("Post");
        submitBtn.setPreferredSize(new Dimension(120, 35));
        submitBtn.addActionListener(this::doPost);
        panel.add(submitBtn, gbc);

        add(panel, BorderLayout.CENTER);
    }

    // 执行发布逻辑
    private void doPost(ActionEvent e) {
        try {
            // 获取输入
            String moduleCode = moduleCodeField.getText().trim();
            String moduleName = moduleNameField.getText().trim();
            String description = descArea.getText().trim();
            String weeklyHoursStr = weeklyHoursField.getText().trim();
            String deadline = deadlineField.getText().trim();
            String requirements = requirementsField.getText().trim();
            String limitStr = applicantLimitField.getText().trim();

            // 非空校验
            if (moduleCode.isEmpty() || moduleName.isEmpty() || description.isEmpty()
                    || weeklyHoursStr.isEmpty() || deadline.isEmpty() || requirements.isEmpty() || limitStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!");
                return;
            }

            // 数字校验
            int weeklyHours = Integer.parseInt(weeklyHoursStr);
            int applicantLimit = Integer.parseInt(limitStr);

            // 生成唯一Job ID
            String jobId = "JOB-" + System.currentTimeMillis();

            // 创建职位对象
            Job job = new Job(
                    jobId,
                    currentUser.getEmail(),
                    moduleCode,
                    moduleName,
                    description,
                    weeklyHours,
                    deadline,
                    requirements,
                    "OPEN",
                    applicantLimit
            );

            // 保存到文件
            List<Job> jobs = FileUtil.loadJobs();
            jobs.add(job);
            FileUtil.saveJobs(jobs);

            // 日志记录
            LoggerUtil.logCreate("Job", jobId + " | " + moduleCode);
            JOptionPane.showMessageDialog(this, "Position posted successfully!");
            dispose();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hours and limit must be numbers!");
        } catch (Exception ex) {
            LoggerUtil.logError("PostJob", ex.getMessage());
            JOptionPane.showMessageDialog(this, "Post failed: " + ex.getMessage());
        }
    }
}
