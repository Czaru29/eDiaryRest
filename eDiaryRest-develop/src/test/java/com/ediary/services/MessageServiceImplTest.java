package com.ediary.services;

import com.ediary.domain.Message;
import com.ediary.domain.security.User;
import com.ediary.repositories.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class MessageServiceImplTest {

    private Long userId = 1L;

    @Mock
    MessageRepository messageRepository;

    MessageService messageService;

    User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        messageService = new MessageServiceImpl(messageRepository);

        user = User.builder().id(userId).build();
    }

    @Test
    void listReadMessageByUser() {
        when(messageRepository.findAllByReadersOrderByDateDesc(user)).thenReturn(Arrays.asList(
                Message.builder().id(1L).build(),
                Message.builder().id(2L).build()
        ));

        List<Message> messages = messageService.listReadMessageByUser(user);

        assertEquals(2, messages.size());
        assertEquals(2L, messages.get(1).getId());
        verify(messageRepository, times(1)).findAllByReadersOrderByDateDesc(user);

    }

    @Test
    void listSendMessageByUser() {
        when(messageRepository.findAllBySendersOrderByDateDesc(user)).thenReturn(Arrays.asList(
                Message.builder().id(1L).build(),
                Message.builder().id(2L).build()
        ));

        List<Message> messages = messageService.listSendMessageByUser(user);

        assertEquals(2, messages.size());
        assertEquals(1L, messages.get(0).getId());
        verify(messageRepository, times(1)).findAllBySendersOrderByDateDesc(user);
    }

    @Test
    void saveMessage() {
        Long messageId = 1L;
        Message messageToSave = Message.builder().id(messageId).build();

        when(messageRepository.save(messageToSave)).thenReturn(messageToSave);

        Message messageSaved = messageService.saveMessage(messageToSave);

        assertEquals(messageSaved.getId(), messageSaved.getId());
        verify(messageRepository, times(1)).save(messageToSave);
    }
}