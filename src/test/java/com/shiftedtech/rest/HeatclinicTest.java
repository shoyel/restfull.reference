package com.shiftedtech.rest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;

import static io.restassured.path.xml.XmlPath.from;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;
/**
 * Created by PaxoStudent on 3/3/2018.
 */
public class HeatclinicTest {

    @BeforeClass
    public void beforeClass(){
        RestAssured.baseURI = "http://heatclinic.shiftedtech.com";
        RestAssured.port = 80;
        RestAssured.basePath = "/api/v1/";
    }


    @Test
    public void test1(){
        RequestSpecification requestSpecification = RestAssured.given();

        requestSpecification.param("q","hot");

        requestSpecification.accept(ContentType.JSON);
        requestSpecification.contentType(ContentType.JSON);

        Response response = requestSpecification.get("/catalog/search");

        int statusCode =  response.statusCode();
        Assert.assertEquals(statusCode,200);
        Assert.assertEquals(response.contentType(),"application/json;charset=UTF-8");
    }

    @Test
    public void test2(){
        // io.restassured.RestAssured.given();

        given()
            .log().all()
            .param("q", "hot")
            .contentType(ContentType.XML)
            .accept(ContentType.XML)
        .when()
             .get("/catalog/search")
        .then()
             .log().body()
             .statusCode(200);

    }
    @Test
    public void searchTestProducts(){
        given()
                .log().all()
                .param("q", "hot")
                .contentType(ContentType.XML)
                .accept(ContentType.XML)
        .when()
                .get("/catalog/search")
        .then()
                .log().body()
                .statusCode(200)
                .contentType(ContentType.XML)
                .body(hasXPath("boolean(/searchResults)",containsString("true")))
                .body(hasXPath("/searchResults/totalResults",containsString("14")))
                .body(hasXPath("count(/searchResults/products/product)",equalTo("14")))
                .body(hasXPath("/searchResults/products/product[1]/id",containsString("13")))
                .body(hasXPath("/searchResults//product[1]/id",containsString("13")));
    }
    @Test
    public void searchTestProducts3() {
        RequestSpecification request = given()
                .log().all()
                .param("q", "hot")
                .contentType(ContentType.XML)
                .accept(ContentType.XML);

        Response response = request.when().get("/catalog/search");

        String body = response.asString();
        System.out.println(body);
        List<String> products =  from(body).getList("searchResults.products.product.name");
        System.out.println("Product Count: " + products.size());
        assertThat(products.size(),equalTo(14));
        String[] expectedProduct = {"Bull Snort Smokin' Toncils Hot Sauce",
                "Hoppin' Hot Sauce",
                "Roasted Garlic Hot Sauce",
                "Scotch Bonnet Hot Sauce",
                "Hurtin' Jalepeno Hot Sauce",
                "Blazin' Saddle XXX Hot Habanero Pepper Sauce",
                "Dr. Chilemeister's Insane Hot Sauce",
                "Cool Cayenne Pepper Hot Sauce",
                "Day of the Dead Habanero Hot Sauce",
                "Day of the Dead Chipotle Hot Sauce",
                "Armageddon The Hot Sauce To End All",
                "Bull Snort Cowboy Cayenne Pepper Hot Sauce",
                "Roasted Red Pepper & Chipotle Hot Sauce",
                "Day of the Dead Scotch Bonnet Hot Sauce"};

        String[] actualProducts = new String[products.size()];
        actualProducts = products.toArray(actualProducts);
        System.out.println("Actual products:" + Arrays.toString(actualProducts));

        assertThat(expectedProduct,arrayContainingInAnyOrder(actualProducts));

    }


    @AfterClass
    public void afterClass(){

    }
}
