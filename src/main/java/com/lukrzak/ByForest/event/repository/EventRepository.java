package com.lukrzak.ByForest.event.repository;

import com.lukrzak.ByForest.event.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

	List<Event> findAllByNameLike(String name);

}
