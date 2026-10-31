package com.gpsolutions.hotel.repository;

import com.gpsolutions.hotel.entity.Hotel;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository
    extends JpaRepository<Hotel, Long>, JpaSpecificationExecutor<Hotel> {

  // GET /property-view/hotels/{id}:
  @EntityGraph(attributePaths = {"address", "contact", "arrivalTime", "amenities"})
  Optional<Hotel> findDetailedById(Long id);

  // GET /property-view/histogram/city
  @Query("SELECT a.city, COUNT(h) FROM Hotel h JOIN h.address a GROUP BY a.city")
  List<Object[]> countHotelsByCity();

  // GET /property-view/histogram/country
  @Query("SELECT a.country, COUNT(h) FROM Hotel h JOIN h.address a GROUP BY a.country")
  List<Object[]> countHotelsByCountry();

  // GET /property-view/histogram/brand
  @Query("SELECT h.brand, COUNT(h) FROM Hotel h GROUP BY h.brand")
  List<Object[]> countHotelsByBrand();

  // Для GET /property-view/histogram/amenities
  @Query("SELECT a.name, COUNT(h) FROM Hotel h JOIN h.amenities a GROUP BY a.name")
  List<Object[]> countHotelsByAmenity();
}
