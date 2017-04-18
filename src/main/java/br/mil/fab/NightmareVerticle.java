package br.mil.fab;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Set;


public class NightmareVerticle extends AbstractVerticle {

    //TODO: FIND A BETTER WAY TO CONFIG IP AND PORT
    private static final String HTTP_HOST = "0.0.0.0";
    private static final int HTTP_PORT = 8080;

    @Override
    public void start(Future<Void> fut) {



        Router router = Router.router(vertx);

        Set<String> allowHeaders = new HashSet<>();
        allowHeaders.add("x-requested-with");
        allowHeaders.add("Access-Control-Allow-Origin");
        allowHeaders.add("origin");
        allowHeaders.add("Content-Type");
        allowHeaders.add("accept");

        Set<HttpMethod> allowMethods = new HashSet<>();
        allowMethods.add(HttpMethod.GET);
        allowMethods.add(HttpMethod.POST);
        allowMethods.add(HttpMethod.DELETE);
        allowMethods.add(HttpMethod.PUT);

        router.route().handler(CorsHandler.create("*")
                .allowedHeaders(allowHeaders)
                .allowedMethods(allowMethods));

        router.route().handler(BodyHandler.create());


        router.get("/api/select/:table_name").handler(rc -> {

            String table_name = rc.request().getParam("table_name");

            rc.response().putHeader("content-type", "application/json")
                    .end(new JsonObject().put("table_name", table_name).encode());
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(
                HTTP_PORT, HTTP_HOST, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }
}
