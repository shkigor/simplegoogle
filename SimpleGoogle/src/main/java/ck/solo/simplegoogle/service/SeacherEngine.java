package ck.solo.simplegoogle.service;

import java.util.ArrayList;

public interface SeacherEngine {

	public ArrayList<DefaultSearchItem> search(String queryString, int count, int start, boolean sortField, boolean sortType);

	public int getNumTotalHits();
}
