package ck.solo.simplegoogle.service;

import java.io.IOException;

public interface IndexerEngine {

	void add(String url, String html) throws IOException;

	void prepare();
}
