package gg.repo.party;

import org.springframework.data.jpa.repository.JpaRepository;

import gg.data.party.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
