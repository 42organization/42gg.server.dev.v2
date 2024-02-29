package gg.data.party;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class GameTemplate {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long game_template_id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id")
	private Category category;

	@Column(length = 10)
	private String game_name;

	@Column
	private Integer max_game_people;

	@Column
	private Integer min_game_people;

	@Column
	private Integer max_game_time;

	@Column
	private Integer min_game_time;

	@Column(length = 10)
	private String genre;

	@Column(length = 10)
	private String difficulty;

	@Column(length = 100)
	private String summary;
}
