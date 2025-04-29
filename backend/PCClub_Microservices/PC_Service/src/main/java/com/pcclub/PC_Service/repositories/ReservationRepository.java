package com.pcclub.PC_Service.repositories;

import com.pcclub.PC_Service.models.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByPcIdAndReservedFromLessThanEqualAndReservedToGreaterThanEqual(Long pcId, Date reservedTo, Date reservedFrom);
    Optional<Reservation> findByPcIdAndReservedFromAndReservedTo(Long pcId, Date reservedFrom, Date reservedTo);
    List<Reservation> findByPcId(Long pcId);
    @Query("SELECT r FROM Reservation r WHERE r.pcId = :pcId AND r.reservedFrom < :reservedTo AND r.reservedTo > :reservedFrom")
    List<Reservation> findOverlappingReservations(@Param("pcId") Long pcId,
                                                  @Param("reservedFrom") Date reservedFrom,
                                                  @Param("reservedTo") Date reservedTo);
}
