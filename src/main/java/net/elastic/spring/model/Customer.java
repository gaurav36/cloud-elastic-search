package net.elastic.spring.model;

import java.util.Date;

public class Customer {

	private Long id;
	private String filePath;
	private String index;

	public Customer(long id, String filePath, String index, String email, String mobile) {
		this.id = id;
		this.filePath = filePath;
		this.index = index;
	}

	public Customer() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getfilePath() {
		return filePath;
	}

	public void setfilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getindex() {
		return index;
	}

	public void setindex(String index) {
		this.index = index;
	}
}
