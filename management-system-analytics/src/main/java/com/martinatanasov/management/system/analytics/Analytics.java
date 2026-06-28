package com.martinatanasov.management.system.analytics;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "analytics")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(name = "event_identifier", nullable = false, updatable = false, length = 50)
    private String eventIdentifier;

    @Column(name = "platform_id", nullable = false, updatable = false, length = 4, columnDefinition = "CHAR(4)")
    private String platformId;

    @Column(name = "created_date", updatable = false, nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

}
