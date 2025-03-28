//package tn.esprit.adminservice.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import tn.esprit.adminservice.entity.User;
//
//public interface UserRepository extends JpaRepository<User, Long> {
//    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
//    User findByUsernameIgnoreCase(@Param("username") String username);
//}

package tn.esprit.adminservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.adminservice.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE LOWER(u.username) = LOWER(:username)")
    User findByUsernameIgnoreCase(@Param("username") String username);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
}