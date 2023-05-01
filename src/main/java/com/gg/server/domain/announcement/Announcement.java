package com.gg.server.domain.announcement;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class Announcement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull
    @Column(name = "content", length=10000)
    private String content;

    @NotNull
    @Column(name = "is_del")
    private Boolean isDel;

    @NotNull
    @Column(name = "creator_intra_id")
    private String creatorIntraId;


    @Column(name = "deleter_intra_id")
    private String deleterIntraId;

    @NotNull
    @Column(name = "created_time")
    private LocalDateTime createdTime;

    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;

    @Builder
    Announcement(String content, String creatorIntraId, LocalDateTime createdTime) {
        this.content = content;
        this.creatorIntraId = creatorIntraId;
        this.deleterIntraId = null;
        this.createdTime = createdTime;
        this.deletedTime = null;
        this.isDel = false;
    }

    public void update(String deleterIntraId, LocalDateTime deletedTime) {
        this.deleterIntraId = deleterIntraId;
        this.deletedTime = deletedTime;
        this.isDel = true;
    }
}
