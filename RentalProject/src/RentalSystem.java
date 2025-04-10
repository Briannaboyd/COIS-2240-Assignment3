import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class RentalSystem {
    // Singleton instance.
    private static RentalSystem instance;
    
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();

    // Private constructor for Singleton pattern.
    private RentalSystem() {
        loadData(); // Load previously saved data.
    }
    
    // Public method to get the singleton instance.
    public static RentalSystem getInstance() {
        if (instance == null) {
            instance = new RentalSystem();
        }
        return instance;
    }

    // Modified addVehicle method: checks for duplicate and returns boolean.
    public boolean addVehicle(Vehicle vehicle) {
        if (findVehicleByPlate(vehicle.getLicensePlate()) != null) {
            System.out.println("Vehicle with plate " + vehicle.getLicensePlate() + " already exists.");
            return false;
        }
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        return true;
    }

    // Modified addCustomer method: checks for duplicate and returns boolean.
    public boolean addCustomer(Customer customer) {
        if (findCustomerById(Integer.toString(customer.getCustomerId())) != null) {
            System.out.println("Customer with ID " + customer.getCustomerId() + " already exists.");
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    // Modified rentVehicle method: returns true if rental is successful.
    public boolean rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            RentalRecord record = new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not available for renting.");
            return false;
        }
    }

    // Modified returnVehicle method: returns true if returning is successful.
    public boolean returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            RentalRecord record = new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        } else {
            System.out.println("Vehicle is not rented.");
            return false;
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

    // --- File-based storage methods (example implementations) ---
    private void saveVehicle(Vehicle vehicle) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("vehicles.txt", true)))) {
            // Format: licensePlate,make,model,year,status
            out.println(vehicle.getLicensePlate() + "," + vehicle.getMake() + "," +
                        vehicle.getModel() + "," + vehicle.getYear() + "," + vehicle.getStatus());
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }

    private void saveCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("customers.txt", true)))) {
            // Format: customerId,name
            out.println(customer.getCustomerId() + "," + customer.getCustomerName());
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("rental_records.txt", true)))) {
            // Save record using toString() for simplicity (adjust as needed).
            out.println(record.toString());
        } catch (IOException e) {
            System.out.println("Error saving rental record: " + e.getMessage());
        }
    }

    // --- Data Loading method ---
    private void loadData() {
        // The loadData method should read the stored files and repopulate vehicles, customers,
        // and the rentalHistory. For simplicity, here is a basic implementation for vehicles and customers.
        
        // Load vehicles
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
                    // For demonstration, create a simple Car (assume 4 seats).  
                    Car vehicle = new Car(make, model, year, 4);
                    vehicle.setLicensePlate(lp);
                    vehicle.setStatus(status);
                    vehicles.add(vehicle);
                }
            }
        } catch (FileNotFoundException e) {
            // File may not exist on first run. This is acceptable.
        } catch (IOException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
        
        // Load customers
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
            // Acceptable if file does not exist yet.
        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
        
        // Load rental records (optional and simplified)
        try (BufferedReader br = new BufferedReader(new FileReader("rental_records.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
            }
        } catch (FileNotFoundException e) {
            // Acceptable if file does not exist.
        } catch (IOException e) {
            System.out.println("Error loading rental records: " + e.getMessage());
        }
    }
}
