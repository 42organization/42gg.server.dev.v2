package gg.recruit.api.user.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.data.recruit.application.Application;
import gg.data.recruit.application.ApplicationAnswer;
import gg.data.recruit.application.enums.ApplicationStatus;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import gg.data.user.User;
import gg.recruit.api.RecruitMockData;
import gg.recruit.api.user.controller.request.CheckListFormRequest;
import gg.recruit.api.user.controller.request.FormPatchRequestDto;
import gg.recruit.api.user.controller.request.FormRequest;
import gg.recruit.api.user.controller.request.RecruitApplyFormListReqDto;
import gg.recruit.api.user.controller.request.RecruitApplyFormReqDto;
import gg.recruit.api.user.controller.request.TextFormRequest;
import gg.recruit.api.user.controller.response.ApplicationResultResDto;
import gg.recruit.api.user.controller.response.MyApplicationsResDto;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;

@IntegrationTest
@Transactional
@AutoConfigureMockMvc
class ApplicationControllerTest {

	@Autowired
	private RecruitMockData recruitMockData;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET /recruitments/applications -> 200 OK TEST")
	public void getMyApplicationsTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitment recruitment = recruitMockData.createRecruitment();
		Recruitment recruitment2 = recruitMockData.createRecruitment();
		Recruitment recruitment3 = recruitMockData.createRecruitment();

		recruitMockData.createApplication(user, recruitment);
		recruitMockData.createApplication(user, recruitment2);
		recruitMockData.createApplication(user, recruitment3);

		//when
		String res = mockMvc.perform(get(("/recruitments/applications"))
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		//then
		MyApplicationsResDto myApplicationsResDto = objectMapper.readValue(res, MyApplicationsResDto.class);
		assertEquals(3, myApplicationsResDto.getApplications().size());
	}

	@Test
	@DisplayName("POST /recruitments/{recruitmentId}/applications -> 201 CREATED TEST")
	void saveApplicationTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);

		Recruitment recruitment = recruitMockData.createRecruitment();
		Question q1 = recruitMockData.createQuestion(recruitment);
		Question q2 = recruitMockData.createQuestion(recruitment);
		Question q3 = recruitMockData.createQuestion(recruitment);

		List<RecruitApplyFormReqDto> forms = List.of(new RecruitApplyFormReqDto(q1.getId(), "답변 1"),
			new RecruitApplyFormReqDto(q2.getId(), "답변 2"),
			new RecruitApplyFormReqDto(q3.getId(), "답변 3"));
		RecruitApplyFormListReqDto req = new RecruitApplyFormListReqDto(forms);
		String content = objectMapper.writeValueAsString(req);

		//when
		String res = mockMvc.perform(post(("/recruitments/" + recruitment.getId() + "/applications"))
				.header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")
				.content(content))
			.andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

		//then
	}

	@Test
	@DisplayName("GET /recruitments/{recruitmentId}/applications/{applicationId}/result -> 200 OK TEST")
	public void applicationResApiTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		Recruitment recruitment = recruitMockData.createRecruitment();
		Application application = recruitMockData.createApplication(user, recruitment);
		recruitMockData.createRecruitStatus(application);

		//when
		String url = "/recruitments/" + recruitment.getId()
			+ "/applications/" + application.getId() + "/result";

		String res = mockMvc.perform(get(url)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

		ApplicationResultResDto applicationResultResDto = objectMapper
			.readValue(res, ApplicationResultResDto.class);

		assertEquals(recruitment.getTitle(), applicationResultResDto.getTitle());
		assertEquals(ApplicationStatus.PROGRESS_DOCS, applicationResultResDto.getStatus());
	}

	@Test
	@DisplayName("DELETE /recruitments/{recruitmentId}/applications/{applicationId} -> 204 NO_CONTENT TEST")
	void deleteApplicationTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		Recruitment recruitment = recruitMockData.createRecruitment();
		Application application = recruitMockData.createApplication(user, recruitment);

		//when
		String url = "/recruitments/" + recruitment.getId()
			+ "/applications/" + application.getId();

		mockMvc.perform(delete(url)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNoContent());
	}

	@Test
	@DisplayName("DELETE /recruitments/{recruitmentId}/applications/{applicationId} -> 403 FORBIDDEN TEST")
	void deleteApplicationForbiddenTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		Recruitment recruitment = recruitMockData.createRecruitmentEnd();
		Application application = recruitMockData.createApplication(user, recruitment);

		//when, then
		String url = "/recruitments/" + recruitment.getId()
			+ "/applications/" + application.getId();

		mockMvc.perform(delete(url)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("DELETE /recruitments/{recruitmentId}/applications/{applicationId} -> 404 NOT_FOUND TEST")
	void deleteApplicationNotFoundTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		Recruitment recruitment = recruitMockData.createRecruitmentDel();
		Application application = recruitMockData.createApplication(user, recruitment);

		//when, then
		String url = "/recruitments/" + recruitment.getId()
			+ "/applications/" + application.getId() + 1;

		mockMvc.perform(delete(url)
				.header("Authorization", "Bearer " + accessToken))
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("PATCH /recruitments/{recruitmentId}/applications/{applicationId} -> 204 NO_CONTENT TEST")
	void patchApplicationTest() throws Exception {
		//given
		User user = testDataUtils.createNewUser();
		String accessToken = testDataUtils.getLoginAccessTokenFromUser(user);
		Recruitment recruitment = recruitMockData.createRecruitment();
		Question question1 = recruitMockData.createQuestion(recruitment, "질문 1", InputType.TEXT);
		Question question2 = recruitMockData.createQuestion(recruitment, "질문 2", InputType.SINGLE_CHECK,
			"선택지 1", "선택지 2");

		Application application = recruitMockData.createApplication(user, recruitment);
		recruitMockData.makeAnswer(application, question1, "답변 1");
		recruitMockData.makeAnswer(application, question2, question2.getCheckLists().get(0).getId());

		//when
		List<FormRequest> forms = new ArrayList<>();
		String newAnswerText = "새로운 답변";
		FormRequest newTextForm = new TextFormRequest(question1.getId(), InputType.TEXT, newAnswerText);
		forms.add(newTextForm);
		FormRequest newCheckListForm = new CheckListFormRequest(question2.getId(), InputType.SINGLE_CHECK,
			Collections.singletonList(question2.getCheckLists().get(1).getId()));
		forms.add(newCheckListForm);
		FormPatchRequestDto requestDto = new FormPatchRequestDto(forms);
		String content = objectMapper.writeValueAsString(requestDto);

		String url = "/recruitments/" + recruitment.getId()
			+ "/applications/" + application.getId();
		mockMvc.perform(patch(url)
				.header("Authorization", "Bearer " + accessToken)
				.contentType("application/json")
				.content(content))
			.andExpect(status().isNoContent());

		//then
		List<ApplicationAnswer> allAnswers = recruitMockData.getAllAnswers(user.getId(),
			recruitment.getId(), application.getId());
		assertEquals(2, allAnswers.size());
		allAnswers.stream().forEach(answer -> {
			if (answer.getQuestionId().equals(question1.getId())) {
				assertEquals(newAnswerText, answer.getAnswer());
			} else if (answer.getQuestionId().equals(question2.getId())) {
				assertEquals(question2.getCheckLists().get(1).getContent(), answer.getAnswer());
			}
		});
	}
}
