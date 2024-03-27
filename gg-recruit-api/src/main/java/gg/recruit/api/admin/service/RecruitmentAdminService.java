package gg.recruit.api.admin.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import gg.admin.repo.recruit.RecruitmentAdminRepository;
import gg.data.recruit.recruitment.CheckList;
import gg.data.recruit.recruitment.Question;
import gg.data.recruit.recruitment.Recruitment;
import gg.data.recruit.recruitment.enums.InputType;
import gg.recruit.api.admin.service.response.Form;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecruitmentAdminService {
	private final RecruitmentAdminRepository recruitmentAdminRepository;

	/**
	 * 채용 공고를 생성한다.
	 * Recruitment, Question, CheckList을 저장한다.
	 * @param recruitment Recruitment
	 * @param forms Question, CheckList이 포함
	 */
	@Transactional
	public void createRecruitment(Recruitment recruitment, List<Form> forms) {
		for (int i = 1; i <= forms.size(); i++) {
			Form form = forms.get(i);
			Question question = form.toQuestion(recruitment, i);
			InputType inputType = question.getInputType();

			if (inputType == InputType.SINGLE_CHECK || inputType == InputType.MULTI_CHECK) {
				List<String> checkList = form.getCheckList();
				for (String content : checkList) {
					new CheckList(question, content);
				}
			}
		}
		recruitmentAdminRepository.save(recruitment);
	}
}
