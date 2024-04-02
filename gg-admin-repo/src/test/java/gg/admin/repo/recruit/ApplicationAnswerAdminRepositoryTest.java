package gg.admin.repo.recruit;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.annotation.UnitTest;

@IntegrationTest
@UnitTest
@Transactional
class ApplicationAnswerAdminRepositoryTest {
	@Autowired
	EntityManager entityManager;

	@Autowired
	ApplicationAdminRepository applicationAnswerAdminRepository;

	@Autowired
	TestDataUtils testDataUtils;

}
