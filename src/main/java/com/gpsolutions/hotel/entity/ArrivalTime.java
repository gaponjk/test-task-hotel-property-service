package com.gpsolutions.hotel.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "arrival_times")
public class ArrivalTime {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hotel_id", referencedColumnName = "id", nullable = false, unique = true)
  private Hotel hotel;

  @Column(name = "check_in", nullable = false, length = 10)
  private String checkIn;

  @Column(name = "check_out", length = 10)
  private String checkOut;

  public ArrivalTime() {}

  public String getCheckOut() {
    return checkOut;
  }

  public void setCheckOut(String checkOut) {
    this.checkOut = checkOut;
  }

  public String getCheckIn() {
    return checkIn;
  }

  public void setCheckIn(String checkIn) {
    this.checkIn = checkIn;
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
    if (!(o instanceof ArrivalTime arrivalTime)) return false;
    return this.getId() != null && this.getId().equals(arrivalTime.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
