package com.gpsolutions.hotel.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "hotels")
public class Hotel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Lob
  @Column(name = "description")
  private String description;

  @Column(name = "brand", nullable = false)
  private String brand;

  @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private Address address;

  @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private Contact contact;

  @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
  private ArrivalTime arrivalTime;

  @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinTable(
      name = "hotel_amenities",
      joinColumns = @JoinColumn(name = "hotel_id"),
      inverseJoinColumns = @JoinColumn(name = "amenity_id"))
  private final Set<Amenity> amenities = new HashSet<>();

  public Hotel() {}

  public Set<Amenity> getAmenities() {
    return amenities;
  }

  public void setAmenities(Set<Amenity> amenities) {
    this.amenities.clear();
    if (amenities != null) {
      this.amenities.addAll(amenities);
    }
  }

  public void addAmenity(Amenity amenity) {
    if (amenity != null) {
      this.amenities.add(amenity);
      amenity.getHotels().add(this);
    }
  }

  public void removeAmenity(Amenity amenity) {
    if (amenity != null) {
      this.amenities.remove(amenity);
      amenity.getHotels().remove(this);
    }
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
    if (address != null) {
      address.setHotel(this);
    }
  }

  public Contact getContact() {
    return contact;
  }

  public void setContact(Contact contact) {
    this.contact = contact;
    if (contact != null) {
      contact.setHotel(this);
    }
  }

  public ArrivalTime getArrivalTime() {
    return arrivalTime;
  }

  public void setArrivalTime(ArrivalTime arrivalTime) {
    this.arrivalTime = arrivalTime;
    if (arrivalTime != null) {
      arrivalTime.setHotel(this);
    }
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true; // They will be equal in case of uninitialized ID
    if (!(o instanceof Hotel hotel)) return false;
    return this.getId() != null && this.getId().equals(hotel.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
