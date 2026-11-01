package com.martinatanasov.management.system.analytics;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface AnalyticsRepository extends JpaRepository<Analytics, Long> {

    List<Analytics> findAllByEmail(String email);

}
