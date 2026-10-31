package com.gpsolutions.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gpsolutions.hotel.dto.request.AddressRequest;
import com.gpsolutions.hotel.dto.request.ArrivalTimeRequest;
import com.gpsolutions.hotel.dto.request.ContactsRequest;
import com.gpsolutions.hotel.dto.request.CreateHotelRequest;
import com.gpsolutions.hotel.dto.request.HotelSearchRequest;
import com.gpsolutions.hotel.dto.response.AddressResponse;
import com.gpsolutions.hotel.dto.response.ArrivalTimeResponse;
import com.gpsolutions.hotel.dto.response.ContactsResponse;
import com.gpsolutions.hotel.dto.response.HotelDetailsResponse;
import com.gpsolutions.hotel.dto.response.HotelShortResponse;
import com.gpsolutions.hotel.exception.BadRequestException;
import com.gpsolutions.hotel.exception.GlobalExceptionHandler;
import com.gpsolutions.hotel.exception.NotFoundException;
import com.gpsolutions.hotel.service.HotelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HotelController.class)
@Import(GlobalExceptionHandler.class)
class HotelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private HotelService hotelService;

    @Test
    @DisplayName("GET /property-view/hotels: should return 200 OK and list of hotels")
    void getAllHotels_shouldReturn200AndList() throws Exception {
        // given
        HotelShortResponse hotel = new HotelShortResponse(
                1L,
                "DoubleTree by Hilton Minsk",
                "Luxurious hotel",
                "9 Pobediteley Avenue, Minsk, 220004, Belarus",
                "+375 17 309-80-00"
        );
        given(hotelService.getAllHotels()).willReturn(List.of(hotel));

        // when & then
        mockMvc.perform(get("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("DoubleTree by Hilton Minsk")))
                .andExpect(jsonPath("$[0].address", is("9 Pobediteley Avenue, Minsk, 220004, Belarus")))
                .andExpect(jsonPath("$[0].phone", is("+375 17 309-80-00")));

        verify(hotelService).getAllHotels();
    }

    @Test
    @DisplayName("GET /property-view/hotels/{id}: when hotel exists should return 200 OK and detailed hotel")
    void getHotelById_whenFound_shouldReturn200AndDetails() throws Exception {
        // given
        Long hotelId = 1L;
        HotelDetailsResponse response = new HotelDetailsResponse(
                hotelId,
                "DoubleTree by Hilton Minsk",
                "Luxurious hotel",
                "Hilton",
                new AddressResponse(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
                new ContactsResponse("+375 17 309-80-00", "info@hilton.com"),
                new ArrivalTimeResponse("14:00", "12:00"),
                List.of("Free parking", "Free WiFi")
        );
        given(hotelService.getHotelById(hotelId)).willReturn(response);

        // when & then
        mockMvc.perform(get("/property-view/hotels/{id}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("DoubleTree by Hilton Minsk")))
                .andExpect(jsonPath("$.brand", is("Hilton")))
                .andExpect(jsonPath("$.address.city", is("Minsk")))
                .andExpect(jsonPath("$.contacts.phone", is("+375 17 309-80-00")))
                .andExpect(jsonPath("$.amenities", containsInAnyOrder("Free parking", "Free WiFi")));

        verify(hotelService).getHotelById(hotelId);
    }

    @Test
    @DisplayName("GET /property-view/hotels/{id}: when hotel not found should return 404 Not Found")
    void getHotelById_whenNotFound_shouldReturn404() throws Exception {
        // given
        Long hotelId = 999L;
        given(hotelService.getHotelById(hotelId))
                .willThrow(new NotFoundException("Hotel not found with id: " + hotelId));

        // when & then
        mockMvc.perform(get("/property-view/hotels/{id}", hotelId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", is("Hotel not found with id: 999")));

        verify(hotelService).getHotelById(hotelId);
    }

    @Test
    @DisplayName("GET /property-view/search: should pass query parameters and return 200 OK")
    void searchHotels_shouldReturn200AndFilteredList() throws Exception {
        // given
        HotelShortResponse hotel = new HotelShortResponse(
                1L,
                "DoubleTree by Hilton Minsk",
                "Desc",
                "Address",
                "+375 17 309-80-00"
        );
        given(hotelService.searchHotels(any(HotelSearchRequest.class))).willReturn(List.of(hotel));

        // when & then
        mockMvc.perform(get("/property-view/search")
                        .param("city", "Minsk")
                        .param("brand", "Hilton")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("DoubleTree by Hilton Minsk")));

        verify(hotelService).searchHotels(any(HotelSearchRequest.class));
    }

    @Test
    @DisplayName("POST /property-view/hotels: should create hotel and return 201 Created")
    void createHotel_whenValidRequest_shouldReturn201() throws Exception {
        // given
        CreateHotelRequest request = new CreateHotelRequest(
                "DoubleTree by Hilton Minsk",
                "Description",
                "Hilton",
                new AddressRequest(9, "Pobediteley Avenue", "Minsk", "Belarus", "220004"),
                new ContactsRequest("+375 17 309-80-00", "info@hilton.com"),
                new ArrivalTimeRequest("14:00", "12:00")
        );

        HotelShortResponse response = new HotelShortResponse(
                1L,
                "DoubleTree by Hilton Minsk",
                "Description",
                "9 Pobediteley Avenue, Minsk, 220004, Belarus",
                "+375 17 309-80-00"
        );

        given(hotelService.createHotel(any(CreateHotelRequest.class))).willReturn(response);

        // when & then
        mockMvc.perform(post("/property-view/hotels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("DoubleTree by Hilton Minsk")))
                .andExpect(jsonPath("$.phone", is("+375 17 309-80-00")));

        verify(hotelService).createHotel(any(CreateHotelRequest.class));
    }

    @Test
    @DisplayName("POST /property-view/hotels/{id}/amenities: should attach amenities and return 200 OK")
    void addAmenities_shouldReturn200() throws Exception {
        // given
        Long hotelId = 1L;
        List<String> amenities = List.of("Free parking", "Free WiFi");
        doNothing().when(hotelService).addAmenities(eq(hotelId), eq(amenities));

        // when & then
        mockMvc.perform(post("/property-view/hotels/{id}/amenities", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(amenities)))
                .andExpect(status().isOk());

        verify(hotelService).addAmenities(eq(hotelId), eq(amenities));
    }

    @Test
    @DisplayName("GET /property-view/histogram/{param}: when param is valid should return 200 OK and map")
    void getHistogram_whenValidParam_shouldReturn200() throws Exception {
        // given
        given(hotelService.getHistogram("city")).willReturn(Map.of("Minsk", 2L, "Grodno", 1L));

        // when & then
        mockMvc.perform(get("/property-view/histogram/{param}", "city")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Minsk", is(2)))
                .andExpect(jsonPath("$.Grodno", is(1)));

        verify(hotelService).getHistogram("city");
    }

    @Test
    @DisplayName("GET /property-view/histogram/{param}: when param is invalid should return 400 Bad Request")
    void getHistogram_whenInvalidParam_shouldReturn400() throws Exception {
        // given
        given(hotelService.getHistogram("invalid"))
                .willThrow(new BadRequestException("Invalid histogram parameter: 'invalid'"));

        // when & then
        mockMvc.perform(get("/property-view/histogram/{param}", "invalid")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.error", is("Bad Request")))
                .andExpect(jsonPath("$.message", is("Invalid histogram parameter: 'invalid'")));

        verify(hotelService).getHistogram("invalid");
    }
}