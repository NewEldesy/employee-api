
package com.liceli.employees.security;

import com.liceli.employees.model.Role;
import com.liceli.employees.model.User;
import com.liceli.employees.repository.RoleRepository;
import com.liceli.employees.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

import java.util.Set;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = Replace.ANY)
class DisabledUserSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void disabledUserCannotAccessEmployeesEndpoint() throws Exception {
        Role role = roleRepository.findByName("ROLE_USER")
                .orElseGet(() ->
                        roleRepository.save(new Role("ROLE_USER")));

        User user = new User();
        user.setUsername("disabled-integration-user");
        user.setPassword(passwordEncoder.encode("ValidPassword123!"));
        user.setEnabled(false);
        user.setRoles(Set.of(role));

        userRepository.saveAndFlush(user);

        mockMvc.perform(get("/api/employees")
                        .with(httpBasic(
                                "disabled-integration-user",
                                "ValidPassword123!"
                        )))
                .andExpect(status().isUnauthorized());
    }
}
