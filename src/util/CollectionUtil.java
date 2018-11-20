package util;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtil {
	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}
	
	public static <A, B> List<B> map(Collection<A> collection, Function<A, B> f) {
		return collection.stream().map(f).collect(Collectors.toList());
	}
	
	public static <A extends Comparable<? super A>> A max(Collection<A> collection) {
		return collection.stream().max(Comparator.naturalOrder()).get();
	}
}
