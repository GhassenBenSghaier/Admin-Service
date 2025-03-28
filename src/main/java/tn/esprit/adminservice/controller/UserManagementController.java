

//package tn.esprit.adminservice.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.web.bind.annotation.*;
//import tn.esprit.adminservice.dto.UserDTO;
//import tn.esprit.adminservice.entity.User;
//import tn.esprit.adminservice.service.UserManagementService;
//
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/admin/users")
//public class UserManagementController {
//
//    @Autowired
//    private UserManagementService userManagementService;
//
//    /**
//     * Fetch all users, filtered by the authenticated user's VIEW_USERS_* permissions.
//     * Returns 403 if no relevant view permissions exist.
//     */
//    @GetMapping
//    public ResponseEntity<List<User>> getAllUsers() {
//        // Extract all authorities from the SecurityContext
//        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
//                .stream()
//                .map(GrantedAuthority::getAuthority)
//                .collect(Collectors.toList());
//
//        // Identify which roles the user can view
//        List<String> viewableRoles = authorities.stream()
//                .filter(auth -> auth.startsWith("VIEW_USERS_"))
//                .map(auth -> auth.substring("VIEW_USERS_".length()))
//                .collect(Collectors.toList());
//
//        if (viewableRoles.isEmpty()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
//        }
//
//        // Fetch and filter users based on viewable roles
//        List<User> users = userManagementService.getAllUsers().stream()
//                .filter(user -> viewableRoles.contains(user.getRole()))
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(users);
//    }
//
//    /**
//     * Fetch a user by ID, checking if the authenticated user has permission to view that role.
//     */
//    @GetMapping("/{id}")
//    public ResponseEntity<?> getUserById(@PathVariable Long id) {
//        try {
//            Optional<User> userOpt = userManagementService.getUserById(id);
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found with id: " + id));
//            }
//            User user = userOpt.get();
//            String requiredPermission = "VIEW_USERS_" + user.getRole();
//            if (!hasPermission(requiredPermission)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Insufficient permissions to view this user"));
//            }
//            return ResponseEntity.ok(user);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Add a new user, checking if the authenticated user has ADD_USER_* permission for the specified role.
//     */
//    @PostMapping
//    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
//        String requiredPermission = "ADD_USER_" + userDTO.getRole().toUpperCase();
//        if (!hasPermission(requiredPermission)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", "Insufficient permissions to add user with role: " + userDTO.getRole()));
//        }
//        try {
//            User savedUser = userManagementService.addUser(userDTO);
//            return ResponseEntity.ok(savedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Update a user, checking EDIT_USER_* permission based on the existing user's role.
//     */
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
//        try {
//            Optional<User> existingUserOpt = userManagementService.getUserById(id);
//            if (existingUserOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found with id: " + id));
//            }
//            User existingUser = existingUserOpt.get();
//            String requiredPermission = "EDIT_USER_" + existingUser.getRole();
//            if (!hasPermission(requiredPermission)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Insufficient permissions to edit this user"));
//            }
//            User updatedUser = userManagementService.updateUser(id, userDTO);
//            return ResponseEntity.ok(updatedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Delete a user, checking DELETE_USER_* permission based on the user's role.
//     */
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        try {
//            Optional<User> userOpt = userManagementService.getUserById(id);
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//            }
//            User user = userOpt.get();
//            String requiredPermission = "DELETE_USER_" + user.getRole();
//            if (!hasPermission(requiredPermission)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//            }
//            userManagementService.deleteUser(id);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().build();
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }
//
//    /**
//     * Assign a profile to a user (requires EDIT_USER_* permission for the user's role).
//     */
//    @PutMapping("/{id}/profile")
//    public ResponseEntity<?> assignProfile(@PathVariable Long id, @RequestBody Map<String, String> profileData) {
//        try {
//            Optional<User> userOpt = userManagementService.getUserById(id);
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found with id: " + id));
//            }
//            User user = userOpt.get();
//            String requiredPermission = "EDIT_USER_" + user.getRole();
//            if (!hasPermission(requiredPermission)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Insufficient permissions to modify this user’s profile"));
//            }
//            String profileCode = profileData.get("profileCode");
//            if (profileCode == null || profileCode.trim().isEmpty()) {
//                return ResponseEntity.badRequest().body(Map.of("error", "Profile code is required"));
//            }
//            User updatedUser = userManagementService.assignProfile(id, profileCode);
//            return ResponseEntity.ok(updatedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (IllegalStateException e) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Remove a profile from a user (requires EDIT_USER_* permission).
//     */
//    @DeleteMapping("/{id}/profile")
//    public ResponseEntity<?> removeProfile(@PathVariable Long id) {
//        try {
//            Optional<User> userOpt = userManagementService.getUserById(id);
//            if (userOpt.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found with id: " + id));
//            }
//            User user = userOpt.get();
//            String requiredPermission = "EDIT_USER_" + user.getRole();
//            if (!hasPermission(requiredPermission)) {
//                return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                        .body(Map.of("error", "Insufficient permissions to modify this user’s profile"));
//            }
//            User updatedUser = userManagementService.removeProfile(id);
//            return ResponseEntity.ok(updatedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    /**
//     * Helper method to check if the authenticated user has a specific permission.
//     */
//    private boolean hasPermission(String permission) {
//        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
//                .contains(new SimpleGrantedAuthority(permission));
//    }
//}

