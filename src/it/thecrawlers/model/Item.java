package it.thecrawlers.model;


import java.util.*;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
/**
 * Classe che modella un item. Un item � una delle mete che vogliamo analizzare: alberghi, ristoranti, attrazioni
 * @author TheCrawlers
 *
 */
@Entity
public class Item {
	private String _itemId;
	private String _description;//sarebbe il nome
	private ItemType _type;//invece della stringa "secca" usiamo un enum, il rischio di valori strani cos� � limitato
	private Set<Review> _reviews;//meglio usare un set, cos� non corriamo il rischio di falsi duplicati
	private Date crawlDate; //"informazioni aggiornate a...."
	private int _totalReviewsCount;
	/**
	 * Costruttore vuoto.
	 */
	public Item(){
		super();
		_reviews = new HashSet<Review>();
		crawlDate = new Date(System.currentTimeMillis());
	}
	/**
	 * Costruisce un nuovo Item in base ai parametri passati
	 * @param itemId l'id dell'Item.
	 * @param description Una descrizione, come ad esempio "Hotel Saracinesco village international"
	 */
	public Item(String itemId, String description){
		this();
		_itemId = itemId;
		_description = description;
	}
	@Id
	@Column(name="ItemId")
	/**
	 * Restituisce l'id dell'oggetto
	 * @return ItemId
	 */
	public String getItemId(){
		return _itemId;
	}
	/**
	 * Imposta il nuovo Id dell'Item
	 * @param itemId il nuovo id
	 */
	public void setItemId(String itemId){
		_itemId = itemId;
	}
	/**
	 * Riporta la data del crawling di questo oggetto
	 * @return Quando � stato effettuato il crawling
	 */
	public Date getCrawlDate() {
		return crawlDate;
	}
	/**
	 * imposta una nuova data per il crawling
	 * @param crawlDate la data del crawling
	 */
	public void setCrawlDate(Date crawlDate) {
		this.crawlDate = crawlDate;
	}
	/**
	 * Restituisce la descrizione dell'Item
	 * @return description
	 */
	public String getDescription() {
		return _description;
	}
	/**
	 * Imposta la nuova descrizione dell'item
	 * @param description La nuova descrizione
	 */
	public void setDescription(String description) {
		_description = description;
	}
	/**
	 * Restituisce il tipo dell'oggetto (albergo, ristorante...)
	 * @return Il tipo dell'Item
	 */
	public ItemType getType() {
		return _type;
	}
	/**
	 * Cambia il tipo dell'oggetto
	 * @param type il nuovo tipo
	 */
	public void setType(ItemType type) {
		_type = type;
	}
	@OneToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name="ItemReviews",
			joinColumns=@JoinColumn(name="ItemId"),
			inverseJoinColumns=@JoinColumn(name="ReviewId")
			)
	/**
	 * Riporta le recensioni dell'item
	 * @return Una lista di recensioni. Usando una lista di azzera il rischio di
	 * eliminazione di falsi duplicati.
	 */
	public Set<Review> getReviews() {
		return _reviews;
	}
	/**
	 * Assegna una nuova lista di recensioni
	 * @param reviews la nuova lista di recensioni
	 */
	public void setReviews(Set<Review> reviews) {
		_reviews = reviews;
	}
	@Transient
	/**
	 * Riporta il numero totale di review per questo item
	 * @return il numero di review
	 */
	public int getTotalReviewsCount() {
		return _totalReviewsCount;
	}
	/**
	 * Imposta un numero di reviews per questo item
	 * @param totalReviewsCount il nuovo numero di reviews
	 */
	public void setTotalReviewsCount(int totalReviewsCount) {
		_totalReviewsCount = totalReviewsCount;
	}
	/**
	 * Ottiene in una stringa i dati di un item e la lista delle reviews
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder("Item: [ ");
		sb.append("ID: " + this.getItemId() + ", ");
		sb.append("Tipo: " + this.getType().toString() + ", ");
		sb.append("Nome: " + this.getDescription() + ", ");
		int numReviews = this.getReviews().size();
		sb.append("#reviews: " + numReviews);
		if ( numReviews > 0 ){
			sb.append("\n");
			for (Review r : this.getReviews()){
				sb.append( " \t" + r.toString() + " \n");
			}
		}
		sb.append(" ]");
		return sb.toString();
	}
}