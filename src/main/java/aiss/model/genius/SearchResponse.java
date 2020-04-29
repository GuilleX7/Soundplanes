package aiss.model.genius;

import java.util.List;

public class SearchResponse {
	private List<Hit> hits;

	public List<Hit> getHits() {
		return hits;
	}

	public void setHits(List<Hit> hits) {
		this.hits = hits;
	}
}
