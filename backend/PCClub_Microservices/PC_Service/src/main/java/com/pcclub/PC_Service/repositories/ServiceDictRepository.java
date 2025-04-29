package com.pcclub.PC_Service.repositories;

import com.pcclub.PC_Service.models.ServiceDict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceDictRepository extends JpaRepository<ServiceDict, Long> {
    List<ServiceDict> findAllByPcGroupId(Long pcGroupId);
}
