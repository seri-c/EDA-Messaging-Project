package com.project.messageservice.message;

import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.project.messageservice.exception.MessageNotFoundException;
import com.project.messageservice.message.dto.MessageResponse;

@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    private static final UUID senderId = UUID.fromString("df3c5cd0-12ea-4e32-aa17-6f70289b5dd1");
    private static final UUID recipientId = UUID.fromString("fab6e5df-15e7-4020-acb8-f3274ec5871e");
    private static final String body = "Automated test message";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MessageService messageService;

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

}
