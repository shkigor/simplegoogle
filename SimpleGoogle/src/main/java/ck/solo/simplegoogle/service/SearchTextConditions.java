package ck.solo.simplegoogle.service;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SearchTextConditions {

	private static final Logger logger = LoggerFactory.getLogger(SearchTextConditions.class);

	public enum SortResult {
		SORT_RELEVANT, SORT_TITLE_DESC, SORT_TITLE_ASC
	};

	private SortResult sortResult = SortResult.SORT_RELEVANT;
	private String q;
	@Min(1)
	@Max(30)
	int resultPerPage;
	int numTotalHits;
	int start;
	int currentPage;
	int allPage;

	public SearchTextConditions() {
		logger.debug("Конструктор SearchTextConditions()");
	}

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}

	public int getResultPerPage() {
		return resultPerPage;
	}

	public void setResultPerPage(int resultPerPage) {
		this.resultPerPage = resultPerPage;
	}

	public int getNumTotalHits() {
		return numTotalHits;
	}

	public void setNumTotalHits(int numTotalHits) {
		this.numTotalHits = numTotalHits;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getCurrentPage() {
		if (start == 0)
			return 1;
		else
			return start / resultPerPage + 1;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getAllPage() {
		if (numTotalHits % resultPerPage == 0)
			return numTotalHits / resultPerPage;
		return numTotalHits / resultPerPage + 1;
	}

	public void setAllPage(int allPage) {
		this.allPage = allPage;
	}

	public SortResult getSortResult() {
		return sortResult;
	}

	public void setSortResult(SortResult sortResult) {
		this.sortResult = sortResult;
	}

	@Override
	public String toString() {
		return "SearchTextConditions [sortResult=" + sortResult + ", q=" + q + ", resultPerPage=" + resultPerPage + ", numTotalHits=" + numTotalHits + ", start=" + start + ", currentPage=" + currentPage + ", allPage=" + allPage + "]";
	}

}
