package dev.hotdeals.treecreate.repository;

import com.sun.source.tree.Tree;
import dev.hotdeals.treecreate.model.TreeOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TreeOrderRepo extends JpaRepository<TreeOrder, Integer>
{
    List<TreeOrder> findAllByUserId(int userId);
    Optional<TreeOrder> findByTreeDesignId(int designId);
}
