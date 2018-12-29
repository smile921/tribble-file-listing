/**
 * 
 */
package boot.start;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.healthchecks.HealthCheckHandler;
import io.vertx.ext.healthchecks.Status;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.StaticHandler;

/**
 * @author Frere921
 *
 */
public class ResourceVerticle extends AbstractVerticle {

	@Override
	public void start() {
		String index = config().getString("index", "index.html");
		String template = config().getString("template", "template.html");
		String root = config().getString("root", "webroot");
		String mavenRepo = config().getString("mavenRepo", "webroot");
		String gradleRepo = config().getString("gradleRepo", "webroot");
		int port = config().getInteger("port", 888);
		HttpServer server = vertx.createHttpServer();
		Router router = Router.router(vertx);
		HealthCheckHandler checkHandler = HealthCheckHandler.create(vertx);
		checkHandler.register("resource health check", future -> {
			future.complete(Status.OK());

		});
		router.route("/ping").handler(checkHandler);
		Handler<RoutingContext> requestHandler = new Handler<RoutingContext>() {

			@SuppressWarnings("unused")
			@Override
			public void handle(RoutingContext event) {
				InetAddress addr;
				try {
					addr = InetAddress.getLocalHost();
					String ip = addr.getHostAddress().toString(); // 获取本机ip
					String hostName = addr.getHostName().toString(); // 获取本机计算机名称
					String json = "{\"success\":true,\"data\":{\"ip\":\"%s\",\"hostname\":\"%s\"}}";

					event.response().putHeader("Content-Type", "application/json;charset=utf-8")
							.end(String.format(json, ip, hostName));
				} catch (UnknownHostException e) {
					e.printStackTrace();
					event.response().putHeader("Content-Type", "application/json")
							.end("{success:false,message:'发生异常'}");
				}

			}
		};
		router.route("/ip").handler(requestHandler);
		
		
		StaticHandler whandler = StaticHandler.create();
		whandler.setDirectoryListing(true);
		whandler.setFilesReadOnly(true);
		whandler.setEnableFSTuning(true);
		whandler.setIndexPage(index);
		whandler.setDefaultContentEncoding("utf-8");
		whandler.setWebRoot("./");
		router.route("/static/*").handler(whandler);

		StaticHandler handler = StaticHandler.create();
		handler.setAllowRootFileSystemAccess(true);
		handler.setDirectoryListing(true);
		handler.setFilesReadOnly(true);
		handler.setEnableFSTuning(true);
		handler.setIncludeHidden(true);
		handler.setIndexPage(index);
		handler.setDefaultContentEncoding("utf-8");
		handler.setWebRoot(root);
		handler.setDirectoryTemplate(template);
		router.route("/files/*").handler(handler);


		StaticHandler mavenHandler = StaticHandler.create();
		mavenHandler.setAllowRootFileSystemAccess(true);
		mavenHandler.setDirectoryListing(true);
		mavenHandler.setFilesReadOnly(true);
		mavenHandler.setEnableFSTuning(true);
		mavenHandler.setIncludeHidden(true);
		mavenHandler.setIndexPage(index);
		mavenHandler.setDefaultContentEncoding("utf-8");
		mavenHandler.setWebRoot(mavenRepo);
		mavenHandler.setDirectoryTemplate(template);
		router.route("/maven/*").handler(mavenHandler);

		StaticHandler gradleHandler = StaticHandler.create();
		gradleHandler.setAllowRootFileSystemAccess(true);
		gradleHandler.setDirectoryListing(true);
		gradleHandler.setFilesReadOnly(true);
		gradleHandler.setEnableFSTuning(true);
		gradleHandler.setIncludeHidden(true);
		gradleHandler.setIndexPage(index);
		gradleHandler.setDefaultContentEncoding("utf-8");
		gradleHandler.setWebRoot(mavenRepo);
		gradleHandler.setDirectoryTemplate(template);
		router.route("/gradle/*").handler(gradleHandler);

		server.requestHandler(router::accept).listen(port 
		/*
		 * ,future ->{ if(future.failed()){ future.otherwiseEmpty();
		 * }else{ HttpServer res = future.result();
		 * 
		 * } }
		 */);
		System.out.println(String.format("server started at %d with root %s", port, root));
	}

}
