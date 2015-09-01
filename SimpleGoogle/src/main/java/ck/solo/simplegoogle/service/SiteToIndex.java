package ck.solo.simplegoogle.service;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SiteToIndex {

	private static final Logger logger = LoggerFactory.getLogger(SiteToIndex.class);

	@NotEmpty
	private String siteUrl;

	@Min(0)
	@Max(6)
	private int depth;

	private int linksCrawled;
	private int linksIndexed;
	private boolean crawlOtherSite;

	public SiteToIndex() {
		logger.debug("Конструктор SiteToIndex()");
	}

	public int getDepth() {
		return depth;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getSiteUrl() {
		return siteUrl;
	}

	public void setSiteUrl(String siteUrl) {
		this.siteUrl = siteUrl;
	}

	public int getLinksCrawled() {
		return linksCrawled;
	}

	public void setLinksCrawled(int linksCrawled) {
		this.linksCrawled = linksCrawled;
	}

	public int getLinksIndexed() {
		return linksIndexed;
	}

	public void setLinksIndexed(int linksIndexed) {
		this.linksIndexed = linksIndexed;
	}

	public boolean isCrawlOtherSite() {
		return crawlOtherSite;
	}

	public void setCrawlOtherSite(boolean crawlOtherSite) {
		this.crawlOtherSite = crawlOtherSite;
	}

	@Override
	public String toString() {
		return "SiteToIndex [siteUrl=" + siteUrl + ", depth=" + depth + ", linksCrawled=" + linksCrawled + ", linksIndexed=" + linksIndexed + ", crawlOtherSite=" + crawlOtherSite + "]";
	}

}
