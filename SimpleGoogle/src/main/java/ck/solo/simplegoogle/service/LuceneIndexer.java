package ck.solo.simplegoogle.service;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ck.solo.simplegoogle.helpers.HtmlHelper;

@Service
public class LuceneIndexer implements IndexerEngine {
	private static final Logger logger = LoggerFactory.getLogger(LuceneIndexer.class);

	public static IndexWriter indexWriter;
	public static int indexAdd = 0;
	private String indexDir;

	// public LuceneIndexer(@Value("#{ systemProperties['user.dir'] + '/search/LuceneIndex' }") String indexdir) {
	public LuceneIndexer() {
		String s = LuceneIndexer.class.getResource("").getPath();
		indexDir = s.substring(0, s.indexOf("WEB-INF")) + "search/LuceneIndex";
		logger.debug("indexDir = " + indexDir);
	}

	@Override
	public void prepare() {

		indexAdd = 0;
		boolean create = true;

		try {
			Directory dir = FSDirectory.open(Paths.get(indexDir));
			// Analyzer analyzer = new RussianAnalyzer();
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

			if (create) {
				// Create a new index in the directory, removing any
				// previously indexed documents:
				iwc.setOpenMode(OpenMode.CREATE);
			} else {
				// Add new documents to an existing index:
				iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			}

			indexWriter = new IndexWriter(dir, iwc);
		} catch (IOException e) {
			logger.error("Ошибка открытия папки для Индекса", e);
		}
	}

	@Override
	public void add(String url, String html) throws IOException {

		String title = HtmlHelper.extractTitle(html);
		String content = HtmlHelper.extractContent(html);

		indexAdd++;

		// make a new, empty document
		Document doc = new Document();

		// Add the path of the file as a field named "path". Use a
		// field that is indexed (i.e. searchable), but don't tokenize
		// the field into separate words and don't index term frequency
		// or positional information:
		Field pathField = new StringField("uri", url, Field.Store.YES);
		doc.add(pathField);

		if (null != title) {
			doc.add(new TextField("title", title, Field.Store.YES));
			doc.add(new SortedDocValuesField("title", new BytesRef(title)));
			doc.add(new TextField("content", content, Field.Store.YES));

			// doc.add(new Field("content", content, TextField.TYPE_STORED));

			if (indexWriter.getConfig().getOpenMode() == OpenMode.CREATE) {
				// New index, so we just add the document (no old document can be there):
				// System.out.println("adding " + url);
				indexWriter.addDocument(doc);
			} else {
				// Existing index (an old copy of this document may have been indexed) so
				// we use updateDocument instead to replace the old one matching the exact
				// path, if present:
				logger.debug("updateDocument for URL: " + url);
				indexWriter.updateDocument(new Term("title", title), doc);
				indexWriter.updateDocument(new Term("content", content), doc);
			}
		}

	}

}