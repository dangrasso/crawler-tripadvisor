package it.thecrawlers.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

import it.thecrawlers.model.Item;
import it.thecrawlers.model.Review;
import it.thecrawlers.persistence.DAO;
import it.thecrawlers.persistence.ReviewDAO;

import java.util.*;
import java.util.regex.*;

/**
 * Questa classe rappresenta un crawler per TripAdvisor ("TA")
 * Estende la classe WebCrwler di crawler4j.
 * @author thecrawlers
 *
 */
public class TACrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");
	private CrawlHandler handler;
	
	public TACrawler(){
		super();
	}
	public TACrawler(CrawlHandler handler){
		super();
		this.handler=handler;
	}
	/**
	 * You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic).
	 */
	@Override
	public boolean shouldVisit(WebURL url) {
		boolean result =false;

		String href = url.getURL().toLowerCase();
		//se la risorsa e' una pagina web e appartiene a tripadvisor.it...
	
		if ( !FILTERS.matcher(href).matches() && href.startsWith("http://www.tripadvisor.it")){
			// verifico che la pagina sia tra quelle d'interesse per il crawling
			String path=url.getPath();
			result =  (path.startsWith("/Tourism"))
					||(path.startsWith("/AllLocations")) 
					||(path.startsWith("/Hotels")) 
					||(path.startsWith("/Restaurants")) 
					||(path.startsWith("/Attractions")) 
					||(path.startsWith("/Hotel_Review")) 
					||(path.startsWith("/Restaurant_Review"))
					||(path.startsWith("/Attraction_Review")) ;

		}
		
		return result;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String path = page.getWebURL().getPath();
		
		//Se il path della pagina corrisponde a quello della classe ItemReviews
		if (handler.isItemReviewsPage(path) ){
			if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				String html = htmlParseData.getHtml(); //ottengo l'html dalla Page
			
				//prendo l'id dell'item 
				String idItem = handler.parseItemIdFromPath(path);
				//prendo le recensioni (effettuo il parsing)
				List<Review> parsedReviews = handler.parseReviews(html,path);
				Item item = handler.getItemById(idItem);	
				@SuppressWarnings("unused")
				DAO<Item> itemDao = new DAO<Item>();
				@SuppressWarnings("unused")
				ReviewDAO reviewDao = new ReviewDAO();
				
				//Item gia' trovato in esplorazioni precedenti, aggiungo le recensioni
				if(item!=null){
					//	System.out.println("Item gia' trovato in esplorazioni precedenti con id "+idItem+", aggiungo le recensioni");
					//	System.out.println(path);
					//	System.out.println(item.toString());
					//	System.out.print("Num. recensioni iniziali: "+item.getReviews().size());
					item.getReviews().addAll(parsedReviews);
					//	System.out.println(" num. recensioni finali: "+item.getReviews().size());
					//	System.out.println("---------------------------------------------------");
					
					//Se l'item è "completo" di tutte le recensioni attese
					if (item.getTotalReviewsCount() == item.getReviews().size()){

						//scriviamo su disco ------------------ MODALITA CON SCRITTURE IN ITINERE  (disattivata)
						//						for(Review r : item.getReviews())									//
						//							reviewDao.set(r);												//
						//						itemDao.set(item);													//
						//						handler.getIdToItem().remove(idItem);								//
						
						//qualunque sia la modalità, qui aggiorniamo il count degli item completati
						handler.countCompleti++;
						
						System.out.println(" Item trovati finora: "+handler.count+" ( completi: " + handler.countCompleti + " )");
					}
					
				}
				else
				//Item non presente, lo aggiungo all'insieme degli item
				{
					//	System.out.print(".");
					handler.count++;
					item = handler.parseItem(html,path);
					//	System.out.println("Item non presente, lo aggiungo all'insieme degli item, trovato con id "+idItem+" inserito nel sistema con id "+item.getItemID());
					item.getReviews().addAll(parsedReviews);
					handler.addNewItem(item);
					
					//Se l'item appena scoperto è "completo" di tutte le recensioni attese (accade quando l'item ha 10 recensioni o meno)
					if (item.getTotalReviewsCount() == item.getReviews().size()){
  						
						//scriviamo su disco ------------------MODALITA CON SCRITTURE IN ITINERE  (disattivata)
						//						for(Review r : item.getReviews())									//
						//							reviewDao.set(r);												//
						//						itemDao.set(item);													//
						//						handler.getIdToItem().remove(idItem);								//
						
						//qualunque sia la modalità, qui aggiorniamo il count degli item completati
						handler.countCompleti++;
					}
					System.out.println(" Item trovati finora: "+handler.count+" ( completi: " + handler.countCompleti + " )");
					
					//Genero i link alle paginate di recensioni successive (i link di navigazione a volte non ci sono)
					int reviewsCount = item.getTotalReviewsCount();
					int reviewsPageCount = reviewsCount / 10 ;
					List<WebURL> reviewsURLs = new ArrayList<WebURL>(reviewsPageCount);
					DocIDServer docIdServer= this.getMyController().getDocIdServer(); //serve per generare un docID
					
					for (int pagenum=0; pagenum<=reviewsPageCount; pagenum++){
						String original_url = page.getWebURL().getURL();
						String url;
						url = original_url.replaceFirst("-Reviews(-or[0-9]+)?-", "-Reviews-or" + (pagenum*10) + "-");
						WebURL webUrl = new WebURL();
						webUrl.setURL(url);
						webUrl.setPath(url.substring(url.lastIndexOf("/") - 1));
						webUrl.setParentDocid(page.getWebURL().getDocid());
						webUrl.setParentUrl(page.getWebURL().getURL());
						webUrl.setDepth( (short)(page.getWebURL().getDepth()+1) );
						webUrl.setDocid(docIdServer.getNewDocID(url));
						reviewsURLs.add(webUrl);				
					}
					//li aggiungo in frontiera
					this.getMyController().getFrontier().scheduleAll(reviewsURLs);
				}
				
				//codice di test ------------------------------------------------------
				/*System.out.println(" URL GENERATI: ");
				for (WebURL wu : reviewsURLs){
					System.out.println("  -  " + wu.getURL());
				}*/
				/*
				System.out.println();
				System.out.println(item.toString());
				System.out.println(path);
				System.out.println("---------------------------------------------------");
				if(handler.count>100)
					Thread.currentThread().stop();
				*/
			}
		}
	}
	
}
