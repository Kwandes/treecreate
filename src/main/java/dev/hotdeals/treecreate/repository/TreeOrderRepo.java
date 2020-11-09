package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.TreeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TreeOrderRepo extends JpaRepository<TreeOrder, Integer>
{
    List<TreeOrder> findAllByUserId(int userId);
}
