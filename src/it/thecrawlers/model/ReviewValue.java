package it.thecrawlers.model;
/**
 * Enumerazione che modella i voti di una recensione
 * (*: veryBad,**: bad,***: medium,****: good,*****: veryGood)
 * @author Pierpaolo Paris
 *
 */
public enum ReviewValue {
	veryBad (1){
		public String toString(){
			return "Very bad";
		}
	}, bad (2){
		public String toString(){
			return "Bad";
		}
	},
	medium (3){
		public String toString(){
			return "Medium";
		}
	},
	good (4){
		public String toString(){
			return "Good";
		}
	}, veryGood (5){
		public String toString(){
			return "Very good";
		}
	};//altrimenti partirebbe da zero
	private final int _value;
	ReviewValue(int value){
		_value = value;
	}
	public int getValue() {
		return _value;
	}
	public boolean equals(ReviewValue rv){
		return rv.getValue()==this.getValue();
	}
}
