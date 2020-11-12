package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepo extends JpaRepository<ResetToken, Integer>
{
    Optional<ResetToken> findByToken(String token);
}
