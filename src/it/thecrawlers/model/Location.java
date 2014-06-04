package it.thecrawlers.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

/**
 * Classe che modella un luogo. Un luogo può averne altri al suo interno
 * (Es. Italia->Lazio->Provincia di Roma->...). Questa classe ci permette di scendere al livello di
 * dettaglio che vogliamo (anche a livello di singolo quartiere)
 * @author Pierpaolo Paris
 *
 */
@Entity
public class Location {
	private String _locationId;
	private String _description;
	/*
	 * non so se ci può servire, ma teniamolo. A toglierlo/non usarlo si fa sempre in tempo
	 * Ci può servire per fare delle interrogazioni
	 */
	private Set<Location> _subLocations;
	private Set<Item> _items;
	/**
	 * Costruttore vuoto
	 */
	public Location(){
		super();
		_subLocations = new HashSet<Location>();
		_items = new HashSet<Item>();
	}
	/**
	 * Costruisce una nuova Location in base ai parametri forniti
	 * @param locationId l'id della location (es. d5467)
	 * @param description La descrizione (es. Lupesco)
	 */
	public Location(String locationId,String description){
		this();
		_locationId = locationId;
		_description = description;
	}
	@Id
	@Column(name="LocationId")
	/**
	 * Riporta l'identificatore del luogo
	 * @return l'id del luogo
	 */
	public String getLocationId() {
		return _locationId;
	}
	/**
	 * Imposta un nuovo id per il luogo
	 * @param locationId il nuovo Id
	 */
	public void setLocationId(String locationId) {
		_locationId = locationId;
	}
	/**
	 * Riporta la descrizione del luogo (es. Saracinesco)
	 * @return la descrizione
	 */
	public String getDescription() {
		return _description;
	}
	/**
	 * Imposta una nuova descrizione per il luogo
	 * @param description la nuova descrizione
	 */
	public void setDescription(String description) {
		_description = description;
	}
	@OneToMany(
			fetch= FetchType.EAGER
			)
	@JoinTable(
			name="SubLocations",
			joinColumns=@JoinColumn(name="subLocation"),
			inverseJoinColumns=@JoinColumn(name="supLocation")
			)
	/**
	 * Riporta i sottoluoghi "immediati" di un luogo. Ad esempio, subLocations() richiamato su
	 * Lazio riporterà un insieme contenente le province di Roma, Viterbo, Latina, Rieti, Frosinone.
	 * @return
	 */
	public Set<Location> getSubLocations() {
		return _subLocations;
	}
	/**
	 * Imposta un nuovo insieme di sottoluoghi
	 * @param subLocations il nuovo insieme di sottoluoghi.
	 */
	public void setSubLocations(Set<Location> subLocations) {
		_subLocations = subLocations;
	}
	@OneToMany(
			fetch = FetchType.EAGER
			)
	@JoinTable(
			name="Items",
			joinColumns=@JoinColumn(name="LocationId"),
			inverseJoinColumns=@JoinColumn(name="ItemId")
			)
	/**
	 * Riporta gli item contenuti in un luogo
	 * @return Items
	 */
	public Set<Item> getItems() {
		Set<Item> items = new HashSet<Item>();
		for (Location l: _subLocations)
			items.addAll(l.getItems());
		items.addAll(_items);
		return items;
	}
	/**
	 * Imposta un nuovo insieme di items
	 * @param items il nuovo insieme
	 */
	public void setItems(Set<Item> items) {
		_items = items;
	}	
}
