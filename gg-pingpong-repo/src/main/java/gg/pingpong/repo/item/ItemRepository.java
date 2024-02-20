package gg.pingpong.repo.item;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.gg.server.data.store.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	@Query("SELECT i FROM Item i WHERE i.isVisible = true ORDER BY i.createdAt DESC")
	List<Item> findAllByCreatedAtDesc();
}
