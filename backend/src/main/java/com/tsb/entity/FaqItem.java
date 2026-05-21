package com.tsb.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "faq_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FaqItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column(length = 100)
    private String category;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;
}
