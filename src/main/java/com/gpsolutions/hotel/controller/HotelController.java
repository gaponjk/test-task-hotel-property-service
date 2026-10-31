package com.gpsolutions.hotel.controller;

import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
import com.gpsolutions.hotel.dto.response.ErrorResponse;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/property-view")
@Tag(name = "Hotel Controller", description = "Operations for hotel management, searching, and analytics")
public class HotelController {

  private final HotelService hotelService;

  public HotelController(HotelService hotelService) {
    this.hotelService = hotelService;
  }

  @Operation(summary = "Get list of all hotels with short information")
  @ApiResponse(
          responseCode = "200",
          description = "Hotels successfully retrieved",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelShortResponse.class))))
  @GetMapping("/hotels")
  public List<HotelShortResponse> getAllHotels() {
    return hotelService.getAllHotels();
  }

  @Operation(summary = "Get detailed hotel information by ID")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Hotel details successfully retrieved",
                  content = @Content(schema = @Schema(implementation = HotelDetailsResponse.class))),
          @ApiResponse(
                  responseCode = "404",
                  description = "Hotel not found with the specified ID",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/hotels/{id}")
  public HotelDetailsResponse getHotelById(
          @Parameter(description = "Unique identifier of the hotel", example = "1")
          @PathVariable Long id) {
    return hotelService.getHotelById(id);
  }

  @Operation(summary = "Search hotels by filter parameters (name, brand, city, country, amenities)")
  @ApiResponse(
          responseCode = "200",
          description = "Filtered list of hotels",
          content = @Content(array = @ArraySchema(schema = @Schema(implementation = HotelShortResponse.class))))
  @GetMapping("/search")
  public List<HotelShortResponse> searchHotels(@ModelAttribute HotelSearchRequest request) {
    return hotelService.searchHotels(request);
  }

  @Operation(summary = "Create a new hotel")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "201",
                  description = "Hotel successfully created",
                  content = @Content(schema = @Schema(implementation = HotelShortResponse.class))),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid request payload or validation errors",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/hotels")
  @ResponseStatus(HttpStatus.CREATED)
  public HotelShortResponse createHotel(@Valid @RequestBody CreateHotelRequest request) {
    return hotelService.createHotel(request);
  }

  @Operation(summary = "Add a list of amenities to an existing hotel")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Amenities successfully attached to the hotel"),
          @ApiResponse(
                  responseCode = "404",
                  description = "Hotel not found",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @PostMapping("/hotels/{id}/amenities")
  @ResponseStatus(HttpStatus.OK)
  public void addAmenities(
          @Parameter(description = "Unique identifier of the hotel", example = "1")
          @PathVariable Long id,
          @RequestBody List<String> amenities) {
    hotelService.addAmenities(id, amenities);
  }

  @Operation(summary = "Get hotel count histogram grouped by specified parameter (brand, city, country, amenities)")
  @ApiResponses({
          @ApiResponse(
                  responseCode = "200",
                  description = "Histogram successfully calculated"),
          @ApiResponse(
                  responseCode = "400",
                  description = "Unsupported histogram parameter provided",
                  content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
  @GetMapping("/histogram/{param}")
  public Map<String, Long> getHistogram(
          @Parameter(description = "Grouping parameter: brand, city, country, amenities", example = "city")
          @PathVariable String param) {
    return hotelService.getHistogram(param);
  }
}