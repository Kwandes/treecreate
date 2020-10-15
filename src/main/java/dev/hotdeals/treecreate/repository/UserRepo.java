package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<User, Integer>
{
    User findOneByEmail(String email);
}
