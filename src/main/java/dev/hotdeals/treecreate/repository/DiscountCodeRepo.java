package dev.hotdeals.treecreate.repository;

import dev.hotdeals.treecreate.model.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepo extends JpaRepository<DiscountCode, Integer>
{
    DiscountCode findObeByDiscountCode(String discountCode);
}
