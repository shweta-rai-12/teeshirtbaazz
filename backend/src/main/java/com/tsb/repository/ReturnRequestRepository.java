package com.tsb.repository;

import com.tsb.model.ReturnRequest;
import com.tsb.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> {
    List<ReturnRequest> findByUser(User user);
}
