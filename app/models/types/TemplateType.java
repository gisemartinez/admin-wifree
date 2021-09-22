package models.types;

import scala.Tuple2;
import scala.collection.Seq;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static scala.collection.JavaConverters.asScalaBuffer;

public enum TemplateType {

    TEMPLATE_1("template-1", "Tema 1"), // Columnas
    TEMPLATE_2("template-2", "Tema 2"); // iframe

    public final String templateId;
    public final String name;

    TemplateType(String templateId, String name) {
        this.templateId = templateId;
        this.name = name;
    }

    public static Seq<Tuple2<String, String>> templatesTypes() {
        List<Tuple2<String, String>> collect = Stream.of(TEMPLATE_1, TEMPLATE_2).map(TemplateType::toTuple).collect(Collectors.toList());
        return asScalaBuffer(collect).toList();
    }

    private static Tuple2<String, String> toTuple(TemplateType lm) {
        return Tuple2.apply(lm.toString(), lm.name);
    }

}
