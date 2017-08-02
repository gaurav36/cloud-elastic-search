package net.elastic.spring.dto;

public class QueryIndexer {

	private Long id;
	private String filePath;
	private String index;

	public QueryIndexer(long id, String filePath, String index) {
		this.id = id;
		this.filePath = filePath;
		this.index = index;
	}

	public QueryIndexer() {
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
