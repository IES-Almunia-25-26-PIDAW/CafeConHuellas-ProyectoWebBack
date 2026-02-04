package com.example.cafe_con_huellas.service;

import com.example.cafe_con_huellas.model.entity.Pet;
import com.example.cafe_con_huellas.model.entity.PetCategory;
import com.example.cafe_con_huellas.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    // ---------- CRUD BÁSICO ----------

    // Obtiene todas las mascotas registradas
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    // Busca una mascota por ID
    public Pet findById(Long id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pet not found"));
    }

    // Guarda o actualiza una mascota
    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    // Elimina una mascota (borra también imágenes por cascade)
    public void deleteById(Long id) {
        petRepository.deleteById(id);
    }

    // ---------- FILTROS Y BÚSQUEDAS ----------

    // Devuelve una lista de mascotas filtradas por su estado de esterilización
    public List<Pet> findByNeutered(Boolean neutered) {
        return petRepository.findByNeutered(neutered);
    }

    // Devuelve mascotas por categoría (PERRO, GATO)
    public List<Pet> findByCategory(PetCategory category) {
        return petRepository.findByCategory(category);
    }

    // Filtra mascotas por condición PPP
    public List<Pet> findByIsPpp(Boolean isPpp) {
        return petRepository.findByIsPpp(isPpp);
    }


    // Devuelve mascotas con edad menor o igual a la indicada
    public List<Pet> findByMaxAge(Integer age) {
        return petRepository.findByAgeLessThanEqual(age);
    }

    // Búsqueda básica por nombre o raza
    public List<Pet> search(String text) {
        return petRepository
                .findByNameContainingIgnoreCaseOrBreedContainingIgnoreCase(text, text);
    }

    // ---------- ACTUALIZACIÓN CONTROLADA ----------

    // Actualiza solo información editable desde la web
    public Pet updateBasicInfo(Long id, Pet updatedPet) {
        Pet pet = findById(id);

        pet.setName(updatedPet.getName());
        pet.setDescription(updatedPet.getDescription());
        pet.setAge(updatedPet.getAge());
        pet.setWeight(updatedPet.getWeight());
        pet.setNeutered(updatedPet.getNeutered());
        pet.setIsPpp(updatedPet.getIsPpp());
        pet.setCategory(updatedPet.getCategory());

        return petRepository.save(pet);
    }


}
