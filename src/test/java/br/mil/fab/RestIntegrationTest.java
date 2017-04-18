package br.mil.fab;

import com.jayway.restassured.RestAssured;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.get;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;


public class RestIntegrationTest {

    @BeforeClass
    public static void configureRestAssured() {


        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;

        System.out.println("Blablalba");
    }

    @AfterClass
    public static void unconfigureRestAssured() {
        RestAssured.reset();
    }

    @Test
    public void checkThatCanSelectByTableAndId() {

        get("/api/select/t_pesfis_comgep_dw").then()
                .assertThat()
                .statusCode(200);

    }
}
