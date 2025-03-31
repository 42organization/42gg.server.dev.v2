package gg.admin.repo.calendar;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import gg.data.calendar.PublicSchedule;
import gg.data.calendar.type.DetailClassification;

@Repository
public interface PublicScheduleAdminRepository extends JpaRepository<PublicSchedule, Long>,
	JpaSpecificationExecutor<PublicSchedule> {

	List<PublicSchedule> findByAuthor(String author);

	List<PublicSchedule> findAllByClassificationOrderByIdDesc(DetailClassification detailClassification);

	List<PublicSchedule> findAll();
}
