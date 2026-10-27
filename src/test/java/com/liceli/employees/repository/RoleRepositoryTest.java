
package com.liceli.employees.repository;

import com.liceli.employees.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldSaveAndFindRoleByName() {
        Role role = new Role("ROLE_TEST");

        roleRepository.save(role);

        Optional<Role> result =
                roleRepository.findByName("ROLE_TEST");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("ROLE_TEST");
        assertThat(result.get().getId()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenRoleDoesNotExist() {
        Optional<Role> result =
                roleRepository.findByName("ROLE_UNKNOWN");

        assertThat(result).isEmpty();
    }
}
