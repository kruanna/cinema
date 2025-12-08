package ru.cinema.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.cinema.model.Session;

import java.time.LocalDate;
import java.util.List;

public interface SessionRepository extends JpaRepository<Session, Long> {

    List<Session> findByDate(LocalDate date);

    List<Session> findByMovie_TitleContainingIgnoreCase(String title);

    List<Session> findAllByOrderByTimeAsc();

    @Query("SELECT SUM(s.price) FROM Session s")
    Double getTotalRevenue();
}

