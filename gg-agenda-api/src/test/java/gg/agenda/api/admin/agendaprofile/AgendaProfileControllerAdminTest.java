package gg.agenda.api.admin.agendaprofile;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.admin.repo.agenda.AgendaProfileAdminRepository;
import gg.agenda.api.AgendaMockData;
import gg.agenda.api.admin.agendaprofile.controller.request.AgendaProfileChangeAdminReqDto;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.type.Location;
import gg.data.user.User;
import gg.data.user.type.RoleType;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
public class AgendaProfileControllerAdminTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private AgendaMockData agendaMockData;
	@Autowired
	private AgendaProfileAdminRepository agendaProfileAdminRepository;
	User user;
	String accessToken;

	@Nested
	@DisplayName("개인 프로필 정보 변경")
	class UpdateAgendaProfile {
		@BeforeEach
		void beforeEach() {
			user = testDataUtils.createNewAdminUser(RoleType.ADMIN);
			accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		}

		@Test
		@DisplayName("유효한 정보로 개인 프로필을 변경합니다.")
		void updateProfileWithValidData() throws Exception {
			// Given
			AgendaProfile agendaProfile = agendaMockData.createAgendaProfile(user, SEOUL);
			agendaMockData.createTicket(agendaProfile);
			AgendaProfileChangeAdminReqDto requestDto = new AgendaProfileChangeAdminReqDto("Valid user content",
				"https://github.com/validUser", "SEOUL");
			String content = objectMapper.writeValueAsString(requestDto);
			// When
			mockMvc.perform(patch("/agenda/admin/profile")
					.param("intraId", user.getIntraId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isNoContent());
			// Then
			AgendaProfile result = agendaProfileAdminRepository.findByIntraId(user.getIntraId()).orElseThrow(null);
			assertThat(result.getContent()).isEqualTo(requestDto.getUserContent());
			assertThat(result.getGithubUrl()).isEqualTo(requestDto.getUserGithub());
			assertThat(result.getLocation().name()).isEqualTo(requestDto.getUserLocation());
		}

		@Test
		@DisplayName("ENUM 이외의 지역 정보가 들어온 경우 MIX로 저장합니다.")
		void updateProfileWithInvalidLocation() throws Exception {
			// Given
			agendaMockData.createAgendaProfile(user, SEOUL);
			AgendaProfileChangeAdminReqDto requestDto = new AgendaProfileChangeAdminReqDto("Valid user content",
				"https://github.com/validUser", "INVALID_LOCATION");
			String content = objectMapper.writeValueAsString(requestDto);

			// When
			mockMvc.perform(patch("/agenda/admin/profile")
					.param("intraId", user.getIntraId())
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isNoContent());

			// Then
			AgendaProfile result = agendaProfileAdminRepository.findByIntraId(user.getIntraId()).orElseThrow();
			assertThat(result.getLocation()).isEqualTo(Location.MIX);
		}

		@Test
		@DisplayName("userContent 없이 개인 프로필을 변경합니다.")
		void updateProfileWithoutUserContent() throws Exception {
			// Given
			AgendaProfileChangeAdminReqDto requestDto = new AgendaProfileChangeAdminReqDto("",
				"https://github.com/validUser", "SEOUL");
			String content = objectMapper.writeValueAsString(requestDto);
			// When & Then
			mockMvc.perform(patch("/agenda/admin/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("잘못된 형식의 userGithub로 개인 프로필을 변경합니다.")
		void updateProfileWithInvalidUserGithub() throws Exception {
			// Given
			AgendaProfileChangeAdminReqDto requestDto = new AgendaProfileChangeAdminReqDto("Valid user content",
				"invalidGithubUrl", "SEOUL");
			String content = objectMapper.writeValueAsString(requestDto);
			// When & Then
			mockMvc.perform(patch("/agenda/admin/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("userContent가 허용된 길이를 초과하여 개인 프로필을 변경합니다.")
		void updateProfileWithExceededUserContentLength() throws Exception {
			// Given
			String longContent = "a".repeat(1001); // Assuming the limit is 1000 characters
			AgendaProfileChangeAdminReqDto requestDto = new AgendaProfileChangeAdminReqDto(longContent,
				"https://github.com/validUser", "SEOUL");
			String content = objectMapper.writeValueAsString(requestDto);
			// When & Then
			mockMvc.perform(patch("/agenda/admin/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}

		@Test
		@DisplayName("userGithub가 허용된 길이를 초과하여 개인 프로필을 변경합니다.")
		void updateProfileWithExceededUserGithubLength() throws Exception {
			// Given
			String longGithubUrl = "https://github.com/" + "a".repeat(256); // Assuming the limit is 255 characters
			AgendaProfileChangeAdminReqDto requestDto = new AgendaProfileChangeAdminReqDto("Valid user content",
				longGithubUrl, "SEOUL");

			String content = objectMapper.writeValueAsString(requestDto);

			// When & Then
			mockMvc.perform(patch("/agenda/admin/profile")
					.header("Authorization", "Bearer " + accessToken)
					.contentType(MediaType.APPLICATION_JSON)
					.content(content))
				.andExpect(status().isBadRequest());
		}
	}

}



