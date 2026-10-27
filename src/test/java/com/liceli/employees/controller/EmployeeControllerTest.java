
package com.liceli.employees.controller;

import com.liceli.employees.dto.EmployeeRequest;
import com.liceli.employees.dto.EmployeeResponse;
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
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Amina");
        request.setLastName("Kabore");
        request.setEmail("amina.kabore@example.com");
        request.setDepartment("Informatique");

        EmployeeResponse response = new EmployeeResponse(
                1L, "Amina", "Kabore", "amina.kabore@example.com", "Informatique");

        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void findByIdShouldReturn404WhenEmployeeDoesNotExist() throws Exception {
        when(employeeService.findById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/employees/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createShouldReturn400WhenEmailIsInvalid() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Amina");
        request.setLastName("Kabore");
        request.setEmail("adresse-invalide");
        request.setDepartment("Informatique");

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateShouldReturn200WhenEmployeeExists() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Ramel");
        request.setLastName("Makosso");
        request.setEmail("ramel@example.com");
        request.setDepartment("Informatique");

        EmployeeResponse response = new EmployeeResponse(
                1L, "Ramel", "Makosso", "ramel@example.com", "Informatique");

        when(employeeService.update(eq(1L), any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
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
