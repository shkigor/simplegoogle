package ck.solo.simplegoogle.helpers;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HtmlHelper {

	private static final Logger logger = LoggerFactory.getLogger(HtmlHelper.class);

	public static Collection<String> extractLinks(String html, String seed) {
		Document document = Jsoup.parse(html, seed);
		Set<String> linksSet = new HashSet<String>();
		for (Element link : document.select("a[href]")) {
			String strLink = link.attr("abs:href").trim();
			if (!strLink.isEmpty()) {
				if (strLink.endsWith("/")) {
					String newLink = strLink.substring(0, strLink.length() - 1);
					linksSet.add(newLink);
				} else {
					linksSet.add(strLink);
				}
			}
		}

		return Collections.unmodifiableCollection(linksSet);
	}

	public static String download(String link) throws IOException, InterruptedException {
		int retry = 5;

		do {
			try {

				/* Crawling a real web site politeness > 5s */

				Thread.sleep(1);
				Document bDoc = Jsoup.connect(link).userAgent("Mozilla").timeout(30000).get();
				return bDoc.html();
			} catch (IOException ex) {
				logger.error("Exception: download url = " + link, ex);
				return null;
			}
		} while (--retry > 0);

	}

	public static String extractTitle(String html) {
		Document doc = Jsoup.parse(html);
		return doc.title();
	}

	public static String extractContent(String html) {
		Document doc = Jsoup.parse(html);
		return doc.text();
	}
}