package nl.vng.diwi.services;

import jakarta.inject.Inject;
import nl.vng.diwi.config.ProjectConfig;

public class KeycloakService implements AutoCloseable {
//    private final Keycloak keycloak;
//    private final RealmResource realm;
//    private final ProjectConfig config;
//    private final UsersResource usersResource;
//
//    private final ClientResource clientResource;
//    private final ClientRepresentation client;

    @SuppressWarnings("serial")
    public static class KeycloakPermissionException extends Exception {

        public KeycloakPermissionException(String message) {
            super(message);
        }
    }

    @Inject
    public KeycloakService(ProjectConfig config) throws KeycloakPermissionException {
//        this.config = config;
//        final var clientId = config.getKcResourceName();
//        logger.info("Connecting to keycloak with url: {}, realm: {} and client id: {}", config.getKcAuthServerUrl(), config.getKcRealmName(), clientId);
//        try {
//            keycloak = KeycloakBuilder.builder()
//                    .serverUrl(config.getKcAuthServerUrl())
//                    .realm(config.getKcRealmName())
//                    .clientId(clientId)
//                    .clientSecret(config.getKcSecret())
//                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
//                    .build();
//        } catch (NotAuthorizedException e) {
//            throw new KeycloakPermissionException("Could not connect to keycloak. Is the secret set correctly?");
//        }
//        realm = keycloak.realm(config.getKcRealmName());
//        usersResource = realm.users();
//        ClientsResource clientsResource = realm.clients();
//        List<ClientRepresentation> clients = clientsResource.findAll();
//        client = clients
//                .stream()
//                .filter(c -> c.getClientId()
//                        .equals(clientId))
//                .findFirst()
//                .orElseThrow(() -> {
//                    logger.info("Client count: {}", clients.size());
//                    return new KeycloakPermissionException(
//                            "Could not find client. Does the client have the 'realm-management query-clients' and 'realm-management view-clients' service account roles?");
//                });
//
//        clientResource = clientsResource.get(client.getId());
    }

//    public List<User> getUsers() {
//        var usersList = usersResource.list();
//
//        return usersList.stream()
//                .map((UserRepresentation u) -> {
//                    UserResource userResource = realm
//                            .users()
//                            .get(u.getId());
//
//                    Map<String, ClientMappingsRepresentation> clientMappings = userResource
//                            .roles()
//                            .getAll()
//                            .getClientMappings();
//
//                    // If there are no client specific roles the user is not authorized for this
//                    // app.
//                    // These users will be filtered out
//                    if (clientMappings == null) {
//                        logger.warn("Filtering out {} ({}) as it has no client mappings", u.getUsername(), u.getId());
//                        return null;
//                    }
//                    // We need an email in the db
//                    if (u.getEmail() == null || u.getEmail().isBlank()) {
//                        logger.warn("Filtering out {} ({}) as there is no email address set", u.getUsername(), u.getId());
//                        return null;
//                    }
//                    if (!u.isEnabled()) {
//                        logger.warn("Filtering out {} ({}) as it is disabled", u.getUsername(), u.getId());
//                        return null;
//                    }
//
//                    ClientMappingsRepresentation clientMappingsRepresentation = clientMappings
//                            .get(config.getKcResourceName());
//                    if (clientMappingsRepresentation == null) {
//                        logger.warn("Filtering out {} ({}) as it has no client mappings for {}", u.getUsername(), u.getId(), config.getKcResourceName());
//                        return null;
//                    }
//
//                    List<String> roles = clientMappingsRepresentation
//                            .getMappings()
//                            .stream()
//                            .map(m -> m.getName())
//                            .toList();
//
//                    UserRole role = UserService.getHighestRole(new HashSet<>(roles));
//                    return new User()
//                            .setEmail(u.getEmail())
//                            .setName(u.getUsername())
//                            .setUuid(UUID.fromString(u.getId()))
//                            .setRole(role);
//                })
//                // Filter out the users that do not have accounts for this application
//                .filter(u -> u != null)
//                .toList();
//    }
//
//    public void deleteUser(User deleteUser) {
//        var kcUser = usersResource.get(deleteUser.getUuid().toString());
//        RoleMappingResource roles = kcUser.roles();
//        RoleScopeResource clientLevelRoles = roles.clientLevel(client.getId());
//        List<RoleRepresentation> clientRoles = clientLevelRoles.listEffective();
//        clientLevelRoles.remove(clientRoles);
//    }
//
//    public void addOrUpdateUser(User newUser) throws FindUserException, AddUserException {
//        // When adding a user in the db it might already exist in keycloak. Check and
//        // act accordingly.
//        UserRepresentation existingUser = getMatchingKcUser(newUser);
//        if (existingUser == null) {
//            addUser(newUser);
//        } else {
//            // User exists, link it
//            updateUser(newUser, existingUser);
//        }
//    }
//
//    private void addUser(User newUser) throws AddUserException {
//        // Create new user
//        var newKcUser = new UserRepresentation();
//        newKcUser.setEmail(newUser.getEmail());
//        newKcUser.setUsername(newUser.getName());
//        newKcUser.setEnabled(true);
//
//        var response = usersResource.create(newKcUser);
//        if (!response.getStatusInfo().getFamily().equals(Family.SUCCESSFUL)) {
//            throw new AddUserException(response);
//        }
//        var userId = CreatedResponseUtil.getCreatedId(response);
//        UserResource userResource = usersResource.get(userId);
//        RoleRepresentation roleRepresentation = getRole(newUser);
//
//        RoleScopeResource userClientRolesResource = userResource.roles().clientLevel(client.getId());
//        userClientRolesResource.add(List.of(roleRepresentation));
//
//        triggerActionsEmail(userResource);
//    }
//
//    public void triggerActionsEmail(User user) throws FindUserException {
//        UserRepresentation matchingKcUser = getMatchingKcUser(user);
//        UserResource userResource = usersResource.get(matchingKcUser.getId());
//
//        triggerActionsEmail(userResource);
//    }
//
//    private void triggerActionsEmail(UserResource userResource) {
//        // Trigger an update profile action, if there are other open default actions configured in keycloak
//        // the user will have to do those as well.
//        logger.debug("Calling executeActionsEmail with client: {} and redirectUrl: {}",config.getKcResourceName(), config.getBaseUrl());
//        userResource.executeActionsEmail(config.getKcResourceName(), config.getBaseUrl(), List.of("UPDATE_PROFILE"));
//    }
//
//    private UserRepresentation getMatchingKcUser(User user) throws  FindUserException {
//        UserRepresentation existingUser;
//        var matchingUserList = usersResource.searchByEmail(user.getEmail(), true);
//        if (matchingUserList.isEmpty()) {
//            matchingUserList = usersResource.search(user.getName());
//        }
//        if (matchingUserList.size() > 1) {
//            var message = String.format("Multiple users in keycloak for user with username '%s' and email '%s'", user.getName(), user.getEmail());
//            logger.warn(message);
//            throw new FindUserException(message);
//        } else if (matchingUserList.isEmpty()) {
//            existingUser = null;
//        } else {
//            existingUser = matchingUserList.get(0);
//        }
//        return existingUser;
//    }
//
//    private RoleRepresentation getRole(User newUser) {
//        RoleResource roleResource = clientResource.roles().get(newUser.getRole().name());
//        RoleRepresentation roleRepresentation = roleResource.toRepresentation();
//        return roleRepresentation;
//    }
//
//    private void updateUser(User newUser, UserRepresentation existingUser) {
//        String roleName = newUser.getRole().name();
//
//        RoleResource roleResource = clientResource.roles().get(roleName);
//        RoleRepresentation roleRepresentation = roleResource.toRepresentation();
//        UserResource userResource = usersResource.get(existingUser.getId());
//
//        RoleScopeResource userClientRolesResource = userResource.roles().clientLevel(client.getId());
//        userClientRolesResource.remove(userClientRolesResource.listEffective());
//        userClientRolesResource.add(List.of(roleRepresentation));
//
//        userResource.update(existingUser);
//    }

    @Override
    public void close() throws Exception {
//        logger.info("cleaning up keycloak service");
//        keycloak.close();
    }
}
