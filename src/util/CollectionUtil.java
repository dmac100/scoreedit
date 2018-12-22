package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionUtil {
	public static <T> T getFirst(List<T> list) {
		return list.get(0);
	}
	
	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}
	
	public static <A> List<A> filter(Collection<A> collection, Predicate<A> f) {
		return collection.stream().filter(f).collect(Collectors.toList());
	}
	
	public static <A, B> List<B> map(Collection<A> collection, Function<A, B> f) {
		return collection.stream().map(f).collect(Collectors.toList());
	}
	
	public static <A, B> Collection<B> flatMap(List<A> collection, Function<A, Collection<B>> f) {
		return collection.stream().flatMap(a -> f.apply(a).stream()).collect(Collectors.toList());
	}
	
	public static <A extends Comparable<? super A>> A max(Collection<A> collection) {
		return collection.stream().max(Comparator.naturalOrder()).get();
	}

	public static <T> List<T> concat(List<T> list1, List<T> list2) {
		List<T> list = new ArrayList<>();
		list.addAll(list1);
		list.addAll(list2);
		return list;
	}

	public static int sum(List<Integer> list) {
		int sum = 0;
		for(Integer item:list) {
			sum += item;
		}
		return sum;
	}
}