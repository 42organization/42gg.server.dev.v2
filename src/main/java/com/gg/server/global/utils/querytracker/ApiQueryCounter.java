package com.gg.server.global.utils.querytracker;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import lombok.Getter;

@Component
@RequestScope
@Getter
public class ApiQueryCounter {
	private int count;

	public void increaseCount() {
		this.count++;
	}
}
