package com.gg.server.domain.userImage;

import com.gg.server.global.utils.BaseTimeEntity;
import com.sun.istack.NotNull;

import javax.persistence.*;
import java.io.Serializable;

public class UserImage extends BaseTimeEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne
    @Column(name = "user_id", length = 30)
    private String userId;

    @NotNull
    @Column(name = "image_url")
    private String imageUrl;

    public UserImage(String userId, String imageUrl) {
        this.userId = userId;
        this.imageUrl = imageUrl;
    }

    public String getImageId() {
        return imageUrl;
    }

    public String getuserId() {
        return userId;
    }

    public String getUserImageUrl() {
        return imageUrl;
    }

    public void imageUpdate(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}