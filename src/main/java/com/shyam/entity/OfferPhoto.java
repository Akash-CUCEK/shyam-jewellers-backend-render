package com.shyam.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "offer_photo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offer_photo_seq")
    @SequenceGenerator(name = "offer_photo_seq", sequenceName = "offer_photo_seq", allocationSize = 1)
    private Integer id;

    @Column(name = "img_url1")
    private String imgUrl1;

    @Column(name = "img_url2")
    private String imgUrl2;

    @Column(name = "img_url3")
    private String imgUrl3;

    @Column(name = "img_url4")
    private String imgUrl4;

    @Column(name = "img_url5")
    private String imgUrl5;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}