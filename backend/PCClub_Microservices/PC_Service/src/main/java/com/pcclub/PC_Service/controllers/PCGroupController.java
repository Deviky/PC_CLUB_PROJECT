package com.pcclub.PC_Service.controllers;

import com.pcclub.PC_Service.dto.PCGroup.PCGroupCreateRequest;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupCreateResponse;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupDeleteResponse;
import com.pcclub.PC_Service.dto.PCGroup.PCGroupDto;
import com.pcclub.PC_Service.services.PCGroupService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/pc-service/pc-group")
@AllArgsConstructor
public class PCGroupController {

    @Autowired
    private final PCGroupService pcGroupService;


    @PostMapping("/create")
    public ResponseEntity<PCGroupCreateResponse> create(@Valid @RequestBody PCGroupCreateRequest pcGroupNew) {
        PCGroupCreateResponse response = pcGroupService.create(pcGroupNew);

        // Если группа успешно создана, возвращаем код 201 (Created)
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/get/{group_id}")
    public ResponseEntity<PCGroupDto> get(@PathVariable Long group_id) {
        try {
            PCGroupDto groupDto = pcGroupService.get(group_id);
            return ResponseEntity.ok(groupDto);  // Статус 200 (OK)
        } catch (IllegalArgumentException ex) {
            // Если группа не найдена, возвращаем 404 (Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<PCGroupDto>> getAll() {
        List<PCGroupDto> groupDtos = pcGroupService.getAll();
        return ResponseEntity.ok(groupDtos);  // Статус 200 (OK)
    }

    @DeleteMapping("/delete/{group_id}")
    public ResponseEntity<PCGroupDeleteResponse> delete(@PathVariable Long group_id) {
        PCGroupDeleteResponse response = pcGroupService.delete(group_id);

        if (response.getId() == null) {
            // Если группа не найдена, возвращаем 404 (Not Found)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if(response.getMessage().startsWith("Ошибка")){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        // Если группа удалена, возвращаем 200 (OK)
        return ResponseEntity.ok(response);
    }
}

