package lux.fm.bookingservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lux.fm.bookingservice.dto.accommodation.AccommodationDto;
import lux.fm.bookingservice.dto.accommodation.CreateAccommodationRequestDto;
import lux.fm.bookingservice.service.AccommodationService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Accommodation management", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/accommodations")
@Validated
public class AccommodationController {
    private final AccommodationService accommodationService;

    @Operation(summary = "Create a new accommodation",
            description = "Add a new accommodation to DB")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public AccommodationDto save(
            @Valid @RequestBody CreateAccommodationRequestDto accommodationRequestDto) {
        return accommodationService.save(accommodationRequestDto);
    }

    @Operation(summary = "Get all accommodations", description = "Get a list of all"
            + "accommodations, could be divided into pages")
    @GetMapping
    public List<AccommodationDto> getAll(@ParameterObject @PageableDefault Pageable pageable) {
        return accommodationService.findAll(pageable);
    }

    @Operation(summary = "Get an accommodation by id", description =
            "Get exact accommodation by id")
    @GetMapping("/{id}")
    public AccommodationDto getBookById(@Positive @PathVariable Long id) {
        return accommodationService.findById(id);
    }

    @Operation(summary = "Update an accommodation", description =
            "Update information about an accommodation by id")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public AccommodationDto updateById(
            @Positive @PathVariable Long id,
            @Valid @RequestBody CreateAccommodationRequestDto accommodationRequestDto) {
        return accommodationService.updateById(id, accommodationRequestDto);
    }

    @Operation(summary = "Delete an accommodation", description = "Delete an accommodation by id")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@Positive @PathVariable Long id) {
        accommodationService.deleteById(id);
    }
}
