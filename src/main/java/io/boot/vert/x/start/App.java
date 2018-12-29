package io.boot.vert.x.start;

import boot.start.MyVerticle;
import boot.start.ResourceVerticle;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		ConfigStoreOptions fileStore = new ConfigStoreOptions().setType("file")
				.setConfig(new JsonObject().put("path", "config.json"));
		Vertx vertx = Vertx.vertx();

		ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
		ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
		retriever.getConfig(ch -> {
			if (ch.failed()) {
				System.out.println(ch.cause());
			} else {
				JsonObject option = ch.result();
				vertx.deployVerticle(MyVerticle.class.getName());
				DeploymentOptions deployOption = new DeploymentOptions(option);
				vertx.deployVerticle(ResourceVerticle.class.getName(), deployOption);
			}
		});

		System.out.println("deploy verticles done !");
	}

}
