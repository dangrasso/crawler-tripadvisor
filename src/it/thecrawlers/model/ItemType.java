package it.thecrawlers.model;
/**
 * Enumerazione che elenca i tipi di Item da recensire.
 * Valori possibili: hotel, restaurant, attraction
 * @author Pierpaolo Paris
 *
 */
public enum ItemType {
	hotel{
		public String toString(){
			return "Hotel";
		}
	},
	restaurant{
		public String toString(){
			return "Restaurant";
		}
	},
	attraction{
		public String toString(){
			return "Attraction";
		}
	}
}
