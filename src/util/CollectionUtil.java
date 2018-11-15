package util;

import java.util.List;

public class CollectionUtil {
	public static <T> T getLast(List<T> list) {
		return list.get(list.size() - 1);
	}
}
