package com.gpsolutions.hotel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "contacts")
public class Contact {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", referencedColumnName = "id", nullable = false, unique = true)
  private Hotel hotel;

  @Column(name = "phone", nullable = false)
  private String phone;

  @Column(name = "email", nullable = false)
  private String email;

  public Contact() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public Hotel getHotel() {
    return hotel;
  }

  public void setHotel(Hotel hotel) {
    this.hotel = hotel;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Contact contact)) return false;
    return this.getId() != null && this.getId().equals(contact.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
