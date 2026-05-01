package com.example.cafe_con_huellas.scheduler;

import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;


/**
 * Tarea programada que desactiva automáticamente los vínculos usuario-mascota
 * cuya fecha de fin ({@code endDate}) ya ha pasado.
 *
 * Se ejecuta cada día a medianoche. Si una relación tiene {@code active = true}
 * pero su {@code endDate} es anterior a la fecha actual, se pone a {@code false}
 * sin necesidad de intervención del administrador.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RelationshipExpirationScheduler {

    private final UserPetRelationshipRepository relationshipRepository;

    /**
     * Desactiva todos los vínculos activos cuyo {@code endDate} ya ha vencido.
     * Cron: 0 0 0 * * *  →  se ejecuta a las 00:00:00 de cada día.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void deactivateExpiredRelationships() {
        LocalDate today = LocalDate.now();

        List<UserPetRelationship> expired =
                relationshipRepository.findByActiveTrueAndEndDateBefore(today);

        if (expired.isEmpty()) {
            log.info("[Scheduler] No hay relaciones vencidas que desactivar.");
            return;
        }

        expired.forEach(r -> r.setActive(false));
        relationshipRepository.saveAll(expired);

        log.info("[Scheduler] Se han desactivado {} relación/es vencida/s.", expired.size());
    }

}
