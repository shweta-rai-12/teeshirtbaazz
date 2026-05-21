package com.tsb.entity;

import com.tsb.entity.enums.CustomTshirtStatus;
import com.tsb.entity.enums.Size;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "custom_tshirt_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomTshirtRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Size size;

    @Column(nullable = false, length = 50)
    private String color;

    @Column(name = "custom_text", length = 255)
    private String customText;

    @Column(name = "logo_image_url", length = 500)
    private String logoImageUrl;

    @Column(name = "sample_image_url", length = 500)
    private String sampleImageUrl;

    @Column(length = 1000)
    private String notes;

    @Column(name = "estimated_price", precision = 10, scale = 2)
    private BigDecimal estimatedPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private CustomTshirtStatus status = CustomTshirtStatus.DRAFT;
}
