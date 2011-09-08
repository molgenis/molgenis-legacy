package lifelines.matrix;

import java.util.List;
import java.util.Set;

public class SQLUtils {
	public static int[] toArray(List<Integer> list) {
		int[] result = new int[list.size()];
		for(int i = 0; i < result.length; ++i) {
			result[i] = list.get(i);
		}
		return result;
	}

	public static String escapeSql(String sql) {
		StringBuilder sb = new StringBuilder();
		String[] parts = sql.split(",");
		for(int i = 0; i < parts.length; ++i) {
			sb.append("'");
			sb.append(parts[i]);
			sb.append("'");
			if(i + 1 < parts.length) {
				sb.append(",");
			}
		}
		return sb.toString();

	}	
	
	
	public static String toSqlArray(List list) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < list.size(); ++i) {
			sb.append(list.get(i).toString());
			if(i + 1 < list.size()) {
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static String toSqlArray(Set set) {
		StringBuilder sb = new StringBuilder();
		int idx = 0;
		for(Object o : set) {
			sb.append(o.toString());
			if(idx + 1 < set.size()) {
				sb.append(",");
			}
			++idx;
		}
		return sb.toString();
	}
}