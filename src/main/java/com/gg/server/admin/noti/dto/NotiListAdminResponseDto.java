package com.gg.server.admin.noti.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiListAdminResponseDto {
    private List<NotiAdminDto> notiList;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalPage;
    @Override
    public String toString() {
        return "NotiListResponseDto{" +
                "notifications=" + notiList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotiListAdminResponseDto other = (NotiListAdminResponseDto) o;
        return Objects.equals(notiList, other.notiList);
    }
}