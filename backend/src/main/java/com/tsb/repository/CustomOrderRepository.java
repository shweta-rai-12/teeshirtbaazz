package com.tsb.repository;

import com.tsb.model.CustomOrder;
import com.tsb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomOrderRepository extends JpaRepository<CustomOrder, Long> {
    List<CustomOrder> findByUser(User user);
}
