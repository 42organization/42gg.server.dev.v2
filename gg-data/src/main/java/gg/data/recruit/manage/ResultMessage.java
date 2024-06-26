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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ResultMessage extends BaseTimeEntity {
	public static final int contentLimit = 100;
	public static final int messageTypeLimit = 15;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = contentLimit)
	private String content;

	@Column(length = messageTypeLimit, nullable = false)
	@Enumerated(EnumType.STRING)
	private MessageType messageType;

	@Column
	private Boolean isUse = true;

	@Builder
	public ResultMessage(String content, MessageType messageType) {
		this.content = content;
		this.messageType = messageType;
	}
}
