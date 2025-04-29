package com.pcclub.Client_Service.repositories;

import com.pcclub.Client_Service.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByEmailIgnoreCase(String email);
}
