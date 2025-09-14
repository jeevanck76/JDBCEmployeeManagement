import java.sql.*;
import java.util.Scanner;

public class SQLServerConnection {
    static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=company;integratedSecurity=true;encrypt=true;trustServerCertificate=true";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(URL);
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to SQL Server");

            while (true) {
                System.out.println("\n1. Add Employee\n2. View Employees\n3. Update Employee\n4. Delete Employee\n5. View An Employee\n6. Get 3rd Highest Salary\n7. SQL Injection\n8. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1 -> addEmployee(conn, scanner);
                    case 2 -> viewEmployees(conn);
                    case 3 -> updateEmployee(conn, scanner);
                    case 4 -> deleteEmployee(conn, scanner);
                    case 5 -> viewSpecificEmployee(conn, scanner);
                    case 6 -> getThirdHighestSalary(conn);
                    case 7 -> sqlInjection(conn, scanner);
                    case 8 -> {
                        System.out.println("Exiting");
                        return;
                    }
                    default -> System.out.println("Invalid choice!");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    static void addEmployee(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Name: ");
        String name = scanner.nextLine();
        System.out.print("Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Department: ");
        String dept = scanner.nextLine();
        System.out.print("Salary: ");
        double salary = scanner.nextDouble();

        String sql = "INSERT INTO employee_details (name, age, department, salary) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, dept);
            ps.setDouble(4, salary);
            ps.executeUpdate();
            System.out.println("Employee added!");
        }
    }

    static void viewEmployees(Connection conn) throws SQLException {
        String sql = "SELECT * FROM employee_details";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\nEmployee List:");
            while (rs.next()) {
                System.out.printf("%d | %s | %d | %s | %.2f%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("department"),
                        rs.getDouble("salary"));
            }
        }
    }

    static void updateEmployee(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Employee ID to update: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("New Name: ");
        String name = scanner.nextLine();
        System.out.print("New Age: ");
        int age = scanner.nextInt();
        scanner.nextLine();
        System.out.print("New Department: ");
        String dept = scanner.nextLine();
        System.out.print("New Salary: ");
        double salary = scanner.nextDouble();

        String sql = "UPDATE employee_details SET name = ?, age = ?, department = ?, salary = ? WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setInt(2, age);
            ps.setString(3, dept);
            ps.setDouble(4, salary);
            ps.setInt(5, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Employee updated!" : "Employee not found.");
        }
    }

    static void deleteEmployee(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Employee ID to delete: ");
        int id = scanner.nextInt();

        String sql = "DELETE FROM employee_details WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            System.out.println(rows > 0 ? "Employee deleted!" : "Employee not found.");
        }
    }

    static void viewSpecificEmployee(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Employee ID to view: ");
        int id = scanner.nextInt();

        String sql = "SELECT * FROM employee_details WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("\nEmployee Details:");
                    System.out.printf("ID: %d\nName: %s\nAge: %d\nDepartment: %s\nSalary: %.2f\n",
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("department"),
                            rs.getDouble("salary"));
                } else {
                    System.out.println("Employee not found.");
                }
            }
        }
    }

    static void getThirdHighestSalary(Connection conn) throws SQLException {
        String sql = "SELECT DISTINCT salary FROM employee_details ORDER BY salary DESC OFFSET 2 ROWS FETCH NEXT 1 ROWS ONLY";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                System.out.printf("3rd Highest Salary: %.2f%n", rs.getDouble("salary"));
            } else {
                System.out.println("Not enough data to determine 3rd highest salary.");
            }
        }
    }

    static void sqlInjection(Connection conn, Scanner scanner) throws SQLException {
        System.out.print("Enter Employee Name (unsafe input): ");
        String name = scanner.nextLine();
        String sql = "SELECT * FROM employee_details WHERE name = '" + name + "'";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            System.out.println("\nResults:");
            while (rs.next()) {
                System.out.printf("ID: %d | Name: %s | Age: %d | Dept: %s | Salary: %.2f%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("department"),
                        rs.getDouble("salary"));
            }
        }
    }
}
