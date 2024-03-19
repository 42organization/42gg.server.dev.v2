package gg.data.recruit.recruitment;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import gg.data.BaseTimeEntity;
import gg.data.recruit.recruitment.enums.InputType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "recruit_id", nullable = false)
	private Recruitments recruit;

	@Enumerated(EnumType.STRING)
	@Column(length = 20, nullable = false)
	private InputType inputType;

	@Column(length = 300)
	private String question;

	@OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
	private List<CheckList> checkLists;

	private int sortNum;

}
