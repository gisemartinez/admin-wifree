package models.types;

import scala.Tuple2;
import scala.collection.Iterator;
import scala.collection.JavaConverters;
import scala.collection.Seq;
import scala.collection.convert.Decorators;

import static java.util.Arrays.asList;

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
		return loginMethods()
				.asScala()
				.map(LoginMethodType::toTuple)
				.toSeq();
	}

	private static Tuple2<String, String> toTuple(LoginMethodType lm) {
		return Tuple2.apply(lm.toString(), lm.name);
	}

	private static Decorators.AsScala<Iterator<LoginMethodType>> loginMethods() {
		return JavaConverters.asScalaIteratorConverter(
				asList(SocialLogin, Survey).iterator()
		);
	}

	}

