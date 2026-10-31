package com.gpsolutions.hotel.repository.specification;

import com.gpsolutions.hotel.entity.Address;
import com.gpsolutions.hotel.entity.Amenity;
import com.gpsolutions.hotel.entity.Hotel;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
//
public final class HotelSpecification {

    private HotelSpecification() {
    }

    public static Specification<Hotel> filterBy(HotelSearchCriteria hotelSearchCriteria) {
        return (root, query, criteriaBuilder) -> {
            if (hotelSearchCriteria == null) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            if (hotelSearchCriteria.name() != null && !hotelSearchCriteria.name().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + hotelSearchCriteria.name().strip().toLowerCase() + "%"));
            }
            if (hotelSearchCriteria.brand() != null && !hotelSearchCriteria.brand().isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("brand")), hotelSearchCriteria.brand().strip().toLowerCase()));
            }
            boolean hasCity = hotelSearchCriteria.city() != null && !hotelSearchCriteria.city().isBlank();
            boolean hasCountry = hotelSearchCriteria.country() != null && !hotelSearchCriteria.country().isBlank();

            if (hasCity || hasCountry) {
                Join<Hotel, Address> addressJoin = root.join("address", JoinType.INNER);

                if (hasCity) {
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(addressJoin.get("city")), hotelSearchCriteria.city().strip().toLowerCase()));
                }
                if (hasCountry) {
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(addressJoin.get("country")), hotelSearchCriteria.country().strip().toLowerCase()));
                }
            }

            if (hotelSearchCriteria.amenities() != null && !hotelSearchCriteria.amenities().isEmpty()) {
                if (query != null) {
                    query.distinct(true);
                }
                List<String> normalizedAmenities = hotelSearchCriteria.amenities().stream()
                        .filter(Objects::nonNull)
                        .map(String::strip)
                        .map(String::toLowerCase)
                        .filter(s -> !s.isBlank())
                        .toList();

                if (!normalizedAmenities.isEmpty()) {
                    Join<Hotel, Amenity> amenityJoin = root.join("amenities", JoinType.INNER);
                    predicates.add(criteriaBuilder.lower(amenityJoin.get("name")).in(normalizedAmenities));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
