package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepo extends JpaRepository<ResetToken, Integer>
{
}
