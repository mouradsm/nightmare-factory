package br.mil.fab;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.net.ServerSocket;

@RunWith(VertxUnitRunner.class)
public class NightmareVerticleTest {

    private Vertx vertx;


    private int port = 0;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        try {
            ServerSocket socket = new ServerSocket(0);
            port = socket.getLocalPort();
            socket.close();
        }catch (IOException ex){
            vertx.close(context.asyncAssertFailure());
        }

        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject().put("http.port", 8080));

        vertx.deployVerticle(NightmareVerticle.class.getName(),options, context.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void checkThatApplicationIsRunning(TestContext context) {
        Async async = context.async();

        vertx.createHttpClient().getNow(8080, "localhost", "/", response -> {
                    context.assertEquals(response.statusCode(), 200);
                    response.handler(body -> {
                        context.assertTrue(body.toString().contains("Hello"));
                        async.complete();
                    });
                });
    }

    public void checkThatWeCanSelectByTable(TestContext context) {
        Async async = context.async();
        vertx.createHttpClient().getNow(8080, "localhost", "/api/select/t_pesfis_comgep_dw", response -> {
           context.assertEquals(response.statusCode(), 200);
           context.assertTrue(response.headers().get("content-type").contains("application/json"));
        });

    }
}
