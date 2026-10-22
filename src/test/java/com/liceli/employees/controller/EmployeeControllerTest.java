
package com.liceli.employees.controller;

import com.liceli.employees.model.Employee;
import com.liceli.employees.service.EmployeeService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @Test
    void createShouldReturn201() throws Exception {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Amina");
        employee.setLastName("Kabore");
        employee.setEmail("amina.kabore@example.com");
        employee.setDepartment("Informatique");

        when(employeeService.create(any(Employee.class)))
                .thenReturn(employee);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByIdShouldReturn404WhenEmployeeDoesNotExist()
            throws Exception {

        when(employeeService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenEmailIsInvalid() throws Exception {
        Employee employee = new Employee();
        employee.setFirstName("Amina");
        employee.setLastName("Kabore");
        employee.setEmail("adresse-invalide");
        employee.setDepartment("Informatique");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employee)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturn200WhenEmployeeExists() throws Exception {
        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setFirstName("Ramel");
        updatedEmployee.setLastName("Makosso");
        updatedEmployee.setEmail("ramel@example.com");
        updatedEmployee.setDepartment("Informatique");

        when(employeeService.update(eq(1L), any(Employee.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.department").value("Informatique"));
    }

    @Test
    void deleteShouldReturn204WhenEmployeeExists() throws Exception {
        when(employeeService.delete(7L)).thenReturn(true);

        mockMvc.perform(delete("/api/employees/7"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturn404WhenEmployeeDoesNotExist() throws Exception {
        when(employeeService.delete(99L)).thenReturn(false);

        mockMvc.perform(delete("/api/employees/99"))
                .andExpect(status().isNotFound());
    }
}