package it.thecrawlers.parser;

import it.thecrawlers.model.*;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.Element;

/**
 * Questa classe e' un Parser di pagine di TripAdvisor.it, in particolare
 * pensato per le pagine di tipo ItemReviews. Tali pagine contengono
 * contemporaneamente info su un singolo Item (Hotel, Ristorante, Attrazione) e
 * una collezione di tutte le Reviews relative a quell'item. Questo Parser e'
 * basato interamente sulla libreria jericho per le funzioni di parsing, ed e'
 * quindi totalmente disaccoppiato da crawler4j.
 * 
 * @author thecrawlers
 */
public class ItemReviewsPageParser {
	private Map<String, Integer> monthName2int;

	/**
	 * Verifica se la pagina indicata dal path e' di tipo Item-Reviews, ossia se e' la
	 * pagina di un restaurant, hotel o attraction
	 * 
	 * @param path
	 *            e' il percorso della pagina, ad es.
	 *            "/Restaurant_Review-g1207908-d2060003-Reviews-or20-Mama_Rosa_s-Leighton_Buzzard_Bedfordshire_England.html"
	 * @return boolean true se il path e' di un item, false altrimenti
	 */
	public boolean isItemReviewsPage(String path) {
		return (path.startsWith("/Hotel_Review"))
				|| (path.startsWith("/Restaurant_Review"))
				|| (path.startsWith("/Attraction_Review"));
	}

	/**
	 * Effettua il parsing di un item dati html e path della pagina relativa.
	 * 
	 * @param html
	 *            contiene tutto l'html della pagina (non più usata, ma
	 *            fondamentale se poi vorremo estrarre altre info)
	 * @param path
	 *            il percorso della pagina, ad es.
	 *            "/Restaurant_Review-g1207908-d2060003-Reviews-or20-Mama_Rosa_s-Leighton_Buzzard_Bedfordshire_England.html"
	 * @return Un oggetto di tipo Item costruito a partire dai dati della
	 *         pagina.
	 */
	public Item parseItem(String html, String path) {
		Source source = new Source(html); // creo
		//source.fullSequentialParse(); // inizializzo

		String[] pathFields = path.split("-");
		/*
		 * NB:
		 * " /Restaurant_Review-g1207908-d2060003-Reviews-or20-Mama_Rosa_s-Leighton_Buzzard_Bedfordshire_England.html"
		 * [0] /Restaurant_Review [1] g1207908 [2] d2060003 [3] Reviews [4?]
		 * or20 [4-5] Mama_Rosa_s (penultimo) [5-6]
		 * Leighton_Buzzard_Bedfordshire_England.html (ultimo)
		 */
		String itemID = pathFields[2];
		String description = pathFields[pathFields.length - 2]
				.replace("_", " ");
		// String description = source.getElementById("HEADING").getContent().toString();
		// //ALTERNATIVA (non sempre funziona)

		// String locationID = pathFields[1];
		// String locationName = pathFields[pathFields.length - 1].substring(0, IndexOf(".") - 1);
		String raw_totalReviewsCount = "0";
		int totalReviewsCount = 0;
		try {
			raw_totalReviewsCount = source
					.getFirstElementByClass("reviews_header").getContent()
					.toString().replace(".", "");
			totalReviewsCount = Integer.parseInt(raw_totalReviewsCount
					.substring(0, raw_totalReviewsCount.indexOf(" ")));

		} catch (Exception ex) {}

		Item item = new Item(itemID, description);
		item.setTotalReviewsCount(totalReviewsCount);

		String typeName = pathFields[0].substring(1, path.indexOf("_")); // es: "/Hotel_Review" >> "Hotel"
		if (typeName.equals("Hotel"))
			item.setType(ItemType.hotel);
		else if (typeName.equals("Restaurant"))
			item.setType(ItemType.restaurant);
		else if (typeName.equals("Attraction"))
			item.setType(ItemType.attraction);
		else
			System.out.println("Tipo di Item non riconosciuto, valore in input: '" + typeName + "'");
		return item;
	}

	/**
	 * Dato il path di una Item_Reviews page di tripadvisor, ritorna l'id
	 * dell'item a cui fa riferimento il path
	 * 
	 * @param path
	 * @return
	 */
	public String parseItemIdFromPath(String path) {
		String[] pathFields = path.split("-");
		/*
		 * Tipo URL
		 * http://www.tripadvisor.it/Hotel_Review-g187791-d191099-Reviews
		 * -or1200-Albergo_del_Senato-Rome_Lazio.html#REVIEWS Quindi l'id
		 * dell'item sarebbe il terzo componente della stringa url (d191099)
		 */
		return pathFields[2];
	}

