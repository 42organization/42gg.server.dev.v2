package com.gg.server.domain.user.data;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class UserImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @Column(name = "image_uri")
    private String imageUri;

    @NotNull
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @NotNull
    @Column(name = "is_current")
    private Boolean isCurrent;

    public UserImage(User user, String imageUri, LocalDateTime createdAt, LocalDateTime deletedAt, Boolean isCurrent) {
        this.user = user;
        this.imageUri = imageUri;
        this.createdAt = createdAt;
        this.deletedAt = deletedAt;
        this.isCurrent = isCurrent;
    }

    public void updateIsCurrent() {
        this.isCurrent = !this.isCurrent;
    }

    public void updateDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
        updateIsCurrent();
    }
}