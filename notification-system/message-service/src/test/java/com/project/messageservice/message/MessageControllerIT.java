package com.project.messageservice.message;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Testcontainers;
import tools.jackson.databind.ObjectMapper;

import com.project.messageservice.message.dto.CreateMessageRequest;
import com.project.messageservice.message.dto.MessageResponse;
import com.project.messageservice.message.repository.LabelRepository;
import com.project.messageservice.message.repository.MessageRepository;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class MessageControllerIT {

    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    ObjectMapper objectMapper;

    private static final UUID senderId = UUID.fromString("df3c5cd0-12ea-4e32-aa17-6f70289b5dd1");
    private static final UUID recipientId = UUID.fromString("fab6e5df-15e7-4020-acb8-f3274ec5871e");
    private static final String body = "Automated test message";

    @BeforeEach
    void cleanDatabase() {
        messageRepository.deleteAll();
        labelRepository.deleteAll();
    }

    @BeforeAll
    static void setup() {
        postgres.start();
    }

    @AfterAll
    static void tearDown() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    MessageResponse createMessage(CreateMessageRequest request) throws Exception {

        String recipientId = request.recipientId().toString();
        String senderId = request.senderId().toString();
        String body = request.body();

        return RestAssured.given()
                .port(port)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(request))
                .when()
                .post("/messages")
                .then()
                .statusCode(201)
                .body("senderId", equalTo(senderId))
                .body("recipientId", equalTo(recipientId))
                .body("body", equalTo(body))
                .extract()
                .as(MessageResponse.class);

    }

    void addLabel(UUID messageId, String labelName) {
        RestAssured.given()
                .port(port)
                .when()
                .put("/messages/{id}/labels/{labelName}", messageId, labelName)
                .then()
                .statusCode(204);
    }

    @Test
    void createMessage_returnsCreated() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        createMessage(request);
    }

    @Test
    void getallMessages_returnsAllMessages() throws Exception {
        CreateMessageRequest intialRequest = new CreateMessageRequest(senderId, recipientId, "Initial message");
        CreateMessageRequest followUpRequest = new CreateMessageRequest(senderId, recipientId, "Follow up message");

        MessageResponse initialMessage = createMessage(intialRequest);
        MessageResponse followUpMessage = createMessage(followUpRequest);

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages")
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("body", hasItems(initialMessage.body(), followUpMessage.body()))
                .body("senderId", everyItem(equalTo(senderId.toString())))
                .body("recipientId", everyItem(equalTo(recipientId.toString())));

    }

    @Test
    void getMessage_returnsMessage() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        MessageResponse message = createMessage(request);

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages/{id}", message.id())
                .then()
                .statusCode(200)
                .body("body", equalTo(body))
                .body("senderId", equalTo(senderId.toString()))
                .body("recipientId", equalTo(recipientId.toString()));

    }

    @Test
    void readMessage_marksMessageAsRead() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        MessageResponse message = createMessage(request);

        RestAssured.given()
                .port(port)
                .when()
                .patch("/messages/{id}/read", message.id())
                .then()
                .statusCode(204);

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages/{id}", message.id())
                .then()
                .statusCode(200)
                .body("body", equalTo(body))
                .body("senderId", equalTo(senderId.toString()))
                .body("recipientId", equalTo(recipientId.toString()))
                .body("read", equalTo(true));

    }

    @Test
    void acknowledgeMessage_marksMessageAsAcknowledged() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        MessageResponse message = createMessage(request);

        RestAssured.given()
                .port(port)
                .when()
                .patch("/messages/{id}/acknowledge", message.id())
                .then()
                .statusCode(204);

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages/{id}", message.id())
                .then()
                .statusCode(200)
                .body("body", equalTo(body))
                .body("senderId", equalTo(senderId.toString()))
                .body("recipientId", equalTo(recipientId.toString()))
                .body("acknowledged", equalTo(true));

    }

    @Test
    void addLabel_addsLabelToMessages() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        MessageResponse message = createMessage(request);

        String labelName = "Test Label";

        addLabel(message.id(), labelName);

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages/{id}/labels", message.id())
                .then()
                .statusCode(200)
                .body("size()", equalTo(1))
                .body("", hasItem(labelName));

    }

    @Test
    void deleteLabel_removesLabelFromMessages() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        MessageResponse message = createMessage(request);

        String labelName = "Test Label";

        addLabel(message.id(), labelName);

        RestAssured.given()
                .port(port)
                .when()
                .delete("/messages/{id}/labels/{labelName}", message.id(), labelName)
                .then()
                .statusCode(204);

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages/{id}/labels", message.id())
                .then()
                .statusCode(200)
                .body("size()", equalTo(0));

    }

     @Test
    void getLabels_retrievesAllLabels() throws Exception {

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);
        MessageResponse message = createMessage(request);

        String firstLabel = "Test Label";
        String secondLabel = "Informational";

        addLabel(message.id(), firstLabel);
        addLabel(message.id(), secondLabel);

       

        RestAssured.given()
                .port(port)
                .when()
                .get("/messages/{id}/labels", message.id())
                .then()
                .statusCode(200)
                .body("size()", equalTo(2))
                .body("", hasItems(firstLabel, secondLabel));

    }

   
}
