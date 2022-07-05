package com.ediary.web.controllers;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.UserDto;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.Message;
import com.ediary.domain.Notice;
import com.ediary.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest {

    private final Long userId = 1L;

    @Mock
    UserService userService;

    @Mock
    UserToUserDto userToUserDto;

    UserController userController;

    MockMvc mockMvc;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userController = new UserController(userService, userToUserDto);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void updatePassword() throws Exception {
        mockMvc.perform(get("/user/"+ userId +"/updatePassword"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/updatePassword"));
    }

    @Test
    void processUpdatePassword() throws Exception {
        when(userService.updatePassword(any(), any(), any())).thenReturn(true);

        mockMvc.perform(post("/user/"+ userId +"/updatePassword")
                    .param("newPassword", "pwd")
                    .param("oldPassword", "opwd"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/profil"));

        verify(userService, times(1)).updatePassword(any(), any(), any());
    }

    @Test
    void addAuthenticatedUser() throws Exception {
        when(userToUserDto.convert(any())).thenReturn(UserDto.builder().build());

        mockMvc.perform(get("/user/notice"))
                .andExpect(model().attributeExists("user"));

        verify(userToUserDto, times(1)).convert(any());
    }

    @Test
    void getReadMessages() throws Exception {

        when(userService.listReadMessage(1, 1, userId)).thenReturn(Arrays.asList(
                MessageDto.builder().id(1L).build(),
                MessageDto.builder().id(2L).build()
        ));

        mockMvc.perform(get("/user/"+ userId +"/readMessages"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("readMessages"))
                .andExpect(view().name("user/readMessages"));

        verify(userService, times(1)).listReadMessage(0, 15, userId);
        assertEquals(2, userService.listReadMessage(1, 1, userId).size());

    }

    @Test
    void getSendMessages() throws Exception {
        when(userService.listSendMessage(1, 1, userId)).thenReturn(Arrays.asList(
                MessageDto.builder().id(1L).build(),
                MessageDto.builder().id(2L).build()
        ));

        mockMvc.perform(get("/user/"+ userId +"/sendMessages"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("sendMessages"))
                .andExpect(view().name("user/sendMessages"));

        verify(userService, times(1)).listSendMessage(0, 15, userId);
        assertEquals(2, userService.listSendMessage(1, 1, userId).size());
    }

    @Test
    void newMessage() throws Exception {
        when(userService.initNewMessage(userId)).thenReturn(MessageDto.builder().build());
        when(userService.listUsers()).thenReturn(Collections.singletonList(UserDto.builder().build()));

        mockMvc.perform(get("/user/"+ userId +"/newMessages"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("readers"))
                .andExpect(model().attributeExists("messageDto"))
                .andExpect(view().name("user/newMessages"));

        verify(userService, times(1)).initNewMessage(userId);
        verify(userService, times(1)).listUsers();
    }

    @Test
    void processNewMessageNoValid() throws Exception {
        when(userService.sendMessage(any())).thenReturn(Message.builder().build());

        mockMvc.perform(post("/user/"+ userId +"/newMessages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(AbstractAsJsonControllerTest.asJsonString(MessageDto.builder().sendersId(1L).build())))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void addReaderToMessage() throws Exception {
        Long readerId = 27L;

        when(userService.listUsers()).thenReturn(Collections.singletonList(UserDto.builder().build()));
        when(userService.addReaderToMessage(any(), any())).thenReturn(MessageDto.builder().build());

        mockMvc.perform(post("/user/"+ userId +"/newMessages/addReader/" + readerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("readers"))
                .andExpect(model().attributeExists("messageDto"))
                .andExpect(view().name("user/newMessages"));

        verify(userService, times(1)).listUsers();
        verify(userService, times(1)).addReaderToMessage(any(),any());
    }

    @Test
    void viewReadMessage() throws Exception {
        Long messageId = 10L;

        when(userService.getReadMessageById(messageId, userId)).thenReturn(MessageDto.builder().build());

        mockMvc.perform(get("/user/"+ userId +"/readMessages/" + messageId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("/user/viewReadMessage"));

        verify(userService, times(1)).getReadMessageById(messageId, userId);

    }

    @Test
    void replyReadMessage() throws Exception {
        Long messageId = 10L;

        when(userService.listUsers()).thenReturn(Collections.singletonList(UserDto.builder().build()));
        when(userService.replyMessage(any(), any(), any())).thenReturn(MessageDto.builder().build());

        mockMvc.perform(post("/user/"+ userId +"/readMessages/" + messageId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("readers"))
                .andExpect(model().attributeExists("messageDto"))
                .andExpect(view().name("/user/newMessages"));

        verify(userService, times(1)).listUsers();
        verify(userService, times(1)).replyMessage(any(), any(), any());

    }

    @Test
    void viewSendMessage() throws Exception {
        Long messageId = 10L;

        when(userService.getSendMessageById(messageId, userId)).thenReturn(MessageDto.builder().build());

        mockMvc.perform(get("/user/"+ userId +"/sendMessages/" + messageId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("/user/viewSendMessage"));

        verify(userService, times(1)).getSendMessageById(messageId, userId);

    }

    @Test
    void getAllNotices() throws Exception {
        mockMvc.perform(get("/user/notice"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/notices"));
    }

    @Test
    void getReadNotices() throws Exception {
        when(userService.listNotices()).thenReturn(Arrays.asList(
                NoticeDto.builder().id(1L).build(),
                NoticeDto.builder().id(2L).build()
        ));

        mockMvc.perform(get("/user/"+ userId + "/readNotices"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notices"))
                .andExpect(view().name("user/readNotices"));

        verify(userService, times(1)).listNotices();
        assertEquals(2, userService.listNotices().size());
        assertEquals(2L, userService.listNotices().get(1).getId());
    }


    @Test
    void newNotice() throws Exception {
        when(userService.initNewNotice(userId)).thenReturn(NoticeDto.builder().build());

        mockMvc.perform(get("/user/"+ userId +"/newNotice"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notice"))
                .andExpect(view().name("user/newNotice"));

        verify(userService, times(1)).initNewNotice(userId);
    }

    @Test
    void processNewNotice() throws Exception {
        when(userService.addNotice(any())).thenReturn(Notice.builder().build());

        mockMvc.perform(post("/user/"+ userId +"/newNotice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/" + userId + "/readNotices"));

        verify(userService).addNotice(any());
    }

    @Test
    void editNotice() throws Exception {
        Long noticeId = 28l;

        when(userService.getNoticeById(userId, noticeId)).thenReturn(NoticeDto.builder().build());

        mockMvc.perform(get("/user/"+ userId +"/editNotice/" + noticeId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("notice"))
                .andExpect(view().name("user/editNotice"));

        verify(userService, times(1)).getNoticeById(userId, noticeId);
    }

    @Test
    void updatePatchNotice() throws Exception {
        Long noticeId = 28L;
        when(userService.updatePatchNotice(any(), anyLong())).thenReturn(NoticeDto.builder().build());

        mockMvc.perform(post("/user/"+ userId +"/updateNotice/" + noticeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/" + userId + "/readNotices"));

        verify(userService).updatePatchNotice(any(), anyLong());
    }

    @Test
    void deleteNotice() throws Exception {
        Long noticeId = 28L;
        when(userService.deleteNotice(userId, noticeId)).thenReturn(Boolean.TRUE);

        mockMvc.perform(post("/user/"+ userId +"/deleteNotice/" + noticeId))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/user/" + userId + "/readNotices"));

        verify(userService, times(1)).deleteNotice(userId, noticeId);
    }
}