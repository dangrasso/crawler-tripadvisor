package it.thecrawlers.crawler;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import it.thecrawlers.parser.ItemReviewsPageParser;
import it.thecrawlers.persistence.DAO;
import it.thecrawlers.persistence.ReviewDAO;
import it.thecrawlers.model.Item;
import it.thecrawlers.model.Review;
/*SOLO PER TEST*/
import java.io.*;
/* FINE SOLO PER TEST*/
import java.util.*;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Yasser Ganjisaffar <lastname at gmail dot com>
 */
public class CrawlHandler{
	/*Questa mappa contiene l'id dell'oggetto item e il corrispettivo oggetto di riferimento.
	 * I valori di questa mappa, al termine del crawling, saranno scritti nel DB, e saranno quindi 
	 * tutti gli oggetti di tipo Hotels,Attractions e Restaurants
	 */
	private Map<String,Item> idToItem;
	private ItemReviewsPageParser parser;
	public int count=0;   //num. item scoperti
	public int countCompleti=0;  //...di cui completati e scritti su DB
	
	public CrawlHandler(){
		this.idToItem=new HashMap<String,Item>();
		this.parser=new ItemReviewsPageParser();
	}

	public Map<String, Item> getIdToItem() {
		return idToItem;
	}
	public void setIdToItem(Map<String, Item> idToItem) {
		this.idToItem = idToItem;
	}
	public Item getItemById(String id){
		return this.idToItem.get(id);
	}
	public void addNewItem(Item item){
		String id=item.getItemId();
		System.out.println(id);
		this.idToItem.put(id, item);
	}

