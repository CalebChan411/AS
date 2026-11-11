/** Creates drivers through the original constructor and public setter. */
public class DriverFactory {
    public Driver createDriver(String driverID, String driverName) {
        Driver driver = new Driver(driverID);
        driver.setName(driverName);
        return driver;
    }
}
