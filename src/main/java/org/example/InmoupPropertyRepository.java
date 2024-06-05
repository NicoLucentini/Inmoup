package org.example;

import org.example.entities.InmoupProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InmoupPropertyRepository extends JpaRepository<InmoupProperty, Integer> {
}
