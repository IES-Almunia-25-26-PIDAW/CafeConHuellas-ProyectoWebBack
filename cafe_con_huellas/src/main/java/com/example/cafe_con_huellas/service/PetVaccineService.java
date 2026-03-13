package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.dto.PetVaccineDTO;
import com.example.cafe_con_huellas.exception.BadRequestException;
import com.example.cafe_con_huellas.exception.ResourceNotFoundException;
import com.example.cafe_con_huellas.mapper.PetVaccineMapper;
import com.example.cafe_con_huellas.model.entity.PetVaccine;
import com.example.cafe_con_huellas.repository.PetRepository;
import com.example.cafe_con_huellas.repository.PetVaccineRepository;
import com.example.cafe_con_huellas.repository.VaccineRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la lógica de negocio del historial de vacunación de las mascotas.
 * <p>
 * Gestiona el registro y seguimiento de las vacunas administradas,
 * con validaciones de fechas y control de próximas dosis pendientes.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class PetVaccineService {

    private final PetVaccineRepository petVaccineRepository;
    private final PetRepository petRepository;
    private final VaccineRepository vaccineRepository;
    private final PetVaccineMapper petVaccineMapper;

    // ---------- CRUD BÁSICO ----------

    /**
     * Obtiene todo el historial de vacunación global convertido a DTO.
     *
     * @return lista de {@link PetVaccineDTO} con todos los registros
     */
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findAll() {
        return petVaccineRepository.findAll().stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca un registro de vacunación por su identificador.
     *
     * @param id identificador único del registro
     * @return {@link PetVaccineDTO} con los datos del registro
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional(readOnly = true)
    public PetVaccineDTO findById(Long id) {
        return petVaccineRepository.findById(id)
                .map(petVaccineMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Registro de vacunación no encontrado con ID: " + id));
    }

    /**
     * Registra la administración de una vacuna a una mascota.
     * <p>
     * Valida que la fecha de administración no sea futura y que
     * tanto la mascota como el tipo de vacuna existan en el sistema.
     * </p>
     *
     * @param dto datos del registro de vacunación
     * @return {@link PetVaccineDTO} con el registro persistido
     * @throws BadRequestException si la fecha de administración es futura
     * @throws ResourceNotFoundException si la mascota o la vacuna no existen
     */
    @Transactional
    public PetVaccineDTO save(PetVaccineDTO dto) {
        // Validación de negocio: La vacuna ya debe haberse puesto, no puede ser en el futuro
        if (dto.getDateAdministered() != null && dto.getDateAdministered().isAfter(LocalDate.now())) {
            throw new BadRequestException("La fecha de administración no puede ser una fecha futura.");
        }

        // Convertimos el DTO a Entidad
        PetVaccine petVaccine = petVaccineMapper.toEntity(dto);

        // Vinculamos con las entidades reales de la base de datos
        petVaccine.setPet(petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con ID: " + dto.getPetId())));

        petVaccine.setVaccine(vaccineRepository.findById(dto.getVaccineId())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de vacuna no encontrado con ID: " + dto.getVaccineId())));

        // Guardamos y retornamos el DTO
        return petVaccineMapper.toDto(petVaccineRepository.save(petVaccine));
    }

    /**
     * Elimina un registro de vacunación del historial médico.
     *
     * @param id identificador del registro a eliminar
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional
    public void deleteById(Long id) {
        if (!petVaccineRepository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar. Registro no encontrado");
        }
        petVaccineRepository.deleteById(id);
    }

    // ---------- FILTROS Y BÚSQUEDAS ----------

    /**
     * Obtiene el historial completo de vacunas de una mascota específica.
     *
     * @param petId identificador de la mascota
     * @return lista de {@link PetVaccineDTO} asociadas a la mascota
     */
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findByPetId(Long petId) {
        return petVaccineRepository.findByPetId(petId).stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Busca vacunas con próxima dosis programada a partir de una fecha concreta.
     *
     * @param fromDate fecha a partir de la cual buscar próximas dosis
     * @return lista de {@link PetVaccineDTO} con refuerzos pendientes
     */
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findUpcomingVaccines(LocalDate fromDate) {
        return petVaccineRepository.findByNextDoseDateAfter(fromDate).stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Identifica vacunas cuya fecha de refuerzo ya ha pasado y están pendientes de aplicar.
     *
     * @return lista de {@link PetVaccineDTO} con vacunas vencidas
     */
    @Transactional(readOnly = true)
    public List<PetVaccineDTO> findOverdueVaccines() {
        return petVaccineRepository.findByNextDoseDateBefore(LocalDate.now()).stream()
                .map(petVaccineMapper::toDto)
                .collect(Collectors.toList());
    }

    // ---------- ACTUALIZACIÓN CONTROLADA ----------

    /**
     * Actualiza la información médica de un registro de vacunación existente.
     * <p>
     * Solo permite modificar la fecha de la próxima dosis y las notas clínicas,
     * para no alterar la trazabilidad del tratamiento original.
     * </p>
     *
     * @param id  identificador del registro a actualizar
     * @param dto nuevos datos médicos
     * @return {@link PetVaccineDTO} con los datos actualizados
     * @throws ResourceNotFoundException si no existe el registro con ese ID
     */
    @Transactional
    public PetVaccineDTO updateMedicalInfo(Long id, PetVaccineDTO dto) {
        // Verificamos que el registro exista
        PetVaccine vaccine = petVaccineRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Registro no encontrado"));

        // Actualizamos campos permitidos para no romper la trazabilidad del tratamiento
        vaccine.setNextDoseDate(dto.getNextDoseDate());
        vaccine.setNotes(dto.getNotes());

        return petVaccineMapper.toDto(petVaccineRepository.save(vaccine));
    }



}
