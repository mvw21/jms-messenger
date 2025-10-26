package org.example.app;

import org.example.entities.Message;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface MessageRepository extends CrudRepository<Message, UUID> {
        Message findMessageByAuthorId(UUID authorId);

        @Query( value = "From Message m where m.author.id = :authorId ")
        List<Message> loadAllByAuthorId(@Param("authorId") UUID authorId, Pageable pageable);

        List<Message> findAllByAuthorIdAndRecipientId(UUID authorId,UUID recipientId,Pageable pageable);

//        @Query( value = "From Message m where m.author.id = :authorId and m.recipient.id = :recipientId")
//        List<Message> loadAllByAuthorId1(@Param("authorId") UUID authorId, Pageable pageable);


}
