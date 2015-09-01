package ck.solo.simplegoogle.service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Scope("prototype")
public class LuceneSearcher implements SeacherEngine {
	private static final Logger logger = LoggerFactory.getLogger(LuceneSearcher.class);
	public static volatile IndexReader indexReader;
	private static String indexDir;
	public int numTotalHits;
	private ArrayList<DefaultSearchItem> searchListResult;
	private int hitsPerPage = 10;

	static {
		String s = LuceneSearcher.class.getResource("").getPath();
		indexDir = s.substring(0, s.indexOf("WEB-INF")) + "search/LuceneIndex";
		logger.debug("indexDir = " + indexDir);
	}

	public LuceneSearcher() throws IOException {
		logger.debug("Конструктор LuceneSearcher()");
		searchListResult = new ArrayList<>();

		if (null == indexReader) {
			synchronized (LuceneSearcher.class) {
				if (null == indexReader) {
					indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
				}
			}
		}
	}

	@Override
	public ArrayList<DefaultSearchItem> search(String queryString, int count, int start, boolean sortFieldTitle, boolean sortType) {
		this.hitsPerPage = count;
		String[] fields = { "title", "content" };

		try {
			IndexSearcher searcher = new IndexSearcher(indexReader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new MultiFieldQueryParser(fields, analyzer);

			Query query = parser.parse(queryString);

			doSearchAndHighLightKeywords(searcher, query, start, hitsPerPage, sortFieldTitle, sortType);

		} catch (IOException | ParseException | InvalidTokenOffsetsException e) {
			logger.error("Ошибка открытия папки для Индекса", e);
		}

		return searchListResult;
	}

	private void doSearchAndHighLightKeywords(IndexSearcher searcher, Query query, int start, int hitsPerPage, boolean sortFieldTitle, boolean sortType) throws IOException, InvalidTokenOffsetsException {

		// STEP A
		QueryScorer queryScorer = new QueryScorer(query);
		Fragmenter fragmenter = new SimpleSpanFragmenter(queryScorer, 230);

		Highlighter highlighter = new Highlighter(new SimpleHTMLFormatter("[solo.ck]", "[/solo.ck]"), queryScorer);
		highlighter.setTextFragmenter(fragmenter); // Set fragment to highlight

		int nDocs = start + hitsPerPage;
		if (nDocs <= 0)
			return;

		TopDocs results;
		if (sortFieldTitle) {
			SortField sortField = new SortField("title", SortField.Type.STRING, sortType);
			Sort sort = new Sort(sortField);
			results = searcher.search(query, nDocs, sort);
		} else {
			// Collect enough docs to show nDocs pages
			results = searcher.search(query, nDocs);
		}

		ScoreDoc[] hits = results.scoreDocs;

		numTotalHits = results.totalHits;

		int end = Math.min(numTotalHits, nDocs);

		for (int i = start; i < end; i++) {
			Document doc = searcher.doc(hits[i].doc);
			String uri = doc.get("uri");
			String title = doc.get("title");
			String content = doc.get("content");

			String fragmentTitle = highlighter.getBestFragment(new StandardAnalyzer(), "title", title);
			if (StringUtils.isEmpty(fragmentTitle)) {
				fragmentTitle = title;
			}
			String fragmentContent = highlighter.getBestFragment(new StandardAnalyzer(), "content", content);

			DefaultSearchItem item = new DefaultSearchItem(fragmentTitle, fragmentContent, uri, hits[i].score);
			searchListResult.add(item);
		}
	}

	@Override
	public int getNumTotalHits() {
		return numTotalHits;
	}
}
