package com.gpsolutions.hotel.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
import com.gpsolutions.hotel.entity.Address;
import com.gpsolutions.hotel.entity.Amenity;
import com.gpsolutions.hotel.entity.ArrivalTime;
import com.gpsolutions.hotel.entity.Contact;
import com.gpsolutions.hotel.entity.Hotel;
import com.gpsolutions.hotel.repository.specification.HotelSpecification;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@TestPropertySource(properties = "spring.liquibase.contexts=test")
class HotelRepositoryTest {

  @Autowired private HotelRepository hotelRepository;

  @Autowired private TestEntityManager entityManager;

  @BeforeEach
  void setUp() {
    Amenity wifi = new Amenity();
    wifi.setName("Free WiFi");
    entityManager.persist(wifi);

    Amenity parking = new Amenity();
    parking.setName("Free parking");
    entityManager.persist(parking);

    Amenity pool = new Amenity();
    pool.setName("Swimming pool");
    entityManager.persist(pool);

    createHotel("DoubleTree by Hilton Minsk", "Hilton", "Minsk", "Belarus", Set.of(wifi, parking));
    createHotel("Minsk Marriott Hotel", "Marriott", "Moscow", "Russia", Set.of(wifi, pool));
    createHotel("Grodno Hotel", "LocalBrand", "Grodno", "Belarus", Set.of(parking));

    entityManager.flush();
    entityManager.clear();
  }

  @Test
  @DisplayName("filterBy: should find hotels by city and brand case-insensitively")
  void filterBy_whenCityAndBrandProvided_shouldReturnMatchingHotels() {
    // given
    HotelSearchRequest criteria = new HotelSearchRequest(null, "hilton", "MINSK", null, null);
    Specification<Hotel> spec = HotelSpecification.filterBy(criteria);

    // when
    List<Hotel> foundHotels = hotelRepository.findAll(spec);

    // then
    assertThat(foundHotels)
        .hasSize(1)
        .extracting(Hotel::getName)
        .containsExactly("DoubleTree by Hilton Minsk");
  }

  @Test
  @DisplayName("filterBy: should filter by amenities and eliminate duplicates via distinct")
  void filterBy_whenAmenitiesFilterMatchesMultiple_shouldNotReturnDuplicates() {
    // given
    HotelSearchRequest criteria =
        new HotelSearchRequest(null, null, null, null, List.of("Free WiFi", "Free parking"));
    Specification<Hotel> spec = HotelSpecification.filterBy(criteria);

    // when
    List<Hotel> foundHotels = hotelRepository.findAll(spec);

    // then
    assertThat(foundHotels).hasSize(3).extracting(Hotel::getId).doesNotHaveDuplicates();
  }

  @Test
  @DisplayName("countHotelsByCity: should correctly aggregate hotel count per city")
  void countHotelsByCity_shouldReturnAggregatedCounts() {
    // given - 1 hotel in Minsk, 1 in Grodno, 1 from Moscow(from setUp)

    // when
    List<Object[]> results = hotelRepository.countHotelsByCity();

    // then
    assertThat(results).hasSize(3);

    Map<String, Long> cityCounts =
        results.stream().collect(Collectors.toMap(row -> (String) row[0], row -> (Long) row[1]));

    assertThat(cityCounts)
        .containsExactlyInAnyOrderEntriesOf(
            Map.of(
                "Minsk", 1L,
                "Moscow", 1L,
                "Grodno", 1L));
  }

  // for cleaner code
  private void createHotel(
      String name, String brand, String city, String country, Set<Amenity> amenities) {
    Hotel hotel = new Hotel();
    hotel.setName(name);
    hotel.setBrand(brand);

    Address address = new Address();
    address.setHouseNumber(1);
    address.setStreet("Main St");
    address.setCity(city);
    address.setCountry(country); // теперь используется аргумент метода
    address.setPostCode("100000");
    address.setHotel(hotel);
    hotel.setAddress(address);

    Contact contact = new Contact();
    contact.setPhone("+375 00 000-00-00");
    contact.setEmail("hotel@test.com");
    contact.setHotel(hotel);
    hotel.setContact(contact);

    ArrivalTime arrivalTime = new ArrivalTime();
    arrivalTime.setCheckIn("14:00");
    arrivalTime.setHotel(hotel);
    hotel.setArrivalTime(arrivalTime);

    hotel.setAmenities(amenities);

    entityManager.persist(hotel);
  }
}
