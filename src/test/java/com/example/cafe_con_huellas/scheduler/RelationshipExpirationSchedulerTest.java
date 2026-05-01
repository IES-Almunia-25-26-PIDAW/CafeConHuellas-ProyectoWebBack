package com.example.cafe_con_huellas.scheduler;

import com.example.cafe_con_huellas.model.entity.UserPetRelationship;
import com.example.cafe_con_huellas.repository.UserPetRelationshipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelationshipExpirationSchedulerTest {

    @Mock
    private UserPetRelationshipRepository relationshipRepository;

    @InjectMocks
    private RelationshipExpirationScheduler scheduler;

    private UserPetRelationship expiredRelationship;

    @BeforeEach
    void setUp() {
        expiredRelationship = new UserPetRelationship();
        expiredRelationship.setId(1L);
        expiredRelationship.setActive(true);
        expiredRelationship.setEndDate(LocalDate.now().minusDays(1));
    }

    @Test
    @DisplayName("Debe desactivar las relaciones cuyo endDate ha vencido")
    void shouldDeactivateExpiredRelationships() {
        when(relationshipRepository.findByActiveTrueAndEndDateBefore(any(LocalDate.class)))
                .thenReturn(List.of(expiredRelationship));

        scheduler.deactivateExpiredRelationships();

        // Verificamos que se ha puesto active a false
        assertThat(expiredRelationship.getActive()).isFalse();
        // Verificamos que se ha llamado a saveAll con la relación desactivada
        verify(relationshipRepository, times(1)).saveAll(List.of(expiredRelationship));
    }

    @Test
    @DisplayName("No debe hacer nada si no hay relaciones vencidas")
    void shouldDoNothingWhenNoExpiredRelationships() {
        when(relationshipRepository.findByActiveTrueAndEndDateBefore(any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

        scheduler.deactivateExpiredRelationships();

        // Verificamos que no se ha llamado a saveAll
        verify(relationshipRepository, never()).saveAll(any());
    }
}
