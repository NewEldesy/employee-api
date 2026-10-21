
package com.liceli.employees.service;

import com.liceli.employees.model.Employee;
import com.liceli.employees.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    void createShouldSaveEmployee() {
        Employee employee = new Employee();
        employee.setFirstName("Ramel");
        employee.setLastName("Makosso");

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee result = employeeService.create(employee);

        assertNotNull(result);
        verify(employeeRepository).save(employee);
    }

    @Test
    void findByIdShouldReturnEmployeeWhenFound() {
        Employee employee = new Employee();
        employee.setId(1L);

        when(employeeRepository.findById(1L))
                .thenReturn(Optional.of(employee));

        Employee result = employeeService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void deleteShouldReturnFalseWhenEmployeeDoesNotExist() {
        when(employeeRepository.existsById(99L)).thenReturn(false);

        boolean result = employeeService.delete(99L);

        assertFalse(result);
        verify(employeeRepository, never()).deleteById(99L);
    }
}
