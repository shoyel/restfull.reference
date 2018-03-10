package com.shiftedtech.rest;

import io.restassured.path.json.JsonPath;
import org.testng.annotations.BeforeClass;
import com.google.gson.Gson;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;

import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.with;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by PaxoStudent on 3/4/2018.
 */
public class ShiftVisionTest {

    private String authToken = null;
    private String createdUserId = null;

    @BeforeClass
    public void beforeClass(){

        //  http://heatclinic.shiftedtech.com:80/api/v1/
       RestAssured.baseURI = "https://stage.video.shiftvision.com";
       RestAssured.port = 443;
       RestAssured.basePath = "/api/";
    }

    @Test(priority = 0)
    public void loginTest(){
        RequestSpecification requestSpecification = RestAssured.given();
        requestSpecification.contentType(ContentType.JSON);
        requestSpecification.accept(ContentType.JSON);
        requestSpecification.body("{\"email\":\"shiftqa01@gmail.com\",\"password\":\"Shift6786!\"}");

        Response response = requestSpecification.relaxedHTTPSValidation().post("/auth/login");
        String jsonString = response.asString();
        System.out.println(jsonString);
        String authToken = with(jsonString).get("token");
        System.out.println("AuthToken:  " + authToken);
        assertThat(authToken, not(isEmptyOrNullString()));
        this.authToken = authToken;
    }

    @Test(dependsOnMethods = {"loginTest"}, priority = 1)
    public void getAllUser(){
        RequestSpecification request = given()
                .log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(new Header("Authorization","Bearer " + this.authToken));

        Response response = request.relaxedHTTPSValidation().get("user/listuser");
        String jsonString = response.asString();
        System.out.println(jsonString);

        JsonPath jsonPath = new JsonPath(jsonString);

        int usercount = jsonPath.get("usercount");
        Assert.assertEquals(usercount,2);

        String username = jsonPath.get("user[0].username");
        Assert.assertEquals(username,"ShiftQA01");

        //Gson gson = new Gson();
        //Users users =   gson.fromJson(jsonString, Users.class);

        //Assert.assertEquals(users.getUsercount(),new Integer(10));
        //Assert.assertEquals(users.getUser().size(),3);
        //Assert.assertEquals(users.getUser().get(0).getFirstName(),"mushfiq_admin");

    }

    @Test(dependsOnMethods = {"loginTest"}, priority = 2)
    public void createUser(){
        RequestSpecification requestSpecification = given()
                .log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(new Header("Authorization","Bearer " + this.authToken))
                .body(new File(System.getProperty("user.dir") + "/src/test/resources/createuser.json"));

        Response response = requestSpecification.relaxedHTTPSValidation().post("user/create");
        assertThat(response.statusCode(), equalTo(201));

        String jsonString = response.asString();
        System.out.println(jsonString);

        String userId = with(jsonString).get("user.id").toString();
        System.out.println("User created with id: " + userId);
        this.createdUserId = userId;
    }


    @Test(dependsOnMethods = {"loginTest"}, priority = 3)
    public void getUser(){
        RequestSpecification request =  given()
                .log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(new Header("Authorization","Bearer " + this.authToken))
                .param("user_id",this.createdUserId);

        Response response = request.relaxedHTTPSValidation().get("user/read");
        String jsonString = response.asString();
        System.out.println(jsonString);
        JsonPath jsonPath = new JsonPath(jsonString);

        String username = jsonPath.get("user.username");
        Assert.assertEquals(username,"test01");
    }

    @Test(dependsOnMethods = {"loginTest","createUser","getUser"}, priority = 4)
    public void updateUser() throws IOException {

        File file = new File(System.getProperty("user.dir") + "/src/test/resources/updateuser.json");
        String inStr = new String(Files.readAllBytes(file.toPath()));
        String updateUserPostPayload = String.format(inStr, new Integer(createdUserId),"Ameera2","Iftekhar2");

        Response response = given()
                .log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(new Header("Authorization","Bearer " + this.authToken))
                .body(updateUserPostPayload)
                .when().relaxedHTTPSValidation()
                .put("user/update");

        String jsonString = response.asString();
        String firstName = with(jsonString).get("user.first_name");
        System.out.println("FirstName: " + firstName);
        assertThat(firstName, equalToIgnoringCase("Ameera2"));
    }

    @Test(dependsOnMethods = {"loginTest","createUser","getUser","updateUser"}, priority = 5)
    public void deteleUser(){
        given()
                .log().all()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .header(new Header("Authorization","Bearer " + this.authToken))
                .when().relaxedHTTPSValidation()
                .delete("user/delete/" + this.createdUserId)
                .then()
                .log().body()
                .statusCode(200);
    }
}
