# TA Recruitment System

A Java Swing-based Teaching Assistant Recruitment Management System for managing TA applications, job postings, and workload management.

## Authors

| Name          | QMID      |
| ------------- | --------- |
| BopuZhang     | 231225971 |
| Siwenrui      | 231226565 |
| XizhiZhao     | 231225993 |
| LuoLinyingxue | 221169298 |
| YinanGao      | 231226521 |
| Zhiqi Pi      | 231225960 |

## Requirements

- **Java Development Kit (JDK)**: Version 8 or higher
- **Operating System**: Windows, macOS, or Linux

## Installation

### 1. Clone or Download the Project

```bash
# If using Git
git clone https://github.com/BopuZhang/EBU6304_Group60.git

# Or download and extract the ZIP file
```

### 2. Verify Java Installation

Check if Java is installed on your system:

```bash
java -version
javac -version
```

If Java is not installed, download and install JDK from:

- [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
- [OpenJDK](https://adoptium.net/)

## Project Structure

```
EBU6304_Group60/
├── src/
│   └── system/
│       ├── admin/          # Administrator functionality
│       ├── mo/             # Module Organizer functionality
│       ├── ta/             # Teaching Assistant functionality
│       ├── ui/             # UI components (WrapLayout)
│       ├── User.java       # User model
│       ├── Job.java        # Job model
│       ├── Application.java# Application model
│       ├── Profile.java    # Profile model
│       ├── Notification.java# Notification model
│       ├── FileUtil.java   # File I/O utility
│       ├── LoggerUtil.java # Logging utility
│       ├── UIHelper.java   # UI styling utility
│       ├── LoginFrame.java # Login interface
│       ├── RegisterFrame.java# Registration interface
│       ├── DashboardFrame.java# Main dashboard
│       └── NotificationFrame.java# Notification center
├── data/                   # Data storage directory (auto-created)
├── logs/                   # Log files directory (auto-created)
├── test/                   # Unit tests
└── README.md
```

## Configuration

The application uses file-based storage and does not require external database configuration.

### Data Storage

- Data files are stored in the `data/` directory (created automatically on first run)
- Log files are stored in the `logs/` directory (created automatically)
- CV files are stored in `data/cv/` directory

### Default Admin Account

On first run, a default administrator account is created:

- **Email**: `admin@bupt.edu`
- **Password**: `admin123`

**Important**: Please change the default admin password after first login.

## Running the Application

### Method 1: Using Command Line

1. **Navigate to the project directory**:

   ```bash
   cd EBU6304_Group60
   ```

2. **Compile the source code**:

   ```bash
   # Windows
   javac -d bin -sourcepath src src/system/LoginFrame.java

   # macOS/Linux
   javac -d bin -sourcepath src src/system/LoginFrame.java
   ```

3. **Run the application**:

   ```bash
   # Windows
   java -cp bin system.LoginFrame

   # macOS/Linux
   java -cp bin system.LoginFrame
   ```

### Method 2: Using an IDE

#### IntelliJ IDEA

1. Open IntelliJ IDEA
2. Select "Open" and choose the project folder
3. Wait for the project to index
4. Navigate to `src/system/LoginFrame.java`
5. Right-click on the file and select "Run 'LoginFrame.main()'"

#### Eclipse

1. Open Eclipse
2. Select "File" > "Open Projects from File System"
3. Select the project folder
4. Navigate to `src/system/LoginFrame.java`
5. Right-click on the file and select "Run As" > "Java Application"

#### VS Code

1. Open VS Code
2. Select "File" > "Open Folder" and choose the project folder
3. Install the "Extension Pack for Java" if not already installed
4. Navigate to `src/system/LoginFrame.java`
5. Click the "Run" button above the main method

### Method 3: Using the Compile Script (Windows)

```powershell
# Compile
javac -d bin -sourcepath src src/system/*.java src/system/admin/*.java src/system/mo/*.java src/system/ta/*.java src/system/ui/*.java

# Run
java -cp bin system.LoginFrame
```

## User Roles

### Teaching Assistant (TA)

- Create and edit profile
- Upload CV
- Browse available positions
- Apply for positions
- Track application status

### Module Organizer (MO)

- Post new TA positions
- Edit and manage posted positions
- View applicants
- Accept or reject applications

### Administrator (Admin)

- View all TAs
- Manage TA workload
- View all positions
- View system logs

## Features

- Modern, responsive UI design
- Role-based access control
- Real-time input validation
- Skill matching for job recommendations
- Notification system
- Comprehensive logging
- File-based data persistence

## Troubleshooting

### Application won't start

- Ensure Java 8 or higher is installed
- Check that all source files are compiled correctly

### Data not saving

- Ensure the application has write permissions to the project directory
- Check if `data/` directory exists and is writable

### UI display issues

- Ensure your screen resolution is at least 1024x768
- Try running with different Look and Feel settings

## License

This project is developed for educational purposes as part of EBU6304 course at Queen Mary University of London.
