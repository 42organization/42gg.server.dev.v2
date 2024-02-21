package gg.pingpong.data.game;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
public class Season {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@NotNull
	@Column(name = "season_name", length = 20)
	private String seasonName;

	@Setter
	@NotNull
	@Column(name = "start_time")
	private LocalDateTime startTime;

	@Setter
	@NotNull
	@Column(name = "end_time")
	private LocalDateTime endTime;

	@Setter
	@NotNull
	@Column(name = "start_ppp")
	private Integer startPpp;

	@Setter
	@NotNull
	@Column(name = "ppp_gap")
	private Integer pppGap;

	@Builder
	public Season(String seasonName, LocalDateTime startTime, LocalDateTime endTime, Integer startPpp, Integer pppGap) {
		this.seasonName = seasonName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.startPpp = startPpp;
		this.pppGap = pppGap;
	}

}
