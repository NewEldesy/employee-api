
package com.liceli.employees.controller;

import com.liceli.employees.config.SecurityConfig;
import com.liceli.employees.dto.EmployeeRequest;
import com.liceli.employees.dto.EmployeeResponse;
import com.liceli.employees.security.CustomUserDetailsService;
import com.liceli.employees.service.EmployeeService;

import tools.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import com.liceli.employees.security.JwtAuthFilter;
import com.liceli.employees.security.JwtUtil;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@WebMvcTest(value = EmployeeController.class, excludeAutoConfiguration = {UserDetailsServiceAutoConfiguration.class})
@AutoConfigureMockMvc
@Import({SecurityConfig.class, JwtAuthFilter.class, JwtUtil.class})
class EmployeeSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private EmployeeService employeeService;

    @MockitoBean
    private CustomUserDetailsService userDetailsService;

    @Test
    void anonymousUserCannotCreateEmployee() throws Exception {
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validEmployeeJson()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void userRoleCannotCreateEmployee() throws Exception {
        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validEmployeeJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testadmin", roles = "ADMIN")
    void adminRoleCanCreateEmployee() throws Exception {
        EmployeeResponse response = new EmployeeResponse(
                1L, "Test", "Admin", "test.admin@example.com", "Informatique");

        when(employeeService.create(any(EmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validEmployeeJson()))
                .andExpect(status().isCreated());
    }

    private String validEmployeeJson() throws Exception {
        EmployeeRequest request = new EmployeeRequest();
        request.setFirstName("Test");
        request.setLastName("Security");
        request.setEmail("test.security@example.com");
        request.setDepartment("Informatique");

        return objectMapper.writeValueAsString(request);
    }
    
    @Test
    void anonymousUserCannotReadEmployees() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void userRoleCanReadEmployees() throws Exception {
        when(employeeService.findAll())
                .thenReturn(java.util.Collections.emptyList());

        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void userRoleCannotUpdateEmployee() throws Exception {
        mockMvc.perform(put("/api/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validEmployeeJson()))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "testuser", roles = "USER")
    void userRoleCannotDeleteEmployee() throws Exception {
        mockMvc.perform(delete("/api/employees/1"))
                .andExpect(status().isForbidden());
    }
}
