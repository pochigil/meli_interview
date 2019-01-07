package com.meli.services;

import android.util.Log;

import com.meli.AppController;

import java.util.List;

import model.Searches;
import model.SearchesDao;

public class DatabaseServices {

    public static final String TAG = "DatabaseServices";

    public DatabaseServices() {
    }

    public void addRequest(String query) {
        SearchesDao searchesDao = AppController.getSession().getSearchesDao();
        try {
            Searches search = new Searches();
            search.setQuery(query);
            searchesDao.insert(search);
        } catch (Exception e) {
            Log.e(TAG, "Error addRequest: " + e.getLocalizedMessage());
        }
    }

    public List<Searches> getSearches() {
        try {
            SearchesDao searchesDao = AppController.getSession().getSearchesDao();
            return searchesDao.queryBuilder().orderDesc(SearchesDao.Properties.Id).list();
        } catch (Exception e) {
            Log.d(TAG, "Error getDispatches: " + e.getLocalizedMessage());
        }
        return null;
    }

}