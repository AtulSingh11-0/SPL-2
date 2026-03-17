package com.example.spl2.controller;

import com.example.spl2.repository.UnsoldRepository;
import com.example.spl2.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/unsold")
public class ApiUnsoldController {

    @Autowired
    private UnsoldRepository unsoldRepository;

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<com.example.spl2.dto.PlayerDTO>> listUnsold() {
        var unsold = unsoldRepository.findAll();
        List<com.example.spl2.dto.PlayerDTO> dtos = unsold.stream()
                .map(u -> playerService.convertPlayerToDTO(u.getPlayer()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

