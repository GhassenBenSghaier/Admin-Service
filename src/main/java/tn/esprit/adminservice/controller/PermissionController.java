package tn.esprit.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.adminservice.entity.Permission;
import tn.esprit.adminservice.repository.PermissionRepository;

import java.util.List;

@RestController
@RequestMapping("/admin/permissions")
public class PermissionController {

    @Autowired
    private PermissionRepository permissionRepository;

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissions() {
        return ResponseEntity.ok(permissionRepository.findAll());
    }



    @PostMapping("/seed")
    public ResponseEntity<String> seedPermissions() {
        String[] permissions = {
                "VIEW_CALENDAR", "ADD_CALENDAR", "EDIT_CALENDAR", "DELETE_CALENDAR",
                "VIEW_USERS_CENTRAL_ADMIN", "VIEW_USERS_LOCAL_ADMIN", "VIEW_USERS_TEACHER", "VIEW_USERS_STUDENT",
                "ADD_USER_CENTRAL_ADMIN", "ADD_USER_LOCAL_ADMIN", "ADD_USER_TEACHER", "ADD_USER_STUDENT",
                "EDIT_USER_CENTRAL_ADMIN", "EDIT_USER_LOCAL_ADMIN", "EDIT_USER_TEACHER", "EDIT_USER_STUDENT",
                "DELETE_USER_CENTRAL_ADMIN", "DELETE_USER_LOCAL_ADMIN", "DELETE_USER_TEACHER", "DELETE_USER_STUDENT",
                "VIEW_PROFILES", "CREATE_PROFILE", "EDIT_PROFILE", "DELETE_PROFILE", "VIEW_PERMISSIONS"
        };

        for (String perm : permissions) {
            if (permissionRepository.findByName(perm).isEmpty()) {
                Permission permission = new Permission();
                permission.setName(perm);
                permission.setDescription("Permission for " + perm.replace("_", " ").toLowerCase());
                permissionRepository.save(permission);
            }
        }
        return ResponseEntity.ok("Permissions seeded successfully");
    }
}