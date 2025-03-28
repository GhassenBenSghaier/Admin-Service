package tn.esprit.adminservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.adminservice.entity.Permission;

import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
    Optional<Permission> findByName(String name);
}
