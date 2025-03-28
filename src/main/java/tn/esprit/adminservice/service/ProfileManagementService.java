

//package tn.esprit.adminservice.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import tn.esprit.adminservice.entity.Permission;
//import tn.esprit.adminservice.entity.Profile;
//import tn.esprit.adminservice.repository.PermissionRepository;
//import tn.esprit.adminservice.repository.ProfileRepository;
//
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//public class ProfileManagementService {
//    @Autowired
//    private ProfileRepository profileRepository;
//    @Autowired
//    private PermissionRepository permissionRepository;
//
//    public Profile createProfile(String code, String designation, String status, List<String> permissionNames) {
//        Profile profile = new Profile();
//        profile.setCode(code);
//        profile.setDesignation(designation);
//        profile.setStatus(status);
//        Set<Permission> permissions = permissionNames.stream()
//                .map(name -> permissionRepository.findByName(name)
//                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name)))
//                .collect(Collectors.toSet());
//        profile.setPermissions(permissions);
//        return profileRepository.save(profile);
//    }
//
//    public Profile getProfile(Long id) {
//        return profileRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + id));
//    }
//
//    public Profile updateProfile(Profile profile) {
//        Profile existingProfile = profileRepository.findById(profile.getId())
//                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + profile.getId()));
//        existingProfile.setCode(profile.getCode()); // Allow code to be updated
//        existingProfile.setDesignation(profile.getDesignation());
//        existingProfile.setStatus(profile.getStatus());
//        Set<Permission> permissions = profile.getPermissions().stream()
//                .map(permission -> permissionRepository.findByName(permission.getName())
//                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permission.getName())))
//                .collect(Collectors.toSet());
//        existingProfile.setPermissions(permissions);
//        return profileRepository.save(existingProfile);
//    }
//
//    public void deleteProfile(Long id) {
//        Profile profile = profileRepository.findById(id)
//                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + id));
//        profileRepository.delete(profile);
//    }
//
//    public List<Profile> getAllProfiles() {
//        return profileRepository.findAll();
//    }
//}

package tn.esprit.adminservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.adminservice.entity.Permission;
import tn.esprit.adminservice.entity.Profile;
import tn.esprit.adminservice.repository.PermissionRepository;
import tn.esprit.adminservice.repository.ProfileRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProfileManagementService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PermissionRepository permissionRepository;

    public Profile createProfile(String code, String designation, String status, List<String> permissionNames) {
        Profile profile = new Profile();
        profile.setCode(code);
        profile.setDesignation(designation);
        profile.setStatus(status);
        Set<Permission> permissions = permissionNames.stream()
                .map(name -> permissionRepository.findByName(name)
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + name)))
                .collect(Collectors.toSet());
        profile.setPermissions(permissions);
        return profileRepository.save(profile);
    }

    public Profile getProfile(Long id) {
        return profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + id));
    }

    // New method to fetch profile by code
    public Profile getProfileByCode(String code) {
        return profileRepository.findByCode(code)
                .orElse(null); // Return null if not found; controller handles this
    }

    public Profile updateProfile(Profile profile) {
        Profile existingProfile = profileRepository.findById(profile.getId())
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + profile.getId()));
        existingProfile.setCode(profile.getCode());
        existingProfile.setDesignation(profile.getDesignation());
        existingProfile.setStatus(profile.getStatus());
        Set<Permission> permissions = profile.getPermissions().stream()
                .map(permission -> permissionRepository.findByName(permission.getName())
                        .orElseThrow(() -> new IllegalArgumentException("Permission not found: " + permission.getName())))
                .collect(Collectors.toSet());
        existingProfile.setPermissions(permissions);
        return profileRepository.save(existingProfile);
    }

    public void deleteProfile(Long id) {
        Profile profile = profileRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Profile not found with id: " + id));
        profileRepository.delete(profile);
    }

    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
}