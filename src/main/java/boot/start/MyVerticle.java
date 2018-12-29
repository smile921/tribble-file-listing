package boot.start;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;

public class MyVerticle extends AbstractVerticle {
	
	public void start() {
		HttpServer server = vertx.createHttpServer();
		server.requestHandler(req -> {
			req.response().putHeader("content-type", "text/plain").end("Hello Vert.x !");
		});
		server.listen(8181);
	}

}