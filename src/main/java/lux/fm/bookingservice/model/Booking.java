package lux.fm.bookingservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import lombok.Data;

@Data
@Entity

public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @ManyToOne
    User user;

    @Column(nullable = false)
    Status status = Status.PENDING;

    @Column(nullable = false)
    LocalDate checkIn = LocalDate.now();

    @Column(nullable = false)
    LocalDate checkOut;

    //TODO accommodation relation after implementation
}
