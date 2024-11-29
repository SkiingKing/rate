package com.task.rate.repository;

import com.task.rate.model.CurrencyHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyHistoryRepository extends JpaRepository<CurrencyHistory, String> {
}
