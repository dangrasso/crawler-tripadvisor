package it.thecrawlers.model.comparators;

import it.thecrawlers.model.Review;

import java.util.Comparator;
/**
 * Compara le recensioni in base alla data
 * @author Pierpaolo Paris
 *
 */
public class CompareReviewByDate implements Comparator<Review> {
	@Override
	public int compare(Review r1, Review r2) {
		return r1.getDate().compareTo(r2.getDate());
	}

}
