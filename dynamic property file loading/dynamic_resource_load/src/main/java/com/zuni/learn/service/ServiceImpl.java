package com.zuni.learn.service;

import com.zuni.learn.properties.ReloadableProperty;

@org.springframework.stereotype.Service
public class ServiceImpl {
	
	@ReloadableProperty("message")
	private String message;
	
	public String getMessage() {
		return this.message;
	}

}
