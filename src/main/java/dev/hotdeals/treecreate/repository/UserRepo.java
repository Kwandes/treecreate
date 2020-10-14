package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepo extends JpaRepository<User, Integer>
{
    List<User> findAllByEmail(String email);
}
