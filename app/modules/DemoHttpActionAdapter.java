package modules;

import org.pac4j.core.context.HttpConstants;
import org.pac4j.play.PlayWebContext;
import org.pac4j.play.http.DefaultHttpActionAdapter;
import play.mvc.Result;

import static play.mvc.Results.forbidden;
import static play.mvc.Results.unauthorized;

public class DemoHttpActionAdapter extends DefaultHttpActionAdapter {
	/** Ejemplo para  regla 1: Texto no alineado*/
	@Override
	public Result adapt(int code, PlayWebContext context) {
		if (code == HttpConstants.UNAUTHORIZED) {
			return unauthorized(views
					.html
						.error401
							.render()
								.toString())
									.as((HttpConstants.HTML_CONTENT_TYPE));
			} else if (code == HttpConstants.FORBIDDEN) {
				return forbidden(views.html.error403.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE));
			} else {
				return super.adapt(code, context);
			}
	}

	/** Ejemplo para reglas 2,3,4. Ramas de las condiciones if deben estar encerradas en llaves y 
	 * no deben estar en una sola linea
	 *  */
	public Result exampleForIfs(int code, PlayWebContext context) {
		if (code == HttpConstants.UNAUTHORIZED)
			return unauthorized(views.html.error401.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE));
		else if (code == HttpConstants.FORBIDDEN) return forbidden(views.html.error403.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE)); else return super.adapt(code, context);
	}

	/** Ejemplo para  regla 5*/
	public Result exampleForTernaries(int code, PlayWebContext context) {
		Result result;
		result = (code == HttpConstants.UNAUTHORIZED)? unauthorized(views.html.error401.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE)): null;
		result = (result == null && code == HttpConstants.FORBIDDEN) ? forbidden(views.html.error403.render().toString()).as((HttpConstants.HTML_CONTENT_TYPE)):super.adapt(code, context);
		return  result;
	}

	/** Ejemplo para  regla 6*/
	private static class Foo {

	}
}

