package gg.data.recruit.manage;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import gg.data.BaseTimeEntity;
import gg.data.recruit.manage.enums.MessageType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultMessage extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 100)
	private String content;

	@Column(length = 15, nullable = false)
	@Enumerated(EnumType.STRING)
	private MessageType messageType;

	private Boolean isUse;
}
