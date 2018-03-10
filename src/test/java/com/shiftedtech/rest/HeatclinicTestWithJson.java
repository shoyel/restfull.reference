package com.shiftedtech.rest;

import com.google.gson.Gson;
import com.shiftedtech.model.Product;
import com.shiftedtech.model.SearchResult;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;

//import static io.restassured.path.xml.XmlPath.from;
import static io.restassured.path.json.JsonPath.with;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.StringContains.containsString;

/**
 * Created by PaxoStudent on 3/4/2018.
 */
public class HeatclinicTestWithJson {

    @BeforeClass
    public void beforeClass(){
        RestAssured.baseURI = "http://heatclinic.shiftedtech.com";
        RestAssured.port = 80;
        RestAssured.basePath = "/api/v1/";
    }


    @Test
    public void test1(){
        RequestSpecification request = given()
                .log().all()
                .param("q", "hot")
                .param("pageSize", 5)
                .param("page", 1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        Response response = request.when().get("/catalog/search");

        String jsonString = response.asString();
        System.out.println(jsonString);

        int totalPages = with(jsonString).get("totalPages");
        System.out.println("totalPages:" + totalPages);
        assertThat(totalPages,equalTo(3));

        String  productName = with(jsonString).get("product[0].name");
        System.out.println("productName: " + productName );
        assertThat(productName,equalToIgnoringCase("Bull Snort Smokin' Toncils Hot Sauce"));

        String  retailPrice = with(jsonString).get("product[0].retailPrice.amount");
        System.out.println(retailPrice);
        assertThat(retailPrice,equalToIgnoringCase("3.99"));

    }

    @Test
    public void test1_1(){
        RequestSpecification request = given()
                .log().all()
                .param("q", "hot")
                .param("pageSize", 5)
                .param("page", 1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        Response response = request.log().all()
                            .when().get("/catalog/search");

        String jsonString = response.asString();
        JsonPath jsonPath = new JsonPath(jsonString);

        int totalPages =jsonPath.get("totalPages");
        System.out.println("totalPages:" + totalPages);
        assertThat(totalPages,equalTo(3));

        String  productName = jsonPath.get("product[0].name");
        System.out.println("productName: " + productName );
        assertThat(productName,equalToIgnoringCase("Bull Snort Smokin' Toncils Hot Sauce"));

        String  retailPrice = jsonPath.get("product[0].retailPrice.amount");
        System.out.println(retailPrice);
        assertThat(retailPrice,equalToIgnoringCase("3.99"));

    }


    @Test
    public void test2(){
        RequestSpecification request = given()
                .log().all()
                .param("q", "hot")
                .param("pageSize", 5)
                .param("page", 1)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);

        Response response = request.when().get("/catalog/search");

        String jsonString = response.asString();
        System.out.println(jsonString);

        // http://www.jsonschema2pojo.org/


        Gson gson = new Gson();
        SearchResult searchResult =   gson.fromJson(jsonString, SearchResult.class);
        Assert.assertNotNull(searchResult);

        assertThat(searchResult.getTotalPages(), equalTo(3));

        List<Product> list = searchResult.getProduct();
        for(Product p : list){
            System.out.println(p);
        }

        String productName = searchResult.getProduct().get(0).getName();
        System.out.println("Name: " + productName);
        assertThat(productName, equalToIgnoringCase("Bull Snort Smokin' Toncils Hot Sauce"));

        String retailPrice  = searchResult.getProduct().get(0).getRetailPrice().getAmount();
        assertThat(retailPrice,equalToIgnoringCase("3.99"));

    }
}