package tn.esprit.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;
import tn.esprit.adminservice.dto.UserDTO;
import tn.esprit.adminservice.entity.Permission;
import tn.esprit.adminservice.entity.User;
import tn.esprit.adminservice.entity.Profile;
import tn.esprit.adminservice.service.UserManagementService;
import tn.esprit.adminservice.service.ProfileManagementService;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @Autowired
    private ProfileManagementService profileManagementService; // Added to fetch profile details

    /**
     * Fetch all users, filtered by the authenticated user's VIEW_USERS_* permissions.
     * Returns 403 if no relevant view permissions exist.
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<String> authorities = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        List<String> viewableRoles = authorities.stream()
                .filter(auth -> auth.startsWith("VIEW_USERS_"))
                .map(auth -> auth.substring("VIEW_USERS_".length()))
                .collect(Collectors.toList());

        if (viewableRoles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        List<User> users = userManagementService.getAllUsers().stream()
                .filter(user -> viewableRoles.contains(user.getRole()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(users);
    }

    /**
     * Fetch a user by ID, checking if the authenticated user has permission to view that role.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userManagementService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with id: " + id));
            }
            User user = userOpt.get();
            String requiredPermission = "VIEW_USERS_" + user.getRole();
            if (!hasPermission(requiredPermission)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to view this user"));
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Add a new user, checking ADD_USER_* permission and profile permissions.
     */
    @PostMapping
    public ResponseEntity<?> addUser(@RequestBody UserDTO userDTO) {
        String requiredPermission = "ADD_USER_" + userDTO.getRole().toUpperCase();
        if (!hasPermission(requiredPermission)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Insufficient permissions to add user with role: " + userDTO.getRole()));
        }

        // Validate profile permissions
        if (userDTO.getProfileCode() != null) {
            Profile selectedProfile = profileManagementService.getProfileByCode(userDTO.getProfileCode());
            if (selectedProfile == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Profile not found with code: " + userDTO.getProfileCode()));
            }
            Set<String> profilePermissions = selectedProfile.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            List<String> userPermissions = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            if (!userPermissions.containsAll(profilePermissions)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Cannot assign profile '" + selectedProfile.getDesignation() +
                                "' as it contains permissions you do not have"));
            }
        }

        try {
            User savedUser = userManagementService.addUser(userDTO);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Update a user, checking EDIT_USER_* permission based on the existing user's role.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            Optional<User> existingUserOpt = userManagementService.getUserById(id);
            if (existingUserOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with id: " + id));
            }
            User existingUser = existingUserOpt.get();
            String requiredPermission = "EDIT_USER_" + existingUser.getRole();
            if (!hasPermission(requiredPermission)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to edit this user"));
            }

            // Validate profile permissions if profileCode is updated
            if (userDTO.getProfileCode() != null) {
                Profile selectedProfile = profileManagementService.getProfileByCode(userDTO.getProfileCode());
                if (selectedProfile == null) {
                    return ResponseEntity.badRequest()
                            .body(Map.of("error", "Profile not found with code: " + userDTO.getProfileCode()));
                }
                Set<String> profilePermissions = selectedProfile.getPermissions().stream()
                        .map(Permission::getName)
                        .collect(Collectors.toSet());
                List<String> userPermissions = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList());
                if (!userPermissions.containsAll(profilePermissions)) {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body(Map.of("error", "Cannot assign profile '" + selectedProfile.getDesignation() +
                                    "' as it contains permissions you do not have"));
                }
            }

            User updatedUser = userManagementService.updateUser(id, userDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Delete a user, checking DELETE_USER_* permission based on the user's role.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userManagementService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            User user = userOpt.get();
            String requiredPermission = "DELETE_USER_" + user.getRole();
            if (!hasPermission(requiredPermission)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            userManagementService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Assign a profile to a user, checking EDIT_USER_* and profile permissions.
     */
    @PutMapping("/{id}/profile")
    public ResponseEntity<?> assignProfile(@PathVariable Long id, @RequestBody Map<String, String> profileData) {
        try {
            Optional<User> userOpt = userManagementService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with id: " + id));
            }
            User user = userOpt.get();
            String requiredPermission = "EDIT_USER_" + user.getRole();
            if (!hasPermission(requiredPermission)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to modify this user’s profile"));
            }
            String profileCode = profileData.get("profileCode");
            if (profileCode == null || profileCode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Profile code is required"));
            }

            // Validate profile permissions
            Profile selectedProfile = profileManagementService.getProfileByCode(profileCode);
            if (selectedProfile == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Profile not found with code: " + profileCode));
            }
            Set<String> profilePermissions = selectedProfile.getPermissions().stream()
                    .map(Permission::getName)
                    .collect(Collectors.toSet());
            List<String> userPermissions = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                    .stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            if (!userPermissions.containsAll(profilePermissions)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Cannot assign profile '" + selectedProfile.getDesignation() +
                                "' as it contains permissions you do not have"));
            }

            User updatedUser = userManagementService.assignProfile(id, profileCode);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Remove a profile from a user (requires EDIT_USER_* permission).
     */
    @DeleteMapping("/{id}/profile")
    public ResponseEntity<?> removeProfile(@PathVariable Long id) {
        try {
            Optional<User> userOpt = userManagementService.getUserById(id);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found with id: " + id));
            }
            User user = userOpt.get();
            String requiredPermission = "EDIT_USER_" + user.getRole();
            if (!hasPermission(requiredPermission)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "Insufficient permissions to modify this user’s profile"));
            }
            User updatedUser = userManagementService.removeProfile(id);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Helper method to check if the authenticated user has a specific permission.
     */
    private boolean hasPermission(String permission) {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority(permission));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
        Optional<User> userOpt = userManagementService.getUserByUsername(username);
        return userOpt.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}