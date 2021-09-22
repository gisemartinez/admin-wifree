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
public enum PortalApplicationType {

	Carrousel("carrousel", "Carrousel"),
	Link("link", "Link"),
	SocialMediaWidget("socialMediaWidget", "Widget"),
	Image("image", "Imagen"),
	Text("text", "Texto"),
	News("news", "noticias"),
	TemplateOne("template-1", "Tema 1"),
	TemplateTwo("template-2", "Tema 2");

	public final String id;
	public final String name;

	PortalApplicationType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public static Seq<Tuple2<String, String>> appTypes() {
		List<Tuple2<String, String>> collect = Stream.of(TemplateOne, TemplateTwo)
				.map(PortalApplicationType::toTuple)
				.collect(Collectors.toList());
		return asScalaBuffer(collect).toList();
	}

	private static Tuple2<String, String> toTuple(PortalApplicationType lm) {
		return Tuple2.apply(lm.toString(), lm.name);
	}

}
