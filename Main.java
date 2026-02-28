package com.vehiclerental;

import java.sql.*;
import java.util.Scanner;

public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            System.out.println("\n===== Vehicle Rental System =====");
            System.out.println("1. Add Vehicle");
            System.out.println("2. View Vehicles");
            System.out.println("3. Rent Vehicle");
            System.out.println("4. Return Vehicle");
            System.out.println("5. Delete Vehicle");
            System.out.println("6. Add Customer");
            System.out.println("7. View Customers");
            System.out.println("8. View Rentals");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                sc.next();   
                continue;   
            }

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    addVehicle();
                    break;
                case 2:
                    viewVehicles();
                    break;
                case 3:
                    rentVehicle();
                    break;
                case 4:
                    returnVehicle();
                    break;
                case 5:
                    deleteVehicle();
                    break;
                case 6:
                    addCustomer();
                    break;
                case 7:
                    viewCustomers();
                    break;
                case 8:
                	viewRentals();
                	break;
                case 9:
                    System.out.println("Thank you!");
                    System.exit(0);
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    // ================= VEHICLE METHODS =================

    public static void addVehicle() {

        String query =
            "INSERT INTO vehicles (name, type, rent_per_day, quantity) VALUES (?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            sc.nextLine(); // clear buffer

            System.out.print("Enter vehicle name: ");
            String name = sc.nextLine();

            System.out.print("Enter vehicle type: ");
            String type = sc.nextLine();

            // ✅ Read rent properly
            System.out.print("Enter rent per day: ");
            while (!sc.hasNextDouble()) {
                System.out.println("Invalid input! Please enter a valid number.");
                sc.next();
            }
            double rent = sc.nextDouble();

            // ✅ Read quantity properly
            System.out.print("Enter quantity: ");
            while (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a valid number.");
                sc.next();
            }
            int quantity = sc.nextInt();

            ps.setString(1, name);
            ps.setString(2, type);
            ps.setDouble(3, rent);
            ps.setInt(4, quantity);

            ps.executeUpdate();

            System.out.println("Vehicle added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void viewVehicles() {

        String query = "SELECT * FROM vehicles";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n---- Vehicle List ----");

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Type: " + rs.getString("type"));
                System.out.println("Rent per Day: " + rs.getDouble("rent_per_day"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("----------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }public static void rentVehicle() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter vehicle ID: ");
            int vehicleId = sc.nextInt();

            System.out.print("Enter customer ID: ");
            int customerId = sc.nextInt();

            System.out.print("Enter number of days: ");
            int days = sc.nextInt();

            // 🔎 Check vehicle and quantity
            String vehicleQuery = "SELECT * FROM vehicles WHERE id = ? AND quantity > 0";
            PreparedStatement ps1 = con.prepareStatement(vehicleQuery);
            ps1.setInt(1, vehicleId);
            ResultSet vehicleRs = ps1.executeQuery();

            if (!vehicleRs.next()) {
                System.out.println("Vehicle not available or not found!");
                return;
            }

            String vehicleName = vehicleRs.getString("name");
            String vehicleType = vehicleRs.getString("type");
            double rentPerDay = vehicleRs.getDouble("rent_per_day");
            int currentQuantity = vehicleRs.getInt("quantity");

            // 🔎 Check customer
            String customerQuery = "SELECT * FROM customers WHERE id = ?";
            PreparedStatement ps2 = con.prepareStatement(customerQuery);
            ps2.setInt(1, customerId);
            ResultSet customerRs = ps2.executeQuery();

            if (!customerRs.next()) {
                System.out.println("Customer not found!");
                return;
            }

            String customerName = customerRs.getString("name");
            String customerPhone = customerRs.getString("phone");

            double totalAmount = rentPerDay * days;

            // ✅ Insert rental record
            String insertRental = "INSERT INTO rentals (vehicle_id, customer_id, days, total_amount) VALUES (?, ?, ?, ?)";
            PreparedStatement ps3 = con.prepareStatement(insertRental);
            ps3.setInt(1, vehicleId);
            ps3.setInt(2, customerId);
            ps3.setInt(3, days);
            ps3.setDouble(4, totalAmount);
            ps3.executeUpdate();

            // 🔥 Decrease quantity by 1
            String updateVehicle = "UPDATE vehicles SET quantity = quantity - 1 WHERE id = ?";
            PreparedStatement ps4 = con.prepareStatement(updateVehicle);
            ps4.setInt(1, vehicleId);
            ps4.executeUpdate();

            // 🧾 Print receipt
            System.out.println("\n========== RENTAL RECEIPT ==========");
            System.out.println("Customer ID   : " + customerId);
            System.out.println("Customer Name : " + customerName);
            System.out.println("Phone         : " + customerPhone);
            System.out.println("-------------------------------------");
            System.out.println("Vehicle ID    : " + vehicleId);
            System.out.println("Vehicle Name  : " + vehicleName);
            System.out.println("Vehicle Type  : " + vehicleType);
            System.out.println("-------------------------------------");
            System.out.println("Days          : " + days);
            System.out.println("Rent Per Day  : " + rentPerDay);
            System.out.println("Total Amount  : " + totalAmount);
            System.out.println("-------------------------------------");
            System.out.println("Remaining Quantity: " + (currentQuantity - 1));
            System.out.println("=====================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void deleteVehicle() {

        System.out.print("Enter Vehicle ID to delete: ");
        int id = sc.nextInt();

        String query = "DELETE FROM vehicles WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Vehicle deleted successfully!");
            } else {
                System.out.println("Vehicle not found!");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void returnVehicle() {

        try (Connection con = DBConnection.getConnection()) {

            System.out.print("Enter vehicle ID to return: ");
            int vehicleId = sc.nextInt();

            // 🔎 Check if rental exists
            String checkRental = "SELECT * FROM rentals WHERE vehicle_id = ?";
            PreparedStatement ps1 = con.prepareStatement(checkRental);
            ps1.setInt(1, vehicleId);
            ResultSet rs = ps1.executeQuery();

            if (!rs.next()) {
                System.out.println("No active rental found for this vehicle!");
                return;
            }

            // ✅ Delete rental record
            String deleteRental = "DELETE FROM rentals WHERE vehicle_id = ?";
            PreparedStatement ps2 = con.prepareStatement(deleteRental);
            ps2.setInt(1, vehicleId);
            ps2.executeUpdate();

            // 🔥 Increase quantity by 1
            String updateVehicle = "UPDATE vehicles SET quantity = quantity + 1 WHERE id = ?";
            PreparedStatement ps3 = con.prepareStatement(updateVehicle);
            ps3.setInt(1, vehicleId);
            ps3.executeUpdate();

            System.out.println("Vehicle returned successfully!");
            System.out.println("Quantity increased by 1.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void viewRentals() {

        String query = """
            SELECT r.id,
                   c.name AS customer_name,
                   c.phone,
                   v.name AS vehicle_name,
                   v.type,
                   r.days,
                   r.total_amount
            FROM rentals r
            LEFT JOIN customers c ON r.customer_id = c.id
            LEFT JOIN vehicles v ON r.vehicle_id = v.id
            """;

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n---- Rental Records ----");

            boolean hasData = false;

            while (rs.next()) {
                hasData = true;

                System.out.println("Rental ID      : " + rs.getInt("id"));
                System.out.println("Customer Name  : " + rs.getString("customer_name"));
                System.out.println("Phone          : " + rs.getString("phone"));
                System.out.println("Vehicle Name   : " + rs.getString("vehicle_name"));
                System.out.println("Vehicle Type   : " + rs.getString("type"));
                System.out.println("Days           : " + rs.getInt("days"));
                System.out.println("Total Amount   : " + rs.getDouble("total_amount"));
                System.out.println("-----------------------------------");
            }

            if (!hasData) {
                System.out.println("No rental records found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    // ================= CUSTOMER METHODS =================

    public static void addCustomer() {

        String query = "INSERT INTO customers (name, phone) VALUES (?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(query)) {

            System.out.print("Enter customer name: ");
            sc.nextLine();
            String name = sc.nextLine();

            System.out.print("Enter phone number: ");
            String phone = sc.next();

            ps.setString(1, name);
            ps.setString(2, phone);

            ps.executeUpdate();

            System.out.println("Customer added successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void viewCustomers() {

        String query = "SELECT * FROM customers";

        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {

            System.out.println("\n---- Customer List ----");

            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Phone: " + rs.getString("phone"));
                System.out.println("----------------------");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}