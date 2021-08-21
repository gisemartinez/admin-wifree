package utils;

import scala.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * Created by jesu on 08/06/17.
 */
public class StringHelper {

	public static String toLogString(String... attributes) {
		String logString;
		try {
			logString = StringHelper.toParagraph(attributes);
		} catch (Exception ignored) {
			logString = Arrays.toString(attributes);
		}
		return logString;
	}

	public static String toParagraph(String... lines) {
		String paragraph = "";
		if ( lines != null )
			paragraph = toParagraph(Arrays.stream(lines));
		return paragraph;
	}

	public static String toParagraph(Collection<String> lines) {
		String paragraph = "";
		if ( lines != null )
			paragraph = toParagraph(lines.stream());
		return paragraph;
	}

	public static String toParagraph(Stream<String> lines) {
		String paragraph = "";
		if ( lines != null )
			paragraph = lines.reduce( (l1, l2) -> l1 + "\n" + l2).orElse("");
		return paragraph;
	}
	
	public static boolean isEmpty(String string) {
		return string == null || string.isEmpty() || string.trim().isEmpty();
	}
	
	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}
	
	public static Tuple2<String, String> splitHyphen(String string) {
		return split(string, "-");
	}
	
	public static Tuple2<String, String> splitBlank(String string) {
		return split(string, " ");
	}
	
	public static Tuple2<String, String> split(String string, String character) {
		final String[] splitted = string.split(character);
		return Tuple2.apply(splitted[0], splitted[1]);
	}

}
