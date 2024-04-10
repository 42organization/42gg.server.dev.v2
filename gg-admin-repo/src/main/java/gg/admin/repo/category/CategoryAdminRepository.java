package gg.admin.repo.category;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Category;

public interface CategoryAdminRepository extends JpaRepository<Category, Long> {
	boolean existsByName(String categoryName);

	Optional<Category> findByName(String categoryName);
}
