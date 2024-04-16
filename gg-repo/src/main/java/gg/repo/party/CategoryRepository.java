package gg.repo.party;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	Optional<Category> findByName(String name);
}
