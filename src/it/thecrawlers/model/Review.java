package it.thecrawlers.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
/**
 * Classe che modella una recensione. Una recensione ha una data, una descrizione ("Questo locale e'
 * una bomba!") e un voto. (*,**....) 
 * @author Pierpaolo Paris
 *
 */
@Entity
public class Review {
	private String _reviewId;
	private Date _date;
	private String _review;
	private ReviewValue _value;
	/**
	 * Costruttore vuoto
	 */
	public Review(){
		super();
	}
	/**
	 * Crea una nuova recensione in base ai parametri forniti
	 * @param reviewId l'id della recensione
	 * @param date la data della recensione
	 * @param review la recensione "vera e propria", es. "Questo posto e' una fetecchia"
	 * @param value Il valore "a stelle" della recensione
	 */
	public Review(String reviewId,Date date, String review,ReviewValue value){
		this();
		_reviewId = reviewId;
		_date = date;
		_review = review;
		_value = value;
	}
	@Id
	@Column(name="ReviewId")
	/**
	 * Riporta un Id per la recensione
	 * @return l'id della recensione
	 */
	public String getReviewId(){
		return _reviewId;
	}
	/**
	 * Imposta un nuovo Id per la recensione
	 * @param reviewId il nuovo id
	 */
	public void setReviewId(String reviewId){
		_reviewId = reviewId;
	}
	/**
	 * Riporta un voto per la recensione
	 * @return il voto della recensione
	 */
	@Column(name="value")
	public ReviewValue getValue() {
		return _value;
	}
	/**
	 * Imposta un nuovo voto per la recensione
	 * @param value il nuovo voto
	 */
	public void setValue(ReviewValue value){
		_value = value;
	}
	/*
	 * QUESTO da dove esce fuori? Non mi ricordo di averlo scritto
	 */
	public void setValue(String reviewId,ReviewValue value) {
		_value = value;
		_reviewId = reviewId;
	}
	/**
	 * Riporta la data della recensione
	 * @return la data della recensione
	 */
	@Column(name="date")
	public Date getDate(){
		return _date;
	}
	/**
	 * Imposta una nuova data per la recensione
	 * @param date la nuova data
	 */
	public void setDate(Date date){
		_date = date;
	}
	/**
	 * Riporta la descrizione della recensione
	 * @return la descrizione
	 */
	@Column(name="review")
	public String getReview(){
		return _review;
	}
	/**
	 * Imposta una nuova descrizione per la recensione
	 * @param review la nuova recensione
	 */
	public void setReview(String review){
		_review = review;
	}
	public boolean equals(Object o){
		Review r=(Review)o;
		//||(this.getReview().equals(r.getReview())&&this.getValue().equals(r.getValue())&&(this.getDate().compareTo(r.getDate())==0))
		return this.getReviewId().equals(r.getReviewId());
	}
	public int hashCode(){
		return this.getReviewId().hashCode();
	}
	
	/**
	 * Ottiene una stringa contenente le informazioni relative a una review.
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder("Review: [ ");
		sb.append("ID: " + this.getReviewId() + " , ");
		sb.append("date: " + this.getDate().toString() + " , " );
		sb.append("rating: " + this.getValue().toString() + " , ");
		sb.append("title: '" + this.getReview() + "'");
		sb.append(" ]");
		return sb.toString();
	}
	
}