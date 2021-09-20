package models.types;

import scala.Tuple2;
import scala.collection.Seq;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static scala.collection.JavaConverters.asScalaBuffer;

/**
 * Created by jesu on 09/06/17.
 */
public enum LoginMethodType {

	Google("Google"),
	Facebook("Facebook"),
	Twitter("Twitter"),
	EmailAndPassword("Correo Electronico"),
	SocialLogin("Redes Sociales"),
	Survey("Encuesta");

	public final String name;

	LoginMethodType(String name) {
		this.name = name;
	}

	public static Seq<Tuple2<String, String>> portalLoginTypes() {
		List<Tuple2<String, String>> collect = Stream.of(SocialLogin, Survey).map(LoginMethodType::toTuple).collect(Collectors.toList());
		return asScalaBuffer(collect).toList();
	}

	public static Seq<Tuple2<String, String>> socialLoginTypes() {
		List<Tuple2<String, String>> collect = Stream.of(Google, Facebook).map(LoginMethodType::toTuple).collect(Collectors.toList());
		return asScalaBuffer(collect).toList();
	}

	private static Tuple2<String, String> toTuple(LoginMethodType lm) {
		return Tuple2.apply(lm.toString(), lm.name);
	}

}

