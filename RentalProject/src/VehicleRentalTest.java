import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.time.LocalDate;

public class VehicleRentalTest {

    // Shared instance of RentalSystem for tests.
    private RentalSystem rentalSystem;

    @BeforeEach
    public void setUp() {
        // Get the singleton RentalSystem instance.
        rentalSystem = RentalSystem.getInstance();
        // If needed, clear previous state here so tests run cleanly.
    }
    
    /**
     * Test License Plate Validation in the Vehicle class.
     */
    @Test
    public void testLicensePlateValidation() {
        // Test valid license plate scenarios.
        Car car1 = new Car("Toyota", "Corolla", 2019, 4);
        assertDoesNotThrow(() -> car1.setLicensePlate("AAA100"));
        assertEquals("AAA100", car1.getLicensePlate());
        
        Car car2 = new Car("Honda", "Civic", 2020, 4);
        // Test invalid license plates.
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate(""));
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate(null));
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate("AAA1000")); // Too many characters.
        assertThrows(IllegalArgumentException.class, () -> car2.setLicensePlate("ZZZ99"));   // Too few characters.
        
        // Additional valid tests.
        Car car3 = new Car("Ford", "Focus", 2021, 4);
        assertDoesNotThrow(() -> car3.setLicensePlate("ABC567"));
        assertEquals("ABC567", car3.getLicensePlate());
        
        Car car4 = new Car("Chevy", "Impala", 2020, 4);
        assertDoesNotThrow(() -> car4.setLicensePlate("ZZZ999"));
        assertEquals("ZZZ999", car4.getLicensePlate());
    }
    
    /**
     * Test Rent/Return Vehicle Validation.
     */
    @Test
    public void testRentAndReturnVehicle() {
        // Create a test vehicle and customer.
        Car testCar = new Car("Toyota", "Camry", 2021, 4);
        testCar.setLicensePlate("BBB222");
        Customer testCustomer = new Customer(1, "John Doe");
        
        // Ensure vehicle is initially available.
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, testCar.getStatus(), "Vehicle should be AVAILABLE initially");
        
        // Add the vehicle and customer to the system.
        rentalSystem.addVehicle(testCar);
        rentalSystem.addCustomer(testCustomer);
        
        // Rent the vehicle and verify.
        boolean rentSuccess = rentalSystem.rentVehicle(testCar, testCustomer, LocalDate.now(), 150.0);
        assertTrue(rentSuccess, "Renting should succeed");
        assertEquals(Vehicle.VehicleStatus.RENTED, testCar.getStatus(), "Vehicle status should be RENTED after renting");
        
        // Attempt to rent the already rented vehicle.
        boolean rentAgain = rentalSystem.rentVehicle(testCar, testCustomer, LocalDate.now(), 150.0);
        assertFalse(rentAgain, "Renting an already rented vehicle should fail");
        
        // Return the vehicle and verify.
        boolean returnSuccess = rentalSystem.returnVehicle(testCar, testCustomer, LocalDate.now(), 10.0);
        assertTrue(returnSuccess, "Returning should succeed");
        assertEquals(Vehicle.VehicleStatus.AVAILABLE, testCar.getStatus(), "Vehicle status should be AVAILABLE after return");
        
        // Attempt to return an already available vehicle.
        boolean returnAgain = rentalSystem.returnVehicle(testCar, testCustomer, LocalDate.now(), 10.0);
        assertFalse(returnAgain, "Returning an already available vehicle should fail");
    }
    
    /**
     * Test Singleton Validation for RentalSystem.
     */
    @Test
    public void testSingletonRentalSystem() throws Exception {
        // Use reflection to get the declared constructor.
        Constructor<RentalSystem> constructor = RentalSystem.class.getDeclaredConstructor();
        // Verify that the constructor is private.
        assertEquals(Modifier.PRIVATE, constructor.getModifiers(), "RentalSystem constructor should be private");
        
        // Verify that getInstance() returns a non-null instance.
        RentalSystem instance = RentalSystem.getInstance();
        assertNotNull(instance, "RentalSystem.getInstance() should not return null");
    }
}
