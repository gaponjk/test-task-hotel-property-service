package com.gpsolutions.hotel.repository;

import com.gpsolutions.hotel.entity.Amenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Set;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    Set<Amenity> findByNameIn(Collection<String> names);
}
