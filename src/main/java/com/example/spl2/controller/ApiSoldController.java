package com.example.spl2.controller;

import com.example.spl2.repository.SoldRepository;
import com.example.spl2.dto.PlayerDTO;
import com.example.spl2.entity.Sold;
import com.example.spl2.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/sold")
public class ApiSoldController {

    @Autowired
    private SoldRepository soldRepository;

    @Autowired
    private PlayerService playerService;

    @GetMapping
    public ResponseEntity<List<com.example.spl2.dto.PlayerDTO>> listSold() {
        List<Sold> sold = soldRepository.findAll();
        List<PlayerDTO> dtos = sold.stream().map(s -> {
            PlayerDTO dto = playerService.convertPlayerToDTO(s.getPlayer());
            dto.setSoldPrice(s.getSoldPrice());
            if (s.getTeam() != null) {
                dto.setTeamId(s.getTeam().getId());
                dto.setTeamName(s.getTeam().getTeamName());
            }
            return dto;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
}