	/**
	 * Effettua il parsing delle review sulla pagina corrente del parser.
	 * 
	 * @param html
	 *            contiene tutto l'html della pagina (non più usata, ma
	 *            fondamentale se poi vorremo estrarre altre info)
	 * @param path
	 *            è il percorso della pagina, ad es.
	 *            "/Restaurant_Review-g1207908-d2060003-Reviews-or20-Mama_Rosa_s-Leighton_Buzzard_Bedfordshire_England.html"
	 * @return Una lista di Review, corrispondenti a quelle sulla pagina
	 *         corrente
	 */
	public List<Review> parseReviews(String html, String path) {
		Source source = new Source(html); // creo la source di jericho
		//source.fullSequentialParse();

		List<Review> parsedReviews = new LinkedList<Review>();

		List<Element> reviews = source.getAllElementsByClass("reviewSelector");
		for (Element rev : reviews) {
			try {
				//estraggo "reviewID"
				String reviewID = "r"
						+ rev.getAttributeValue("id").substring(7); // "review_1352800"
																	// -->
																	// "r1352800"
				//estraggo "date"
				String raw_date = rev.getFirstElementByClass("ratingDate")
						.getContent().toString(); // "Recensito il 28 giugno 2009",
													// "Ieri"
				Date date = parseRawDate(raw_date);

				// Potremmo voler estrarre anche il titolo della review (non funziona sempre)
				//estraggo "reviewTitle"
				/*
				 * String reviewTitle =
				 * rev.getFirstElementByClass("quote").getContent
				 * ().getFirstElement().getContent().toString(); reviewTitle =
				 * reviewTitle.substring(8, reviewTitle.length() - 8 ) ; //
				 * altrimenti ho &#x201c;_______&#x201d;
				 */
				String reviewTitle = "";  //altrimenti passo una stringa vuota come titolo della review

				//estraggo "value"
				String raw_value = rev
						.getFirstElementByClass("sprite-ratings")
						.getAttributeValue("alt"); // "3 stelle su 5"
				
				int int_value = Integer.parseInt(raw_value.substring(0,
						raw_value.indexOf(" ")));

				if ((int_value < 1)||(int_value>5))
					throw new Exception("Il valore '" + int_value + "' non corrisponde ad un voto valido! (1-5)");
				
				// creo l'enum a partire dal valore 
				ReviewValue rv = null;
				switch (int_value) {
				case 1:
					rv = ReviewValue.veryBad;
					break;
				case 2:
					rv = ReviewValue.bad;
					break;
				case 3:
					rv = ReviewValue.medium;
					break;
				case 4:
					rv = ReviewValue.good;
					break;
				case 5:
					rv = ReviewValue.veryGood;
					break;
				}
				
				
				//costruisco l'oggetto review con i dati estratti
				Review newReview = new Review(reviewID, date, reviewTitle, rv);
				//lo aggiungo alle review parsate
				parsedReviews.add(newReview);

			} catch (Exception e) {
				// errore durante il parsing di una review
				System.out.println("!!!--------Errore nel parsing di una review nella pagina:");
				System.out.println(path);
				System.out.println("Eccezione: " + e.getMessage());
				// forse qui potremmo anche tenere traccia degli errori di parsing (ad es.
				// del numero di review saltate x errori)
			}

		}
		return parsedReviews;
	}

	/**
	 * prende in input una rappresenzazione grezza della data (es.
	 * "Recensito il 15 giugno 2012", oppure "Ieri") e ne estrae la data in un
	 * oggetto java.util.Date
	 * 
	 * @param raw_date
	 * @return
	 */
	private Date parseRawDate(String raw_date) {
		// la stringa in input � del tipo "Recensito il 15 giugno 2012"
		Date date = null;
		String[] dateParts = raw_date.substring(13).trim().split(" ");
		String giorno = dateParts[0];
		String mese = dateParts[1].toLowerCase();
		String anno = dateParts[2].substring(0, 4);
		int gg = Integer.parseInt(giorno);
		int mm = 0;
		if (this.monthName2int.containsKey(mese))
			mm = (int) this.monthName2int.get(mese);
		int aa = Integer.parseInt(anno);

		Calendar cal = Calendar.getInstance();
		cal.set(aa, mm, gg);
		date = cal.getTime();

		return date;
	}


	public ItemReviewsPageParser() {
		super();
		//inizializzo la mappa con i nomi dei mesi in italiano
		this.monthName2int = new HashMap<String, Integer>();
		monthName2int.put("gennaio", 1);
		monthName2int.put("febbraio", 2);
		monthName2int.put("marzo", 3);
		monthName2int.put("aprile", 4);
		monthName2int.put("maggio", 5);
		monthName2int.put("giugno", 6);
		monthName2int.put("luglio", 7);
		monthName2int.put("agosto", 8);
		monthName2int.put("settembre", 9);
		monthName2int.put("ottobre", 10);
		monthName2int.put("novembre", 11);
		monthName2int.put("dicembre", 12);
	}
}
