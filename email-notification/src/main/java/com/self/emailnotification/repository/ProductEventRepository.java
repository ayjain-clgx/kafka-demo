package com.self.emailnotification.repository;

import com.self.emailnotification.entity.ProductEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductEventRepository extends JpaRepository<ProductEventEntity, Long> {
    List<ProductEventEntity> findByMessageId(String messageId);
}
