package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.AdoptionRequestDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.AdoptionRequestMapper;
import com.example.cafe_con_huellas.model.entity.AdoptionFormToken;
import com.example.cafe_con_huellas.model.entity.AdoptionRequest;
import com.example.cafe_con_huellas.model.entity.AdoptionRequestStatus;
import com.example.cafe_con_huellas.repository.AdoptionFormTokenRepository;
import com.example.cafe_con_huellas.repository.AdoptionRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdoptionRequestService {

    private final AdoptionRequestRepository requestRepository;
    private final AdoptionRequestMapper requestMapper;
    private final AdoptionFormTokenRepository tokenRepository;

    // Guarda la solicitud cuando el usuario envía el formulario
    @Transactional
    public AdoptionRequestDTO save(String token, AdoptionRequestDTO dto) {

        AdoptionFormToken formToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token no encontrado."));

        // Evitar duplicados si el token ya tiene una solicitud guardada
        if (requestRepository.existsByFormTokenId(formToken.getId())) {
            throw new BadRequestException("Ya existe una solicitud para este formulario.");
        }

        AdoptionRequest entity = requestMapper.toEntity(dto);
        entity.setFormToken(formToken);

        return requestMapper.toDto(requestRepository.save(entity));
    }

    // Lista todas las solicitudes (el admin ve todo)
    @Transactional(readOnly = true)
    public List<AdoptionRequestDTO> findAll() {
        return requestRepository.findAll().stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    // Filtra por estado: PENDIENTE, APROBADA, DENEGADA
    @Transactional(readOnly = true)
    public List<AdoptionRequestDTO> findByStatus(AdoptionRequestStatus status) {
        return requestRepository.findByStatus(status).stream()
                .map(requestMapper::toDto)
                .collect(Collectors.toList());
    }

    // Detalle de una solicitud concreta
    @Transactional(readOnly = true)
    public AdoptionRequestDTO findById(Long id) {
        return requestRepository.findById(id)
                .map(requestMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));
    }

    // El admin aprueba o rechaza la solicitud
    @Transactional
    public AdoptionRequestDTO updateStatus(Long id, AdoptionRequestStatus newStatus) {
        AdoptionRequest request = requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Solicitud no encontrada con ID: " + id));

        request.setStatus(newStatus);
        return requestMapper.toDto(requestRepository.save(request));
    }
}