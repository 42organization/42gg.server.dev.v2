package gg.admin.repo.category;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Category;

public interface CategoryAdminRepository extends JpaRepository<Category, Long> {
	Boolean existsByName(String categoryName);

	Category findByName(String categoryName);
}
