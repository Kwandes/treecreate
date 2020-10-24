package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.TreeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeOrderRepo extends JpaRepository<TreeOrder, Integer>
{
}
