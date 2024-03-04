package nl.vng.diwi.services;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

import nl.vng.diwi.config.ProjectConfig;

@Disabled
class KeycloakServiceTest {

    static KeycloakService keycloakService;

    @BeforeAll
    static void beforeAll() throws Exception {
        ProjectConfig config = mock(ProjectConfig.class);

        when(config.getKcRealmName()).thenReturn("test-realm");
        when(config.getKcAuthServerUrl()).thenReturn("http://localhost:1780/");
        when(config.getKcResourceName()).thenReturn("test-client");
        when(config.getKcSecret()).thenReturn("test-secret");

        keycloakService = new KeycloakService(config);
    }

//    @Test
//    void testAddRemove() throws Exception {
//        String randomString = UUID.randomUUID().toString();
//        String email = randomString + "@example.com";
//        String name = randomString;
//        UserRole role = UserRole.Admin;
//
//        User newUser = new User()
//                .setEmail(email)
//                .setName(name)
//                .setRole(role);
//
//        keycloakService.addOrUpdateUser(newUser);
//
//        var usersAfterAdd = keycloakService.getUsers();
//
//        assertThat(usersAfterAdd)
//                .extracting("email", "name", "role")
//                .containsOnlyOnce(new Tuple(email, name, role));
//
//        User kcUser = usersAfterAdd.stream()
//                .filter(u -> u.getEmail().equals(email))
//                .findFirst()
//                .orElseThrow();
//
//        // Need to pass in kcUser, because that has a UUID associated with it.
//        keycloakService.deleteUser(kcUser);
//
//        var usersAfterDelete = keycloakService.getUsers();
//        assertThat(usersAfterDelete)
//                .extracting("email", "name", "role")
//                .doesNotContain(new Tuple(email, name, role));
//    }
//
//    @Test
//    void testUpdate() throws Exception {
//        String randomString = UUID.randomUUID().toString();
//        String email = randomString + "@example.com";
//        String name = randomString;
//        UserRole role = UserRole.Admin;
//
//        // Add user
//        User newUser = new User()
//                .setEmail(email)
//                .setName(name)
//                .setRole(role);
//
//        keycloakService.addOrUpdateUser(newUser);
//
//        var usersAfterAdd = keycloakService.getUsers();
//
//        assertThat(usersAfterAdd)
//                .extracting("email", "name", "role")
//                .containsOnlyOnce(new Tuple(email, name, role));
//
//        // Update user
//        User updatedUser = usersAfterAdd.stream()
//                .filter(u -> u.getEmail().equals(email))
//                .findFirst()
//                .orElseThrow();
//
//        UserRole updatedRole = UserRole.Admin;
//        updatedUser.setRole(updatedRole);
//        keycloakService.addOrUpdateUser(updatedUser);
//
//        var usersAfterUpdate = keycloakService.getUsers();
//
//        assertThat(usersAfterUpdate)
//                .extracting("email", "name", "role")
//                .containsOnlyOnce(new Tuple(email, name, updatedRole));
//
//        var kcUser = usersAfterUpdate.stream()
//                .filter(u -> u.getEmail().equals(email))
//                .findFirst()
//                .orElseThrow();
//
//        // Delete user
//        keycloakService.deleteUser(kcUser);
//
//        var usersAfterDelete = keycloakService.getUsers();
//        assertThat(usersAfterDelete)
//                .extracting("email", "name", "role")
//                .doesNotContain(new Tuple(email, name, role));
//    }

}
