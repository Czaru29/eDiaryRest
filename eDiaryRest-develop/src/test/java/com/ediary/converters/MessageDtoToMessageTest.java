package com.ediary.converters;

import com.ediary.DTO.MessageDto;
import com.ediary.domain.Message;
import com.ediary.domain.security.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageToMessageDtoTest {

    private final Long id = 1L;
    private final String title = "title";
    private final String content = "content";
    private Message.Status status = Message.Status.SENT;

    private final Long userId = 2L;
    User user;

    MessageToMessageDto converter;

    @BeforeEach
    void setUp() {
        converter = new MessageToMessageDto();
        user = User.builder().id(userId).build();
    }

    @Test
    public void testNullObject() throws Exception {
        assertNull(converter.convert(null));
    }


    @Test
    public void testConvert() throws Exception {

        Message message = Message.builder()
                .id(id)
                .title(title)
                .content(content)
                .status(status)
                .senders(user)
                .build();

        MessageDto messageDto = converter.convert(message);

        assertEquals(message.getId(), messageDto.getId());
        assertEquals(message.getTitle(), messageDto.getTitle());
        assertEquals(message.getContent(), messageDto.getContent());
        assertEquals(message.getStatus(), messageDto.getStatus());
        assertEquals(message.getSenders().getId(), messageDto.getSendersId());

    }


}