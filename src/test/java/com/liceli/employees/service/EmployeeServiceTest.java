
package com.liceli.employees.service;

import com.liceli.employees.dto.EmployeeRequest;
import com.liceli.employees.dto.EmployeeResponse;
import com.liceli.employees.exception.EmployeeNotFoundException;
import com.liceli.employees.model.Employee;
import com.liceli.employees.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    private EmployeeService employeeService;

    @BeforeEach
    void setUp() {
        employeeService = new EmployeeService(employeeRepository);
    }

    @Test
    void createShouldReturnResponse() {
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Ramel");
        request.setLastName("Makosso");
        request.setEmail("ramel@example.com");
        request.setDepartment("Informatique");

        Employee saved = new Employee(1L, "Ramel", "Makosso", "ramel@example.com", "Informatique");
        when(employeeRepository.save(any(Employee.class))).thenReturn(saved);

        EmployeeResponse result = employeeService.create(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals("Ramel", result.firstName());
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void findByIdShouldReturnResponseWhenFound() {
        Employee employee = new Employee(1L, "Ramel", "Makosso", "ramel@example.com", "Informatique");
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponse result = employeeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    void deleteShouldThrowWhenEmployeeDoesNotExist() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.delete(99L));
        verify(employeeRepository, never()).deleteById(99L);
    }
}
