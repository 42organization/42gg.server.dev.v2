package gg.pingpong.api.admin.noti.controller.response;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

import gg.pingpong.api.admin.noti.dto.NotiAdminDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotiListAdminResponseDto {
	private List<NotiAdminDto> notiList;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Integer totalPage;

	@Override
	public String toString() {
		return "NotiListResponseDto{"
			+ "notifications=" + notiList
			+ '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		NotiListAdminResponseDto other = (NotiListAdminResponseDto)obj;
		return Objects.equals(notiList, other.notiList);
	}
}
