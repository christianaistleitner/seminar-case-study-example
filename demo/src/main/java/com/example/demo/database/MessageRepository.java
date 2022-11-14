package com.example.demo.database;

import com.example.demo.database.models.Message;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long> {
    List<Message> findAllByDateSentIsAfter(OffsetDateTime timestamp);
}
