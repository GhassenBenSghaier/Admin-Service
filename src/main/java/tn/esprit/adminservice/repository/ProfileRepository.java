package tn.esprit.adminservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.adminservice.entity.Profile;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    Optional<Profile> findByCode(String code);
}
