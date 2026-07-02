package com.project.messageservice.message;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.project.messageservice.exception.MessageNotFoundException;
import com.project.messageservice.message.dto.CreateMessageRequest;
import com.project.messageservice.message.dto.MessageResponse;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    private static final UUID senderId = UUID.fromString("df3c5cd0-12ea-4e32-aa17-6f70289b5dd1");
    private static final UUID recipientId = UUID.fromString("fab6e5df-15e7-4020-acb8-f3274ec5871e");
    private static final String body = "Automated test message";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    private MessageService messageService;

    @Test
    void createMessage_withValidRequest_returnsNewMessage() throws Exception {

        UUID messageId = UUID.randomUUID();

        CreateMessageRequest request = new CreateMessageRequest(senderId, recipientId, body);

        when(messageService.createMessage(request))
                .thenReturn(new MessageResponse(messageId, senderId, recipientId, body, false, false, null));

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.senderId").value(senderId.toString()))
                .andExpect(jsonPath("$.recipientId").value(recipientId.toString()))
                .andExpect(jsonPath("$.body").value(body));
    }

    @Test
    void createMessage_withInvalidRequest_returnsBadRequest() throws Exception {

        ObjectNode requestJson = objectMapper.createObjectNode();

        requestJson.put("senderId", senderId.toString());
        requestJson.put("body", "A message with no recipient");

        mockMvc.perform(post("/messages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getMessage_whenMessageExists_returnsMessage() throws Exception {

        UUID messageId = UUID.randomUUID();

        when(messageService.getMessage(messageId))
                .thenReturn(new MessageResponse(messageId, senderId, recipientId, body, false, false, null));

        mockMvc.perform(get("/messages/{id}", messageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(messageId.toString()))
                .andExpect(jsonPath("$.senderId").value(senderId.toString()))
                .andExpect(jsonPath("$.recipientId").value(recipientId.toString()))
                .andExpect(jsonPath("$.body").value(body));
    }

    @Test
    void getMessage_whenMessageDoesNotExist_returnsNotFound() throws Exception {

        UUID messageId = UUID.randomUUID();

        when(messageService.getMessage(messageId))
                .thenThrow(new MessageNotFoundException(messageId));

        mockMvc.perform(get("/messages/{id}", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMessage_whenInvalidUUID_returnsBadRequest() throws Exception {

        mockMvc.perform(get("/messages/{id}", "123"))
                .andExpect(status().isBadRequest());

    }

    @Test
    void readMessage_whenMessageDoesNotExist_returnsNotFound() throws Exception {

        UUID messageId = UUID.randomUUID();

        doThrow(new MessageNotFoundException(messageId))
                .when(messageService)
                .readMessage(messageId);

        mockMvc.perform(patch("/messages/{id}/read", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void readMessage_whenMessageExists_returnsNoContent() throws Exception {

        UUID messageId = UUID.randomUUID();

        doNothing().when(messageService).readMessage(messageId);

        mockMvc.perform(patch("/messages/{id}/read", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    void acknowledgeMessage_whenMessageDoesNotExist_returnsNotFound() throws Exception {

        UUID messageId = UUID.randomUUID();

        doThrow(new MessageNotFoundException(messageId))
                .when(messageService)
                .acknowledgeMessage(messageId);

        mockMvc.perform(patch("/messages/{id}/acknowledge", messageId))
                .andExpect(status().isNotFound());
    }

    @Test
    void acknowledgeMessage_whenMessageExists_returnsNoContent() throws Exception {

        UUID messageId = UUID.randomUUID();

        doNothing().when(messageService).acknowledgeMessage(messageId);

        mockMvc.perform(patch("/messages/{id}/acknowledge", messageId))
                .andExpect(status().isNoContent());
    }

    @Test
    void getLabels_whenMessageExists_returnsLabels() throws Exception {

        UUID messageId = UUID.randomUUID();
        Set<String> labels = Set.of("Informational", "Test Label");;

        when(messageService.getMessageLabels(messageId)).thenReturn(labels);

        mockMvc.perform(get("/messages/{messageId}/labels", messageId))
                .andExpect(status().isOk()).andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", hasItem("Informational")))
                .andExpect(jsonPath("$", hasItem("Test Label")));

    }

    @Test
    void addLabel_whenMessageExists_returnsNoContent() throws Exception {

        UUID messageId = UUID.randomUUID();
        String labelName = "Test Label";

        doNothing().when(messageService).addLabel(messageId, labelName);

        mockMvc.perform(put("/messages/{messageId}/labels/{labelName}", messageId, labelName))
                .andExpect(status().isNoContent());

    }

    @Test
    void deleteLabel_whenMessageExists_returnsNoContent() throws Exception {

        UUID messageId = UUID.randomUUID();
        String labelName = "Test Label";

        doNothing().when(messageService).removeLabel(messageId, labelName);

        mockMvc.perform(delete("/messages/{messageId}/labels/{labelName}", messageId, labelName))
                .andExpect(status().isNoContent());

    }
}
