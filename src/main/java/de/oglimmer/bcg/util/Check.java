package de.oglimmer.bcg.util;

/**
 * Defines an arbitrary check.
 * 
 * @author oli
 * 
 * @param <E>
 */
public interface Check<E> {

	/**
	 * Returns true if the item o is "okay" (whatever that means)
	 * 
	 * @param o
	 * @return
	 */
	boolean isItemOkay(E o);
}
