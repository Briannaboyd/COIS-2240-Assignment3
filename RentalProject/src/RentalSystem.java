import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalSystem {
    // Singleton instance
    private static RentalSystem instance;
    
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    // Private constructor for singleton pattern.
    private RentalSystem() {
        // Load any previously saved data on startup.
        loadData();
    }
    
    // Public method to get the single instance.
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    // Modified addVehicle: checks duplicates and returns boolean.
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    // Modified addCustomer: checks duplicates and returns boolean.
    public boolean addCustomer(Customer customer) {
        if (findCustomerById(Integer.toString(customer.getCustomerId())) != null) {
            System.out.println("Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    // Modified rentVehicle: now calls saveRecord after adding record.
    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not available for renting.");
        }
    }

    // Modified returnVehicle: now calls saveRecord after adding record.
    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
        } else {
            System.out.println("Vehicle is not rented.");
        }
    }

    public void displayVehicles(boolean onlyAvailable) {
        System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
        System.out.println("---------------------------------------------------------------------------------");
        for (Vehicle v : vehicles) {
            if (!onlyAvailable || v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                String type = (v instanceof Car) ? "Car" : (v instanceof Motorcycle) ? "Motorcycle" :
                              (v instanceof Truck) ? "Truck" : "Vehicle";
                System.out.println("|     " + type + "\t|\t" + v.getLicensePlate() + "\t|\t"
                        + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|");
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }

    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }

    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }

    public Customer findCustomerById(String id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == Integer.parseInt(id))
                return c;
        }
        return null;
    }

    // --- File-based storage methods (subtask 2) ---
    private void saveVehicle(Vehicle vehicle) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("vehicles.txt", true)))) {
            // Save as: licensePlate,make,model,year,status
            out.println(vehicle.getLicensePlate() + "," + vehicle.getMake() + "," +
                        vehicle.getModel() + "," + vehicle.getYear() + "," + vehicle.getStatus());
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("customers.txt", true)))) {
            // Save as: customerId,name
            out.println(customer.getCustomerId() + "," + customer.getCustomerName());
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("rental_records.txt", true)))) {
            // Save record using a simple format. Adjust parsing in loadData() as needed.
            out.println(record.toString());
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    // --- Data Loading method (subtask 3) ---
    // Note: This is a simple example; you may need to refine parsing logic.
    private void loadData() {
        // Load vehicles from vehicles.txt
        try (BufferedReader br = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Expected format: licensePlate,make,model,year,status
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    String lp = parts[0];
                    String make = parts[1];
                    String model = parts[2];
                    int year = Integer.parseInt(parts[3]);
                    Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(parts[4]);
                    // For demonstration, create a default Car with 4 seats (you might want to handle types if stored).
                    Car vehicle = new Car(make, model, year, 4);
                    vehicle.setLicensePlate(lp);
                    vehicle.setStatus(status);
                    vehicles.add(vehicle);
                }
            }
        } catch (FileNotFoundException e) {
            // File may not exist on first run; this is okay.
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }

        // Load customers from customers.txt
        try (BufferedReader br = new BufferedReader(new FileReader("customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                // Expected format: customerId,name
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    Customer customer = new Customer(id, name);
                    customers.add(customer);
                }
            }
        } catch (FileNotFoundException e) {
            // Ignore if file doesn't exist.
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }

        // Load rental records from rental_records.txt
        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
            }
        } catch (FileNotFoundException e) {
            // Ignore if file doesn't exist.
        } catch (IOException e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }
}
