package br.mil.fab;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;

import java.util.HashSet;
import java.util.Set;


public class NightmareVerticle extends AbstractVerticle {

    //TODO: FIND A BETTER WAY TO CONFIG IP AND PORT
    private static final String HTTP_HOST = "0.0.0.0";
    private static final int HTTP_PORT = 8080;

    private Logger logger = LoggerFactory.getLogger("br.mil.fab.NightmareVerticle");
    private JDBCClient jdbcClient;

    @Override
    public void start(Future<Void> fut) {
        initData();


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

        //routes
        router.get(Constants.API_GET).handler(this::handlerGetById);
        //router.get(Constants.API_LIST_ALL).handler(this::handlerGetAll);
//        router.get(Constants.API_CREATE).handler(this::handlerCreate);
//        router.get(Constants.API_UPDATE).handler(this::handlerUpdate);
//        router.get(Constants.API_DELETE).handler(this::handlerDeleteOne);
//        router.get(Constants.API_DELETE_ALL).handler(this::handlerDeleteAll);

//        router.get("/api/select/:table_name").handler(rc -> {
//
//            String table_name = rc.request().getParam("table_name");
//
//            rc.response().putHeader("content-type", "application/json")
//                    .end(new JsonObject().put("table_name", table_name).encode());
//        });

        vertx.createHttpServer().requestHandler(router::accept).listen(
                HTTP_PORT, HTTP_HOST, result -> {
                    if (result.succeeded()) {
                        fut.complete();
                    } else {
                        fut.fail(result.cause());
                    }
                });
    }

    private void handlerGetById(RoutingContext context) {

        String table_name   = context.request().getParam("table_name");
        String columnNames  = context.request().getParam("columnNames") == null ? "*"
                : context.request().getParam("columnNames");

        String where = context.request().getParam("where");

        if(table_name == null) {
            sendError(400, context.response());
            logger.info("table name can't be Null" );
        }

        jdbcClient.getConnection(conn -> {
            if(conn.succeeded()) {
                SQLConnection connection = conn.result();

                StringBuilder sql = new StringBuilder();
                sql.append("SELECT ");
                sql.append(columnNames);
                sql.append(" FROM ");
                sql.append(table_name);
                sql.append(" WHERE ");
                sql.append(where);

                logger.info(sql);

                connection.query(sql.toString(), rs -> {

                    if(rs.succeeded()) {
                        ResultSet result = rs.result();
                        context.response()
                                .putHeader("content-type", "application/json")
                                .end(Json.encodePrettily(result));
                    }else {
                        sendError(400, context.response());
                    }
                });
            }
        });
    }

    private void sendError(int statusCode, HttpServerResponse response){
        response.setStatusCode(statusCode).end();
    }

    private void initData() {

        JsonObject config = new JsonObject()
                .put("url", "jdbc:oracle:thin:@10.52.132.91:1521:homolog")
                .put("driver_class", "oracle.jdbc.driver.OracleDriver")
                .put("user", "consulta")
                .put("password", "53con90")
                .put("max_pool_size", 30);

        this.jdbcClient = JDBCClient.createShared(vertx, config);
    }
}
