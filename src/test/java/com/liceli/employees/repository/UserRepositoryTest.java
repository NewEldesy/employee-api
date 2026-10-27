

package com.liceli.employees.repository;

import com.liceli.employees.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByUsername() {
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("encoded-test-password");

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        assertThat(result.get().getId()).isNotNull();
    }

    @Test
    void shouldReturnEmptyWhenUsernameDoesNotExist() {
        Optional<User> result =
                userRepository.findByUsername("unknownuser");

        assertThat(result).isEmpty();
    }
}
