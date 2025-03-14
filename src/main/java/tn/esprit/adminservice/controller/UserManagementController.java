//package tn.esprit.adminservice.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import tn.esprit.adminservice.entity.User;
//import tn.esprit.adminservice.service.UserManagementService;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/admin/users")
//public class UserManagementController {
//
//    @Autowired
//    private UserManagementService userManagementService;
//
//    @GetMapping
//    public ResponseEntity<List<User>> getAllUsers() {
//        return ResponseEntity.ok(userManagementService.getAllUsers());
//    }
//
//    @PostMapping
//    public ResponseEntity<User> addUser(@RequestBody User user) {
//        try {
//            User savedUser = userManagementService.addUser(user);
//            return ResponseEntity.ok(savedUser);
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.badRequest().body(null);
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        userManagementService.deleteUser(id);
//        return ResponseEntity.ok().build();
//    }
//
//
//}

package tn.esprit.adminservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.adminservice.entity.User;
import tn.esprit.adminservice.service.UserManagementService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
public class UserManagementController {

    @Autowired
    private UserManagementService userManagementService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<User> addUser(@RequestBody User user) {
        try {
            User savedUser = userManagementService.addUser(user);
            return ResponseEntity.ok(savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userManagementService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userManagementService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}