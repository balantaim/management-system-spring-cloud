package com.martinatanasov.management.system.analytics;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "analytics")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Analytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false, updatable = false, length = 36)
    private String userId;

}
