package org.example.app;

import org.example.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface UserRepository extends CrudRepository<User, UUID> {

    @Query( value = "select u from User u where lower(u.username) like lower(concat('%',:username,'%')) ") //%username%
    List<User> loadByName(@Param("username") String username, Pageable pageable);

    @Query("select u from User u where u.email = :email")
    Optional<User> loadByEmail(@Param("email") String email);

}
