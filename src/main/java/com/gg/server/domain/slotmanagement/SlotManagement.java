package com.gg.server.domain.slotmanagement;

import com.gg.server.global.utils.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class SlotManagement extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "past_slot_time")
    private Integer pastSlotTime;

    @NotNull
    @Column(name = "future_slot_time")
    private Integer futureSlotTime;

    @NotNull
    @Column(name = "open_minute")
    private Integer openMinute;

    @NotNull
    @Column(name = "game_interval")
    private Integer gameInterval;
}
