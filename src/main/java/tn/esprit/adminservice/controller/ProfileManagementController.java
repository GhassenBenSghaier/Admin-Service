//package tn.esprit.adminservice.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tn.esprit.adminservice.entity.Profile;
//import tn.esprit.adminservice.service.ProfileManagementService;
//
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/admin/profiles")
//public class ProfileManagementController {
//
//    @Autowired
//    private ProfileManagementService profileManagementService;
//
//    @PostMapping
//    public ResponseEntity<?> createProfile(@RequestBody Map<String, Object> profileData) {
//        try {
//            String code = (String) profileData.get("code");
//            String designation = (String) profileData.get("designation");
//            String status = (String) profileData.get("status");
//            List<String> permissions = (List<String>) profileData.get("permissions"); // Fixed syntax here
//            Profile profile = profileManagementService.createProfile(code, designation, status, permissions);
//            return ResponseEntity.ok(profile);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, Object> profileData) {
//        try {
//            String designation = (String) profileData.get("designation");
//            String status = (String) profileData.get("status");
//            List<String> permissions = (List<String>) profileData.get("permissions"); // Fixed syntax here
//            Profile profile = profileManagementService.updateProfile(id, designation, status, permissions);
//            return ResponseEntity.ok(profile);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteProfile(@PathVariable Long id) {
//        try {
//            profileManagementService.deleteProfile(id);
//            return ResponseEntity.ok().build();
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
//        }
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Profile>> getAllProfiles() {
//        return ResponseEntity.ok(profileManagementService.getAllProfiles());
//    }
//
//}

//package tn.esprit.adminservice.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tn.esprit.adminservice.entity.Permission;
//import tn.esprit.adminservice.entity.Profile;
//import tn.esprit.adminservice.service.ProfileManagementService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/admin/profiles")
//public class ProfileManagementController {
//    @Autowired
//    private ProfileManagementService profileManagementService;
//
//    @PostMapping
//    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
//        Profile createdProfile = profileManagementService.createProfile(
//                profile.getCode(),
//                profile.getDesignation(),
//                profile.getStatus(),
//                profile.getPermissions().stream().map(Permission::getName).toList()
//        );
//        return ResponseEntity.ok(createdProfile);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Profile> getProfile(@PathVariable Long id) {
//        Profile profile = profileManagementService.getProfile(id);
//        return ResponseEntity.ok(profile);
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
//        profile.setId(id); // Ensure ID matches the path variable
//        Profile updatedProfile = profileManagementService.updateProfile(profile);
//        return ResponseEntity.ok(updatedProfile);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
//        profileManagementService.deleteProfile(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Profile>> getAllProfiles() {
//        List<Profile> profiles = profileManagementService.getAllProfiles();
//        return ResponseEntity.ok(profiles);
//    }
//}


package tn.esprit.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit.adminservice.entity.Permission;
import tn.esprit.adminservice.entity.Profile;
import tn.esprit.adminservice.service.ProfileManagementService;

import java.util.List;

@RestController
@RequestMapping("/admin/profiles")
public class ProfileManagementController {

    @Autowired
    private ProfileManagementService profileManagementService;

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_PROFILE')")
    public ResponseEntity<Profile> createProfile(@RequestBody Profile profile) {
        Profile createdProfile = profileManagementService.createProfile(
                profile.getCode(),
                profile.getDesignation(),
                profile.getStatus(),
                profile.getPermissions().stream().map(Permission::getName).toList()
        );
        return ResponseEntity.ok(createdProfile);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<Profile> getProfile(@PathVariable Long id) {
        Profile profile = profileManagementService.getProfile(id);
        return ResponseEntity.ok(profile);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_PROFILE')")
    public ResponseEntity<Profile> updateProfile(@PathVariable Long id, @RequestBody Profile profile) {
        profile.setId(id);
        Profile updatedProfile = profileManagementService.updateProfile(profile);
        return ResponseEntity.ok(updatedProfile);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PROFILE')")
    public ResponseEntity<Void> deleteProfile(@PathVariable Long id) {
        profileManagementService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VIEW_PROFILES')")
    public ResponseEntity<List<Profile>> getAllProfiles() {
        List<Profile> profiles = profileManagementService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<Profile> getProfileByCode(@PathVariable String code) {
        Profile profile = profileManagementService.getProfileByCode(code);
        if (profile != null) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}