package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.FamilyTree;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyTreeRepo extends JpaRepository<FamilyTree, Integer>
{
    FamilyTree findOneByOwnerId(Integer id);
}
