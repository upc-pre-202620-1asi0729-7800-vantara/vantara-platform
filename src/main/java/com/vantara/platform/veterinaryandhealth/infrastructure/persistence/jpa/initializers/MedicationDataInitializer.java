
package com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.initializers;

import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.entities.MedicationPersistenceEntity;
import com.vantara.platform.veterinaryandhealth.infrastructure.persistence.jpa.repositories.MedicationPersistenceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class MedicationDataInitializer implements CommandLineRunner {

    private final MedicationPersistenceRepository repository;

    public MedicationDataInitializer(MedicationPersistenceRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        List<MedicationPersistenceEntity> medications = List.of(
                create("MED-001", "Ivermectina 1%", "Antiparasitario",
                        "Ivermectina", "Solución inyectable"),
                create("MED-002", "Albendazol 10%", "Antiparasitario",
                        "Albendazol", "Suspensión oral"),
                create("MED-003", "Oxitetraciclina 10%", "Antibiótico",
                        "Oxitetraciclina", "Solución inyectable"),
                create("MED-004", "Penicilina", "Antibiótico",
                        "Penicilina", "Suspensión inyectable"),
                create("MED-005", "Flunixin Meglumina", "Antiinflamatorio",
                        "Flunixin meglumina", "Solución inyectable"),
                create("MED-006", "Meloxicam", "Antiinflamatorio",
                        "Meloxicam", "Solución inyectable"),
                create("MED-007", "Complejo B", "Vitaminas",
                        "Vitaminas B1, B6 y B12", "Solución inyectable"),
                create("MED-008", "Calcio y Fósforo", "Suplemento",
                        "Calcio y fósforo", "Solución inyectable"),
                create("MED-009", "Oxitocina", "Hormonal",
                        "Oxitocina", "Solución inyectable"),
                create("MED-010", "Suero electrolítico", "Rehidratante",
                        "Electrolitos", "Solución oral")
        );

        repository.saveAll(medications);
    }

    private MedicationPersistenceEntity create(
            String id,
            String name,
            String category,
            String activeIngredient,
            String presentation
    ) {
        MedicationPersistenceEntity entity = new MedicationPersistenceEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setCategory(category);
        entity.setActiveIngredient(activeIngredient);
        entity.setPresentation(presentation);
        return entity;
    }
}
