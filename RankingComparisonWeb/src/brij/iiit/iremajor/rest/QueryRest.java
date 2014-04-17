package brij.iiit.iremajor.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import brij.iiit.ire.lucene.LuceneSearch;
import brij.iiit.iremajor.core.DocumentScore;
import brij.iiit.iremajor.core.PageRankQuery;
import brij.iiit.iremajor.core.Query;

@Path("/query")
public class QueryRest {
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	@Path("{qstr}")
	public String getResults(@PathParam("qstr") String qstr,
			@QueryParam("normalize") boolean normalize,
			@QueryParam("resultCount") int resultCount,
			@QueryParam("tfidf") boolean tfidf,
			@QueryParam("pagerank") boolean pagerank,
			@QueryParam("hybrid") boolean hybrid) {
		
		JSONObject results = new JSONObject();
		JSONObject resultsObj = new JSONObject();
		JSONObject tfidfResultsObj = new JSONObject();
		JSONObject pagerankResultsObj = new JSONObject();
		JSONObject hybridResultsObj = new JSONObject();
		JSONArray luceneResultsArr = new JSONArray();
		JSONArray tfidfResultsArr = new JSONArray();
		JSONArray pagerankResultsArr = new JSONArray();
		JSONArray hybridResultsArr = new JSONArray();
		results.put("results", resultsObj);
		resultsObj.put("tfidf", tfidfResultsObj);
		resultsObj.put("pagerank", pagerankResultsObj);
		resultsObj.put("hybrid", hybridResultsObj);
		results.put("luceneResults", luceneResultsArr);
		tfidfResultsObj.put("result", tfidfResultsArr);
		pagerankResultsObj.put("result", pagerankResultsArr);
		hybridResultsObj.put("result", hybridResultsArr);
		long start = 0; double timeTaken = 0.0;
		
		// Initialize Lucene module
		LuceneSearch luceneSearch = new LuceneSearch();
		List<String> luceneResults = luceneSearch.query(qstr, resultCount);
		List<String> parallelLuceneList = new ArrayList<String>(
				luceneResults.size());

		for (String lres : luceneResults) {
			luceneResultsArr.add(lres);
			parallelLuceneList.add(lres.trim().toLowerCase());
		}

		if (tfidf) {
			start = (new Date()).getTime();
			// Initialize TF-IDF module
			Query query = new Query("/home/brij/Documents/IIIT/IRE/major/big/");
			Query.normalize = normalize;
			Query.NUM_OF_RESULTS = resultCount;

			// Get TF-TDF results
			List<DocumentScore> resultsList = query.find(qstr);

			int accuracyCount = 0;
			for (DocumentScore res : resultsList) {
				JSONObject resJson = res.toJson();
				resJson.put("matching", false);
				if (parallelLuceneList.contains(res.getID().trim().toLowerCase())) {
					resJson.put("matching", true);
					accuracyCount++;
				}
				tfidfResultsArr.add(resJson);
			}
			timeTaken = ((new Date()).getTime() - start) / 1000.0000;

			tfidfResultsObj.put("time", timeTaken);
			tfidfResultsObj.put("accuracy", (accuracyCount / (float) resultCount) * 100);
		}
		
		if (pagerank) {
			start = (new Date()).getTime();
			// Initialize TF-IDF module
			PageRankQuery prquery = new PageRankQuery("/home/brij/Documents/IIIT/IRE/major/pagerank/");
			PageRankQuery.NUM_OF_RESULTS = resultCount;

			// Get TF-TDF results
			List<DocumentScore> resultsList = prquery.find(qstr);

			int accuracyCount = 0;
			for (DocumentScore res : resultsList) {
				JSONObject resJson = res.toJson();
				resJson.put("matching", false);
				if (parallelLuceneList.contains(res.getID().trim().toLowerCase())) {
					resJson.put("matching", true);
					accuracyCount++;
				}
				pagerankResultsArr.add(resJson);
			}
			timeTaken = ((new Date()).getTime() - start) / 1000.0000;

			pagerankResultsObj.put("time", timeTaken);
			pagerankResultsObj.put("accuracy", (accuracyCount / (float) resultCount) * 100);
		}
		

		return JSONValue.toJSONString(results);
	}
}