	public List<Review> parseReviews(String html,String path){
		return this.parser.parseReviews(html,path);
	}
	public Item parseItem(String html,String path){
		return this.parser.parseItem(html, path);
	}
	public String parseItemIdFromPath(String path){
		return this.parser.parseItemIdFromPath(path);
	}
	public boolean isItemReviewsPage(String path){
		return this.parser.isItemReviewsPage(path);
	}
	
	
	public static void main(String[] args) throws Exception {
		
        if (args.length != 3) {
            System.out.println("Needed parameters: ");
            System.out.println("\t numberOfCralwers (number of concurrent threads)");
            System.out.println("\t rootFolder (it will contain intermediate crawl data,ex data/crawl/)");
            System.out.println("\t timeDelay (time delay between requests in ms as integer)");
            return;
        }
		/*
		 * numberOfCrawlers shows the number of concurrent threads that should
		 * be initiated for crawling.
		 */
		int numberOfCrawlers = Integer.parseInt(args[0]);
		
		/*
		 * crawlStorageFolder is a folder where intermediate crawl data is
		 * stored.
		 */
		String crawlStorageFolder =args[1];
		
		CrawlConfig config = new CrawlConfig();
		config.setCrawlStorageFolder(crawlStorageFolder);

		/*
		 * Be polite: Make sure that we don't send more than 1 request per
		 * second (1000 milliseconds between requests).
		 */
		int delay=Integer.parseInt(args[2]);
		config.setPolitenessDelay(delay);

		/*
		 * You can set the maximum crawl depth here. The default value is -1 for
		 * unlimited depth
		 */
		config.setMaxDepthOfCrawling(-1);

		/*
		 * You can set the maximum number of pages to crawl. The default value
		 * is -1 for unlimited number of pages
		 */
		config.setMaxPagesToFetch(-1);

		/*
		 * Do you need to set a proxy? If so, you can use:
		 * config.setProxyHost("proxyserver.example.com");
		 * config.setProxyPort(8080);
		 * 
		 * If your proxy also needs authentication:
		 * config.setProxyUsername(username); config.getProxyPassword(password);
		 */

		/*
		 * This config parameter can be used to set your crawl to be resumable
		 * (meaning that you can resume the crawl from a previously
		 * interrupted/crashed crawl). Note: if you enable resuming feature and
		 * want to start a fresh crawl, you need to delete the contents of
		 * rootFolder manually.
		 */
		config.setResumableCrawling(false);

		/*
		 * Instantiate the controller for this crawl.
		 */
		PageFetcher pageFetcher = new PageFetcher(config);
		RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		CrawlControllerOwn controller = new CrawlControllerOwn(config, pageFetcher, robotstxtServer);

		/*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */

		controller.addSeed("http://www.tripadvisor.it/");
		//controller.addSeed("http://www.tripadvisor.it/Hotel_Review-g189541-d206741-Reviews-Radisson_BLU_Royal_Hotel-Copenhagen_Zealand.html");
		//controller.addSeed("http://www.tripadvisor.it/Hotel_Review-g189473-d1472362-Reviews-Hotel_Atlantis-Thessaloniki_Thessaloniki_Region_Central_Macedonia.html");
		//controller.addSeed("http://www.tripadvisor.it/Hotel_Review-g186338-d193121-Reviews-The_Milestone_Hotel-London_England.html");
		//controller.addSeed("http://www.tripadvisor.it/Hotel_Review-g255057-d256305-Reviews-Novotel_Canberra-Canberra_Australian_Capital_Territory.html");
		Date startTime = new java.util.Date() ;
		System.out.println("Crawling iniziato - " + startTime.toString()) ;
		System.out.println("opzioni: " + numberOfCrawlers + " crawlers, max " + (double)(1000.00/config.getPolitenessDelay()) + " richieste/sec ") ;
		System.out.println("=============================================================") ;
		
		long start_ts= System.currentTimeMillis();
		
		/*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		CrawlHandler bcc=new CrawlHandler();
		controller.start(TACrawler.class, numberOfCrawlers,bcc);
		
		
		/*
		 * Crawl completed, report elapsed time.
		 */
		Date endTime = new java.util.Date() ;
		long end_ts= System.currentTimeMillis();
		
		int tot_elapsed_secs = (int) (end_ts - start_ts) / 1000 ;
		int hours = tot_elapsed_secs / 3600;
		int remainder = tot_elapsed_secs % 3600;
		int minutes = remainder / 60;
		int seconds = remainder % 60; 
		String elapsed= hours + "h:" + minutes + "':" + seconds + "\"";
		
		System.out.println("Crawling Terminato - " + endTime.toString()  + " - (durata: " + elapsed + " )") ;
		System.out.println("opzioni: " + numberOfCrawlers + " crawlers, limite max " + (double)(1000.00/config.getPolitenessDelay()) + " richieste/sec ") ;
		System.out.println("=============================================================") ;
		FileOutputStream fw;
		PrintStream ps=null;
		DAO<Item> itemDao = new DAO<Item>(Item.class);
		ReviewDAO reviewDao = new ReviewDAO();
		try{
			fw=new FileOutputStream("HOTEL"+bcc.count+".txt");
			ps=new PrintStream(fw);
		}catch(Exception ex){
			
		}
		int recensioniPerse=0;
		int recensioniTotali=0;
		for(Item i : bcc.getIdToItem().values()){
			for (Review r:i.getReviews())
				reviewDao.set(r);
			itemDao.set(i);
			System.out.println(i.toString());
			recensioniTotali+=i.getTotalReviewsCount();
			recensioniPerse+=(i.getTotalReviewsCount()-i.getReviews().size());
			boolean esito=(i.getTotalReviewsCount()==i.getReviews().size());
			String test="TEST: "+esito + "\t ID:"+i.getItemId() + " "+i.getDescription()+ " \t NUM REVIEW PREVISTE: "+i.getTotalReviewsCount()+"\t REVIEW TROVATE: "+i.getReviews().size();
			ps.println(test);
			System.out.println(test);
			//System.out.println("-------------------------NEXT ITEM---------------------------------");
		}
		String recensioni="Perse "+recensioniPerse+" su "+recensioniTotali+" recensioni";
		double accu=((double)(recensioniTotali-recensioniPerse))/recensioniTotali;
		accu=accu*100;
		String accuratezza="Accuratezza: "+accu+"%";
		ps.println(recensioni);
		ps.println(accuratezza);
		System.out.println(recensioni);
		System.out.println(accuratezza);
		
	}
	
}
