package com.example.carins;

import com.example.carins.model.InsurancePolicy;
import com.example.carins.repo.CarRepository;
import com.example.carins.repo.InsurancePolicyRepository;
import com.example.carins.service.CarService;
import com.example.carins.service.InsurancePolicyService;
import com.example.carins.web.CarController;
import com.example.carins.web.InsurancePolicyController;
import com.example.carins.web.dto.CarDto;
import com.example.carins.web.dto.InsuranceDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.example.carins.model.Car;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarInsuranceApplicationTests {

    @Autowired
    CarService service;

    @Autowired
    CarRepository carRepo;

    @Autowired
    InsurancePolicyRepository insuranceRepo;

    @Autowired
    InsurancePolicyService policyService;

    @Autowired
    InsurancePolicyController policyController;


    @Test
    void insuranceValidityBasic() {
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2024-06-01")));
        assertTrue(service.isInsuranceValid(1L, LocalDate.parse("2025-06-01")));
        assertFalse(service.isInsuranceValid(2L, LocalDate.parse("2025-02-01")));
    }

//public Car(String vin, String make, String model, int yearOfManufacture, Owner owner)
    //Teste C)
    @Test
    void testGetCars() {
        // Arrange
        CarService fakeService = new CarService(carRepo,insuranceRepo) {
            @Override
            public List<Car> listCars() {
                return List.of(new Car("VIN123", "Toyota", "Corolla", 2020, null));
            }

            @Override
            public Car getCarById(Long id) { return null; }
            @Override
            public boolean isInsuranceValid(Long carId, LocalDate date) { return false; }
        };
        CarController controller = new CarController(fakeService);

        // Act
        List<CarDto> cars = controller.getCars();

        // Assert
        assertEquals(1, cars.size());
        assertEquals("VIN123", cars.get(0).vin());
    }

    @Test
    void testIsInsuranceValid_carExists_validDate() {
        CarService fakeService = new CarService(carRepo,insuranceRepo) {
            @Override
            public Car getCarById(Long id) { return new Car( "VIN123", "Toyota", "Corolla", 2020, null); }
            @Override
            public boolean isInsuranceValid(Long carId, LocalDate date) { return true; }
            @Override
            public List<Car> listCars() { return List.of(); }
        };

        CarController controller = new CarController(fakeService);

        var response = controller.isInsuranceValid(1L, "2024-06-01");

        assertEquals(200, response.getStatusCodeValue());
        assertTrue(response.getBody() instanceof CarController.InsuranceValidityResponse);
        var body = (CarController.InsuranceValidityResponse) response.getBody();
        assertEquals(1L, body.carId());
        assertEquals("2024-06-01", body.date());
        assertTrue(body.valid());
    }

    @Test
    void testIsInsuranceValid_invalidDate() {
        CarService fakeService = new CarService(carRepo,insuranceRepo) {
            @Override
            public Car getCarById(Long id) { return new Car( "VIN123", "Toyota", "Corolla", 2020, null); }
            @Override
            public boolean isInsuranceValid(Long carId, LocalDate date) { return true; }
            @Override
            public List<Car> listCars() { return List.of(); }
        };

        CarController controller = new CarController(fakeService);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            controller.isInsuranceValid(1L, "2024-13-01"); // luna invalidă
        });

        assertEquals(400, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Invalid date format"));
    }

    @Test
    void testIsInsuranceValid_carNotFound() {
        CarService fakeService = new CarService(carRepo,insuranceRepo) {
            @Override
            public Car getCarById(Long id) { return null; } // masina nu exista
            @Override
            public boolean isInsuranceValid(Long carId, LocalDate date) { return false; }
            @Override
            public List<Car> listCars() { return List.of(); }
        };

        CarController controller = new CarController(fakeService);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, () -> {
            controller.isInsuranceValid(99L, "2024-06-01");
        });

        assertEquals(404, ex.getStatusCode().value());
        assertTrue(ex.getReason().contains("Car not found"));
    }


}
