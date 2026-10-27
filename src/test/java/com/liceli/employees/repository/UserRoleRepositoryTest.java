
package com.liceli.employees.repository;

import com.liceli.employees.model.Role;
import com.liceli.employees.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRoleRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void shouldSaveUserWithRole() {
        // 1. Créer et enregistrer le rôle
        Role role = new Role("ROLE_TEST");
        roleRepository.save(role);

        // 2. Créer l'utilisateur
        User user = new User();
        user.setUsername("relation_test");
        user.setPassword("encoded-test-password");

        // 3. Associer le rôle à l'utilisateur
        user.getRoles().add(role);

        // 4. Enregistrer l'utilisateur
        userRepository.saveAndFlush(user);

        // 5. Rechercher l'utilisateur en base
        Optional<User> result =
                userRepository.findByUsername("relation_test");

        // 6. Vérifier que l'utilisateur et son rôle existent
        assertThat(result).isPresent();
        assertThat(result.get().getRoles())
                .extracting(Role::getName)
                .contains("ROLE_TEST");
    }
}
