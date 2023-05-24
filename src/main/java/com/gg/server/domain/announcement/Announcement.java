package com.gg.server.domain.announcement;

import com.gg.server.admin.announcement.dto.AnnouncementAdminAddDto;
import com.gg.server.global.utils.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Builder
public class Announcement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "content", length=1000)
    private String content;
    @NotNull
    @Column(name = "creator_intra_id", length = 30)
    private String creatorIntraId;
    @Column(name = "deleter_intra_id", length = 30)
    private String deleterIntraId;
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public void update(String deleterIntraId, LocalDateTime deletedAt) {
        this.deleterIntraId = deleterIntraId;
        this.deletedAt = deletedAt;
    }

    static public Announcement from(AnnouncementAdminAddDto addDto) {
        return Announcement.builder()
                .content(addDto.getContent())
                .creatorIntraId(addDto.getCreatorIntraId())
                .build();
    }
}
