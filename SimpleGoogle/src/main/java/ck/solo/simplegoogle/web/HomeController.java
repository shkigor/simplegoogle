package ck.solo.simplegoogle.web;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.validator.routines.UrlValidator;
import org.apache.lucene.index.DirectoryReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import ck.solo.simplegoogle.service.LuceneIndexer;
import ck.solo.simplegoogle.service.LuceneSearcher;
import ck.solo.simplegoogle.service.SeacherEngine;
import ck.solo.simplegoogle.service.SearchTextConditions;
import ck.solo.simplegoogle.service.SimpleCrawler;
import ck.solo.simplegoogle.service.SiteToIndex;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	@Autowired
	private SiteToIndex siteToIndex;

	@Autowired
	ApplicationContext ctx;

	/**
	 * Simply selects the home view to render by returning its name.
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String root() {
		logger.debug("Welcome home!");
		return "root";
	}

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(@RequestParam("q") String q, @RequestParam(value = "action", required = false) String action, Model model, @Valid @ModelAttribute("searchTextConditions") SearchTextConditions searchTextConditions,
			BindingResult result) {
		logger.debug("Welcome search!? q = {} action = {}", q, action);

		if (result.hasErrors()) {
			return "search";
		}

		int start = searchTextConditions.getStart();
		if (!StringUtils.isEmpty(action)) {
			switch (action) {
			case "<<":
				start -= searchTextConditions.getResultPerPage();
				break;
			case ">>":
				start += searchTextConditions.getResultPerPage();
				break;

			default:
				start = 0;
				break;
			}
		} else {
			start = 0;
		}

		boolean sortByTitleField;
		boolean sortByAscDesc;
		switch (searchTextConditions.getSortResult()) {
		case SORT_RELEVANT:
			sortByTitleField = false;
			sortByAscDesc = false;
			break;
		case SORT_TITLE_ASC:
			sortByTitleField = true;
			sortByAscDesc = false;
			break;
		case SORT_TITLE_DESC:
			sortByTitleField = true;
			sortByAscDesc = true;
			break;
		default:
			sortByTitleField = false;
			sortByAscDesc = false;
			break;
		}

		SeacherEngine luceneSearcher = ctx.getBean(SeacherEngine.class);
		model.addAttribute("searchListResult", luceneSearcher.search(q, searchTextConditions.getResultPerPage(), start, sortByTitleField, sortByAscDesc));

		searchTextConditions.setNumTotalHits(luceneSearcher.getNumTotalHits());
		searchTextConditions.setQ(q);
		searchTextConditions.setStart(start);
		model.addAttribute("searchTextConditions", searchTextConditions);

		return "search";
	}

	@RequestMapping(value = "/index", method = RequestMethod.GET)
	public String index(Model model, @RequestParam(value = "q", required = false) String q, HttpServletRequest request) {
		logger.debug("Welcome index!");
		if (StringUtils.isEmpty(q)) {
			model.addAttribute("siteToIndex", siteToIndex);
		} else {
			SiteToIndex ss = (SiteToIndex) request.getSession().getAttribute("siteToIndex");
			if (ss != null)
				model.addAttribute("siteToIndex", ss);
			else
				model.addAttribute("siteToIndex", siteToIndex);
		}

		return "index";
	}

	@RequestMapping(value = "/index", method = RequestMethod.POST)
	public String checkIndexUrl(@Valid @ModelAttribute("siteToIndex") SiteToIndex sti, BindingResult result, HttpServletRequest request) {
		logger.debug("Welcome checkIndexUrl! BindingResult = {}", result);

		// Устанавливаем прокси
		// System.setProperty("http.proxyHost", "10.140.200.202");
		// System.setProperty("http.proxyPort", "3128");
		// System.setProperty("http.nonProxyHosts", "*.bank.gov.ua|10.*");

		if (result.hasErrors()) {
			return "index";
		}

		UrlValidator urlValidator = new UrlValidator();
		if (!urlValidator.isValid(sti.getSiteUrl())) {
			FieldError siteUrlError = new FieldError("siteToIndex", "siteUrl", "Не корректный URL !");
			result.addError(siteUrlError);
			return "index";
		}

		int i = sti.getSiteUrl().indexOf("/", 8);
		String baseUrl = (i == -1) ? sti.getSiteUrl() : sti.getSiteUrl().substring(0, i);
		logger.debug("baseUrl = {}", baseUrl);
		try {

			Date start = new Date();
			SimpleCrawler sc = ctx.getBean(SimpleCrawler.class);
			sc.crawl(sti.getSiteUrl(), baseUrl, sti.getDepth(), sti.isCrawlOtherSite());

			LuceneSearcher.indexReader = DirectoryReader.openIfChanged(DirectoryReader.open(LuceneIndexer.indexWriter, true));
			LuceneIndexer.indexWriter.close();

			Date end = new Date();
			logger.info(end.getTime() - start.getTime() + " total milliseconds");

			// logger.info("Всего захвачено линков: " + SimpleCrawler.allLinksCrawled);
			// logger.info("Всего добавлено линков в индекс: " + LuceneIndexer.indexAdd);
			// for (String url : sc.getLinksCrawled()) {
			// System.out.println(url);
			// }

			sti.setLinksCrawled(SimpleCrawler.allLinksCrawled);
			sti.setLinksIndexed(LuceneIndexer.indexAdd);
			// Запомнить siteToIndex в сессии
			request.getSession().setAttribute("siteToIndex", sti);

		} catch (IOException | InterruptedException ex) {
			logger.error("Exception: checkIndexUrl", ex);
		}

		return "redirect:/index?q=" + sti.getSiteUrl();
	}

}
