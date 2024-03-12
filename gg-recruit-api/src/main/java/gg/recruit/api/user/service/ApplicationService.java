package gg.recruit.api.user.service;

import java.util.List;

import org.springframework.stereotype.Service;

import gg.data.recruit.application.Application;
import gg.recruit.api.user.service.response.ApplicationListDto;
import gg.repo.recruit.user.application.ApplicationRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationService {

	private final ApplicationRepository applicationRepository;

	public ApplicationListDto findMyApplications(Long userId) {
		List<Application> res = applicationRepository.findAllByUserId(userId);
		return new ApplicationListDto(res);
	}
}
