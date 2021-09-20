package models.types;

public enum TemplateType {

    TEMPLATE_1("template-1"),
    TEMPLATE_2("template-2"),
    TEMPLATE_3("template-3");

    public final String templateName;

    TemplateType(String templateName) {
        this.templateName = templateName;
    }

}
