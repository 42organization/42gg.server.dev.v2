package com.gg.server.domain.tier.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Tier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_uri")
    private String imageUri;

    @Column(name = "name")
    private String name;

    public Tier(String imageUri) {
        this.imageUri = imageUri;
    }
}