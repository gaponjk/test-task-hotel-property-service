package com.gpsolutions.hotel.mapper;

import com.gpsolutions.hotel.dto.request.AddressRequest;
import com.gpsolutions.hotel.dto.request.ArrivalTimeRequest;
import com.gpsolutions.hotel.dto.request.ContactsRequest;
import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.response.AddressResponse;
import com.gpsolutions.hotel.dto.response.ArrivalTimeResponse;
import com.gpsolutions.hotel.dto.response.ContactsResponse;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.entity.Address;
import com.gpsolutions.hotel.entity.Amenity;
import com.gpsolutions.hotel.entity.ArrivalTime;
import com.gpsolutions.hotel.entity.Contact;
import com.gpsolutions.hotel.entity.Hotel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
public class HotelMapper {

    // GET /hotels, GET /search POST /hotels
    public HotelShortResponse toShortResponse(Hotel hotel) {
        if (hotel == null) {
            return null;
        }

        String formattedAddress = formatAddress(hotel.getAddress());
        String phone = hotel.getContact() != null ? hotel.getContact().getPhone() : null;

        return new HotelShortResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                formattedAddress,
                phone
        );
    }

    // GET /hotels/{id}
    public HotelDetailsResponse toDetailsResponse(Hotel hotel) {
        if (hotel == null) {
            return null;
        }

        List<String> amenityNames = hotel.getAmenities() != null
                ? hotel.getAmenities().stream()
                .map(Amenity::getName)
                .toList()
                : Collections.emptyList();

        return new HotelDetailsResponse(
                hotel.getId(),
                hotel.getName(),
                hotel.getDescription(),
                hotel.getBrand(),
                toAddressResponse(hotel.getAddress()),
                toContactsResponse(hotel.getContact()),
                toArrivalTimeResponse(hotel.getArrivalTime()),
                amenityNames
        );
    }

    //From DTO to Hotel
    public Hotel toEntity(CreateHotelRequest request) {
        if (request == null) {
            return null;
        }

        Hotel hotel = new Hotel();
        hotel.setName(request.name().strip());
        hotel.setDescription(request.description() != null ? request.description().strip() : null);
        hotel.setBrand(request.brand().strip());

        if (request.address() != null) {
            Address address = toAddressEntity(request.address());
            address.setHotel(hotel);
            hotel.setAddress(address);
        }

        if (request.contacts() != null) {
            Contact contact = toContactEntity(request.contacts());
            contact.setHotel(hotel);
            hotel.setContact(contact);
        }

        if (request.arrivalTime() != null) {
            ArrivalTime arrivalTime = toArrivalTimeEntity(request.arrivalTime());
            arrivalTime.setHotel(hotel);
            hotel.setArrivalTime(arrivalTime);
        }

        hotel.setAmenities(new HashSet<>());
        return hotel;
    }

    private AddressResponse toAddressResponse(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressResponse(
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getCountry(),
                address.getPostCode()
        );
    }

    private ContactsResponse toContactsResponse(Contact contact) {
        if (contact == null) {
            return null;
        }
        return new ContactsResponse(
                contact.getPhone(),
                contact.getEmail()
        );
    }

    private ArrivalTimeResponse toArrivalTimeResponse(ArrivalTime arrivalTime) {
        if (arrivalTime == null) {
            return null;
        }
        return new ArrivalTimeResponse(
                arrivalTime.getCheckIn(),
                arrivalTime.getCheckOut()
        );
    }

    private Address toAddressEntity(AddressRequest request) {
        if (request == null) {
            return null;
        }
        Address address = new Address();
        address.setHouseNumber(request.houseNumber());
        address.setStreet(request.street().strip());
        address.setCity(request.city().strip());
        address.setCountry(request.country().strip());
        address.setPostCode(request.postCode().strip());
        return address;
    }

    private Contact toContactEntity(ContactsRequest request) {
        if (request == null) {
            return null;
        }
        Contact contact = new Contact();
        contact.setPhone(request.phone().strip());
        contact.setEmail(request.email().strip());
        return contact;
    }

    private ArrivalTime toArrivalTimeEntity(ArrivalTimeRequest request) {
        if (request == null) {
            return null;
        }
        ArrivalTime arrivalTime = new ArrivalTime();
        arrivalTime.setCheckIn(request.checkIn().strip());
        arrivalTime.setCheckOut(request.checkOut() != null ? request.checkOut().strip() : null);
        return arrivalTime;
    }

    //for short hotel
    private String formatAddress(Address address) {
        if (address == null) {
            return null;
        }
        return String.format("%d %s, %s, %s, %s",
                address.getHouseNumber(),
                address.getStreet(),
                address.getCity(),
                address.getPostCode(),
                address.getCountry()
        );
    }
}