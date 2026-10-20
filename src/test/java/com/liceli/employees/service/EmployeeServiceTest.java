
package com.liceli.employees.service;

import com.liceli.employees.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmployeeServiceTest {

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService();
    }

    @Test
    void shouldCreateAndFindEmployee() {
        Employee employee = new Employee(
                null,
                "Amina",
                "Traore",
                "amina@example.com",
                "Informatique"
        );

        Employee created = employeeService.create(employee);

        assertNotNull(created.getId());
        assertEquals("Amina", created.getFirstName());
        assertEquals(created, employeeService.findById(created.getId()));
    }

    @Test
    void shouldDeleteEmployee() {
        Employee employee = new Employee(
                null,
                "Amina",
                "Traore",
                "amina@example.com",
                "Informatique"
        );

        Employee created = employeeService.create(employee);

        assertTrue(employeeService.delete(created.getId()));
        assertNull(employeeService.findById(created.getId()));
    }
}