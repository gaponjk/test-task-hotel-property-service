package com.gpsolutions.hotel.repository;

import com.gpsolutions.hotel.entity.Amenity;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Long> {

  Set<Amenity> findByNameIn(Collection<String> names);
}
