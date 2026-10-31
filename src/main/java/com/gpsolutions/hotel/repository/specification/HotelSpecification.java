package com.gpsolutions.hotel.repository.specification;

import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
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

    public static Specification<Hotel> filterBy(HotelSearchRequest hotelSearchRequest) {
        return (root, query, criteriaBuilder) -> {
            if (hotelSearchRequest == null) {
                return criteriaBuilder.conjunction();
            }
            List<Predicate> predicates = new ArrayList<>();
            if (hotelSearchRequest.name() != null && !hotelSearchRequest.name().isBlank()) {
                predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + hotelSearchRequest.name().strip().toLowerCase() + "%"));
            }
            if (hotelSearchRequest.brand() != null && !hotelSearchRequest.brand().isBlank()) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("brand")), hotelSearchRequest.brand().strip().toLowerCase()));
            }
            boolean hasCity = hotelSearchRequest.city() != null && !hotelSearchRequest.city().isBlank();
            boolean hasCountry = hotelSearchRequest.country() != null && !hotelSearchRequest.country().isBlank();

            if (hasCity || hasCountry) {
                Join<Hotel, Address> addressJoin = root.join("address", JoinType.INNER);

                if (hasCity) {
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(addressJoin.get("city")), hotelSearchRequest.city().strip().toLowerCase()));
                }
                if (hasCountry) {
                    predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(addressJoin.get("country")), hotelSearchRequest.country().strip().toLowerCase()));
                }
            }

            if (hotelSearchRequest.amenities() != null && !hotelSearchRequest.amenities().isEmpty()) {
                if (query != null) {
                    query.distinct(true);
                }
                List<String> normalizedAmenities = hotelSearchRequest.amenities().stream()
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
