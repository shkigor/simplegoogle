package ck.solo.simplegoogle.service;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ck.solo.simplegoogle.helpers.HtmlHelper;

@Service
public class SimpleCrawler {

	public static int allLinksCrawled = 1;
	private static final Logger logger = LoggerFactory.getLogger(SimpleCrawler.class);

	private NavigableSet<String> linksToCrawl;
	private Set<String> linksCrawled;
	@Autowired
	private IndexerEngine luceneIndexer;
	private boolean crawlOtherSite;

	public SimpleCrawler() {
		logger.debug("Конструктор SimpleCrawler");
		linksToCrawl = new TreeSet<String>();
		linksCrawled = new HashSet<String>();
	};

	public Set<String> getLinksCrawled() {
		return linksCrawled;
	}

	public void crawl(String pageURL, String baseURL, int depth, boolean crawlOtherSite) throws IOException, InterruptedException {
		this.crawlOtherSite = crawlOtherSite;
		allLinksCrawled = 1;
		linksToCrawl.clear();
		linksCrawled.clear();
		linksToCrawl.add(pageURL);
		luceneIndexer.prepare();
		depthLimitedCrawl(pageURL, depth);
	}

	public void depthLimitedCrawl(String pageURL, int depth) throws IOException, InterruptedException {
		if (depth < 0)
			return;

		// Добавить в список загруженных страниц
		linksCrawled.add(pageURL);
		// Коллекция ссылок на странице pageURL, которые удовлетворяют условиям
		Collection<String> pageVerifyLinks = new TreeSet<String>();

		// Скачиваем страницу
		String html = HtmlHelper.download(pageURL);
		if (null != html) {
			// events.onVisit(pageURL, html, seed);
			luceneIndexer.add(pageURL, html);

			if (depth > 0) {
				// String html;
				// html = HtmlHelper.download(pageURL);

				int linksAdded = 0;

				int i = pageURL.indexOf("/", 8);
				String baseUrl = (i == -1) ? pageURL : pageURL.substring(0, i);

				// Извлекаем из страницы все ссылки
				Collection<String> pageLinks = HtmlHelper.extractLinks(html, baseUrl);
				for (String pageLink : pageLinks) {
					if (shouldVisit(pageLink, baseUrl) && !linksCrawled.contains(pageLink) && !linksToCrawl.contains(pageLink)) {
						linksToCrawl.add(pageLink);
						pageVerifyLinks.add(pageLink);
						linksAdded++;

					}
				}
				allLinksCrawled += linksAdded;
				logger.debug(String.format("Fetched: [%s] %d new links", pageURL, linksAdded));
			}
		}
		for (String pageLink : pageVerifyLinks) {
			depthLimitedCrawl(pageLink, depth - 1);
		}

	}

	/* Skip any external links */
	public boolean shouldVisit(String url, String seed) {
		if (crawlOtherSite)
			return !url.contains("#");
		else
			return url.startsWith(seed) && !url.contains("#");
	}
}