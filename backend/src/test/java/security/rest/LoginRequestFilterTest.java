package security.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.container.ContainerRequestContext;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.adapters.OidcKeycloakAccount;
import org.keycloak.adapters.spi.KeycloakAccount;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;

import com.vng.config.ProjectConfig;
import com.vng.dal.Dal;
import com.vng.dal.DalFactory;
import com.vng.security.LoginRequestFilter;
import com.vng.security.SecurityRepository;
import com.vng.security.User;
import com.vng.security.UserRole;
import com.vng.services.UserService;
import com.vng.testutil.TestDb;

class LoginRequestFilterTest {
    // Class scope
    private static final String TEST_RESOURCE_NAME = "resource-name";
    private static final String TEST_NAME = "";
    private static final String TEST_UUID = "aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeee";
    private static final String TEST_EMAIL = "test@example.com";
    private static TestDb testDb;

    @BeforeAll
    static void beforeAll() throws Exception {
        testDb = new TestDb();
    }

    @AfterAll
    static void afterAll() throws Exception {
        testDb.close();
    }

    // Instance scope
    private Dal dal;
    private DalFactory dalFactory;
    private ContainerRequestContext requestContext;
    private ProjectConfig config;
    private HttpServletRequest servletRequest;
    private Principal principal;
    private HttpSession httpSession;
    private OidcKeycloakAccount keycloakAccount;
    private KeycloakSecurityContext keycloakSecurityContext;
    private AccessToken accessToken;
    private LoginRequestFilter filter;
    private Access access;

    @BeforeEach
    void beforeEach() throws Exception {
        testDb.reset();
        dalFactory = testDb.getDalFactory();
        dal = dalFactory.constructDal();

        requestContext = mock(ContainerRequestContext.class);
        config = mock(ProjectConfig.class);
        when(config.getKcResourceName()).thenReturn(TEST_RESOURCE_NAME);

        principal = mock(Principal.class);
        when(principal.getName()).thenReturn(TEST_UUID);

        access = new Access();

        accessToken = new AccessToken();
        accessToken.setEmail(TEST_EMAIL);
        accessToken.setName(TEST_NAME);
        accessToken.setResourceAccess(Map.of(TEST_RESOURCE_NAME, access));

        keycloakSecurityContext = mock(KeycloakSecurityContext.class);
        when(keycloakSecurityContext.getToken()).thenReturn(accessToken);

        keycloakAccount = mock(OidcKeycloakAccount.class);
        when(keycloakAccount.getKeycloakSecurityContext()).thenReturn(keycloakSecurityContext);

        httpSession = mock(HttpSession.class);
        when(httpSession.getAttribute(KeycloakAccount.class.getName())).thenReturn(keycloakAccount);

        servletRequest = mock(HttpServletRequest.class);
        when(servletRequest.getSession()).thenReturn(httpSession);
        when(servletRequest.getUserPrincipal()).thenReturn(principal);

        filter = new LoginRequestFilter(servletRequest, dalFactory, config, new UserService());
    }

    @AfterEach
    void afterEach() {
        dal.close();
    }
//
//    @EnumSource(value = UserRole.class)
//    @ParameterizedTest
//    void testCreateUser(UserRole expected) throws Exception {
//        access.addRole(expected.toString());
//
//        filter.filter(requestContext);
//
//        var repo = new SecurityRepository(dal.getSession());
//        var user = repo.getUserByEmail(TEST_EMAIL);
//
//        assertThat(user)
//                .usingRecursiveComparison()
//                .ignoringFields("id", "creationDate")
//                .isEqualTo(new User()
//                        .setEmail(TEST_EMAIL)
//                        .setName(TEST_NAME)
//                        .setRole(expected)
//                        .setUuid(UUID.fromString(TEST_UUID)));
//    }
//
//    @EnumSource(value = UserRole.class)
//    @ParameterizedTest
//    void testLinkUser(UserRole expected) throws Exception {
//        access.addRole(expected.toString());
//
//        var repo1 = new SecurityRepository(dal.getSession());
//
//        User dbUser;
//        try (var transaction = repo1.beginTransaction()) {
//            dbUser = repo1.persist(new User()
//                    .setEmail(TEST_EMAIL)
//                    .setName(TEST_NAME)
//                    .setRole(expected));
//            transaction.commit();
//        }
//
//        filter.filter(requestContext);
//
//        User user = getUser(TEST_EMAIL);
//        assertThat(user)
//                .usingRecursiveComparison()
//                .ignoringFields("creationDate")
//                .isEqualTo(new User()
//                        .setId(dbUser.getId())
//                        .setName(TEST_NAME)
//                        .setEmail(TEST_EMAIL)
//                        .setUuid(UUID.fromString(TEST_UUID))
//                        .setRole(expected));
//
//    }
//
//    private User getUser(String email) {
//        try (var session = dalFactory.constructDal().getSession()) {
//            session.beginTransaction();
//            var repo = new SecurityRepository(session);
//            return repo.getUserByEmail(email);
//        }
//    }
}
