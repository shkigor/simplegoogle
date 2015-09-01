package ck.solo.simplegoogle.service;

public class DefaultSearchItem {

	public final String title, content, uri;
	public final float score;

	DefaultSearchItem(String title, String content, String uri, float score) {

		this.uri = uri;
		this.title = title;
		this.content = content;
		this.score = score;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getUri() {
		return uri;
	}

	public float getScore() {
		return score;
	}
}