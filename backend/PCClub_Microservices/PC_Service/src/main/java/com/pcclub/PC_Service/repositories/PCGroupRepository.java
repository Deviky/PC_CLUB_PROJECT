package com.pcclub.PC_Service.repositories;

import com.pcclub.PC_Service.models.PCGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PCGroupRepository extends JpaRepository<PCGroup, Long> {

}
