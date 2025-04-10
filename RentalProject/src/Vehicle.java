public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { AVAILABLE, RESERVED, RENTED, MAINTENANCE, OUTOFSERVICE }

    public Vehicle(String make, String model, int year) {
        // Use helper method to format make and model.
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.AVAILABLE;
        this.licensePlate = null;
    }

    public Vehicle() {
        this(null, null, 0);
    }

    // Helper method for formatting strings.
    private String capitalize(String input) {
        if (input == null || input.isEmpty())
            return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    // Private method to validate the license plate.
    // Returns true only if the plate is not null, not empty,
    // and follows the format of exactly three letters followed by three digits.
    private boolean isValidPlate(String plate) {
        if (plate == null || plate.trim().isEmpty()) {
            return false;
        }
        // Convert to uppercase for consistent checking.
        String formattedPlate = plate.toUpperCase();
        // Validate that it is exactly three letters and three digits.
        return formattedPlate.matches("^[A-Z]{3}[0-9]{3}$");
    }

    // Updated setLicensePlate method: validates the plate and throws exception if invalid.
    public void setLicensePlate(String plate) {
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Invalid license plate. Must be three letters followed by three numbers.");
        }
        this.licensePlate = plate.toUpperCase();
    }

    public String getLicensePlate() { 
        return licensePlate; 
    }

    public String getMake() { 
        return make; 
    }

    public String getModel() { 
        return model;
    }

    public int getYear() { 
        return year; 
    }

    public VehicleStatus getStatus() { 
        return status; 
    }

    public void setStatus(VehicleStatus status) { 
        this.status = status; 
    }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }
}
