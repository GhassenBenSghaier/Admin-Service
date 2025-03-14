//package tn.esprit.adminservice.service;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Service;
//import tn.esprit.adminservice.entity.User;
//import tn.esprit.adminservice.repository.UserRepository;
//
//import java.util.List;
//
//@Service
//public class UserManagementService {
//
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PasswordEncoder passwordEncoder;
//
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }
//
//    public User addUser(User user) {
//        if (userRepository.existsByEmail(user.getEmail())) {
//            throw new IllegalArgumentException("Email already exists");
//        }
//        if (user.getRole() == null) user.setRole("STUDENT"); // Default role
//        if (user.getStatus() == null) user.setStatus("Pending"); // Default status
//        if (user.getPassword() != null) {
//            user.setPassword(passwordEncoder.encode(user.getPassword()));
//        }
//        return userRepository.save(user);
//    }
//
//    public void deleteUser(Long id) {
//        userRepository.deleteById(id);
//    }
//}

package tn.esprit.adminservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.adminservice.entity.User;
import tn.esprit.adminservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserManagementService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (user.getRole() == null) user.setRole("STUDENT"); // Default role
        if (user.getStatus() == null) user.setStatus("Pending"); // Default status
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public User updateUser(Long id, User userDetails) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setUsername(userDetails.getUsername());
            user.setEmail(userDetails.getEmail());
            user.setRole(userDetails.getRole());
            user.setStatus(userDetails.getStatus());
            // Only encode password if it's provided (e.g., changed by admin)
            if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
            }
            return userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}