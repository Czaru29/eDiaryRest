package com.ediary.services;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.UserDto;
import com.ediary.converters.*;
import com.ediary.domain.*;
import com.ediary.domain.security.User;
import com.ediary.repositories.MessageRepository;
import com.ediary.repositories.NoticeRepository;
import com.ediary.repositories.security.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

    private final Long userId = 1L;

    @Mock
    MessageService messageService;

    @Mock
    UserRepository userRepository;

    @Mock
    NoticeService noticeService;

    @Mock
    MessageToMessageDto messageToMessageDto;

    @Mock
    MessageDtoToMessage messageDtoToMessage;

    @Mock
    MessageRepository messageRepository;

    @Mock
    NoticeToNoticeDto noticeToNoticeDto;

    @Mock
    NoticeDtoToNotice noticeDtoToNotice;

    @Mock
    UserToUserDto userToUserDto;

    @Mock
    NoticeRepository noticeRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    UserService userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userService = new UserServiceImpl(messageService, userRepository, noticeService,
                messageToMessageDto, messageDtoToMessage,messageRepository, noticeToNoticeDto, noticeDtoToNotice, userToUserDto,
                noticeRepository, passwordEncoder);
    }

    @Test
    void listUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(
                User.builder().id(1L).build(),
                User.builder().id(2L).build()
        ));

        when(userToUserDto.convert(any())).thenReturn(UserDto.builder().id(3L).build());

        List<UserDto> users = userService.listUsers();

        assertEquals(2, users.size());
        assertEquals(3L, users.get(0).getId());
        verify(userRepository, times(1)).findAll();
        verify(userToUserDto, times(2)).convert(any());
    }

    @Test
    void updatePassword() {
        Long userId = 3L;
        String password = "password";
        String oldPassword = "oldPassword";
        User user = User.builder().id(userId).password(oldPassword).build();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        Boolean status = userService.updatePassword(user, password, oldPassword);

        assertTrue(status);
        verify(passwordEncoder, times(1)).encode(any());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updatePasswordIncorrectOldPassword() {
        Long userId = 3L;
        String password = "password";
        String oldPassword = "oldPassword";
        User user = User.builder().id(userId).password(oldPassword).build();

        when(userRepository.findById(userId)).thenReturn(Optional.ofNullable(user));
        when(passwordEncoder.matches(oldPassword, user.getPassword())).thenReturn(true);

        Boolean status = userService.updatePassword(user, password, "incorrectOldPassword");

        assertFalse(status);
        verify(passwordEncoder, times(0)).encode(any());
        verify(userRepository, times(0)).save(any());
    }

    @Test
    void listReadMessage() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        List<Message> messages = new ArrayList<>(){{
            add(Message.builder().build());
        }};


        Page<Message> page = new PageImpl<>(messages);

        when(messageRepository.findAllByReadersOrderByDateDesc(any(), any())).thenReturn(page);

        when(messageToMessageDto.convert(any())).thenReturn(MessageDto.builder().id(1L).build());


        List<MessageDto> messagesDto = userService.listReadMessage(0, 15, userId);

        assertEquals(1, messagesDto.size());
        assertNotNull(messagesDto);
        verify(userRepository, times(1)).findById(userId);
        verify(messageToMessageDto, times(1)).convert(any());
    }

    @Test
    void listSendMessage() {

        List<Message> messages = new ArrayList<>(){{
            add(Message.builder().build());
        }};


        Page<Message> page = new PageImpl<>(messages);

        when(userRepository.findById(userId)).thenReturn(Optional.of(User.builder().id(userId).build()));

        when(messageRepository.findAllBySendersOrderByDateDesc(any(), any())).thenReturn(page);


        when(messageToMessageDto.convertWithReaders(any())).thenReturn(MessageDto.builder().id(1L).build());

        List<MessageDto> messagesDto = userService.listSendMessage(0, 15, userId);

        assertEquals(1, messagesDto.size());
        assertNotNull(messagesDto);
        verify(userRepository, times(1)).findById(userId);
        verify(messageToMessageDto, times(1)).convertWithReaders(any());
    }

    @Test
    void initNewMessage() {

        User user = User.builder().id(userId).build();

        Long messageId = 1L;
        Message messageToConvert = Message.builder().id(messageId).senders(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(messageService.initNewMessageBySender(any())).thenReturn(
                messageToConvert
        );

        when(messageToMessageDto.convertWithReaders(messageToConvert)).thenReturn(
                MessageDto.builder().id(messageId).sendersId(messageToConvert.getSenders().getId()).build());

        MessageDto messagesDto = userService.initNewMessage(userId);

        assertEquals(user.getId(), messagesDto.getSendersId());
        verify(messageService, times(1)).initNewMessageBySender(user);
        verify(userRepository, times(1)).findById(userId);
        verify(messageToMessageDto, times(1)).convertWithReaders(messageToConvert);
    }

    @Test
    void replyMessage() {
        User user = User.builder().id(userId).build();
        String date = "01-01-01 01:01:01";

        Long messageId = 1L;
        MessageDto messageDtoToSend = MessageDto.builder().id(messageId).sendersId(user.getId()).build();
        Message messageConverted = Message.builder().id(messageDtoToSend.getId()).senders(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageDtoToMessage.convert(messageDtoToSend)).thenReturn(messageConverted);
        when(messageToMessageDto.convertWithReaders(any())).thenReturn(MessageDto.builder()
                .readersId(Arrays.asList(messageDtoToSend.getSendersId()))
                .sendersId(userId)
                .build());

        MessageDto message = userService.replyMessage(userId, messageDtoToSend, date);

        assertEquals(message.getSendersId(), user.getId());
        assertEquals(message.getReadersId().get(0), messageDtoToSend.getSendersId());
        verify(messageDtoToMessage, times(1)).convert(messageDtoToSend);
        verify(userRepository, times(2)).findById(userId);
        verify(messageToMessageDto, times(1)).convertWithReaders(any());
    }

    @Test
    void addReaderToMessage() {
        Long readerId = 27L;

        Long messageId = 1L;
        MessageDto messageDtoToSend = MessageDto.builder().id(messageId).build();
        Message messageConverted = Message.builder().id(messageDtoToSend.getId()).build();

        when(messageDtoToMessage.convert(messageDtoToSend)).thenReturn(messageConverted);
        when(userRepository.findById(readerId)).thenReturn(Optional.of(User.builder().build()));
        when(messageToMessageDto.convertWithReaders(messageConverted)).thenReturn(
                MessageDto.builder().id(messageConverted.getId()).build());

        MessageDto message = userService.addReaderToMessage(messageDtoToSend, readerId);

        assertEquals(message.getId(), messageDtoToSend.getId());
        verify(messageDtoToMessage, times(1)).convert(messageDtoToSend);
        verify(userRepository, times(1)).findById(readerId);
        verify(messageToMessageDto, times(1)).convertWithReaders(messageConverted);
    }

    @Test
    void sendMessage() {
        Long messageId = 1L;
        MessageDto messageDtoToSend = MessageDto.builder().id(messageId).readersId(Arrays.asList(1L,2l)).build();


        when(messageDtoToMessage.convert(messageDtoToSend)).thenReturn(
                Message.builder().id(messageDtoToSend.getId()).build()
        );

        when(messageService.saveMessage(any())).thenReturn(
                Message.builder().id(messageId).build()
        );

        Message messageSaved = userService.sendMessage(messageDtoToSend);

        assertEquals(messageId, messageSaved.getId());
        verify(messageDtoToMessage, times(1)).convert(any());
        verify(messageService, times(1)).saveMessage(any());
    }

    @Test
    void getReadMessageByIdWithStatusChange() {
        User user = User.builder().id(userId).build();

        Long messageId = 10L;
        Message message = Message.builder().id(messageId).readers(Arrays.asList(user)).status(Message.Status.SENT).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageRepository.save(message)).thenReturn(message);
        when(messageToMessageDto.convert(message)).thenReturn(MessageDto.builder()
                .id(message.getId())
                .status(message.getStatus())
                .build());


        MessageDto messagesDto = userService.getReadMessageById(messageId, userId);

        assertEquals(messageId, messagesDto.getId());
        assertNotEquals(message.getStatus(), messagesDto.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(1)).save(message);
        verify(messageToMessageDto, times(1)).convert(message);
    }

    @Test
    void getReadMessageByIdNoStatusChange() {
        User user = User.builder().id(userId).build();

        Long messageId = 10L;
        Message message = Message.builder().id(messageId).readers(Arrays.asList(user)).status(Message.Status.READ).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageToMessageDto.convert(message)).thenReturn(MessageDto.builder()
                .id(message.getId())
                .status(message.getStatus())
                .build());


        MessageDto messagesDto = userService.getReadMessageById(messageId, userId);

        assertEquals(messageId, messagesDto.getId());
        assertEquals(message.getStatus(), messagesDto.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(0)).save(message);
        verify(messageToMessageDto, times(1)).convert(message);
    }

    @Test
    void getSendMessageById() {
        User user = User.builder().id(userId).build();

        Long messageId = 10L;
        Message message = Message.builder().id(messageId).readers(Arrays.asList(user)).status(Message.Status.READ).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(messageToMessageDto.convertWithReaders(message)).thenReturn(MessageDto.builder()
                .id(message.getId())
                .status(message.getStatus())
                .build());


        MessageDto messagesDto = userService.getSendMessageById(messageId, userId);

        assertEquals(messageId, messagesDto.getId());
        assertEquals(message.getStatus(), messagesDto.getStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(messageRepository, times(1)).findById(messageId);
        verify(messageRepository, times(0)).save(message);
        verify(messageToMessageDto, times(1)).convertWithReaders(message);
    }

    @Test
    void listNotices() {
        when(noticeService.listNotices()).thenReturn(Arrays.asList(
                Notice.builder().id(1L).build(),
                Notice.builder().id(2L).build()
        ));

        when(noticeToNoticeDto.convert(any())).thenReturn(NoticeDto.builder().id(4L).build());

        List<NoticeDto> noticesDto = userService.listNotices();

        assertEquals(2, noticesDto.size());
        assertEquals(4L, noticesDto.get(0).getId());
        verify(noticeService, times(1)).listNotices();
        verify(noticeToNoticeDto, times(2)).convert(any());

    }

    @Test
    void getNoticeById() {
        Long noticeId = 28L;
        User user = User.builder().id(userId).build();

        Notice returnedNotice = Notice.builder().id(noticeId).user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(returnedNotice));
        when(noticeToNoticeDto.convert(returnedNotice)).thenReturn(
                NoticeDto.builder()
                        .id(returnedNotice.getId())
                        .authorId(returnedNotice.getUser().getId()).build());

        NoticeDto notice = userService.getNoticeById(userId, noticeId);

        assertEquals(notice.getId(), noticeId);
        verify(userRepository, times(1)).findById(userId);
        verify(noticeRepository, times(1)).findById(noticeId);
        verify(noticeToNoticeDto, times(1)).convert(any());
    }

    @Test
    void initNewNotice() {
        User user = User.builder().id(userId).build();

        Long noticeId = 8L;
        Notice noticeToConvert = Notice.builder().id(noticeId).user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        when(noticeService.initNewNotice(any())).thenReturn(noticeToConvert);

        when(noticeToNoticeDto.convert(noticeToConvert)).thenReturn(
                NoticeDto.builder().id(noticeToConvert.getId()).authorId(noticeToConvert.getUser().getId()).build()
        );

        NoticeDto noticeDto = userService.initNewNotice(userId);

        assertEquals(user.getId(), noticeDto.getAuthorId());
        assertEquals(noticeToConvert.getId(), noticeDto.getId());
        verify(noticeService, times(1)).initNewNotice(user);
        verify(userRepository, times(1)).findById(userId);
        verify(noticeToNoticeDto, times(1)).convert(noticeToConvert);
    }

    @Test
    public void addNotice() {
        Long noticeId = 3L;
        NoticeDto noticeToAdd = NoticeDto.builder().id(noticeId).build();

        when(noticeDtoToNotice.convert(noticeToAdd)).thenReturn(
                Notice.builder().id(noticeToAdd.getId()).build()
        );

        when(noticeService.addNotice(any())).thenReturn(
                Notice.builder().id(noticeToAdd.getId()).build()
        );

        Notice notice = userService.addNotice(noticeToAdd);

        assertEquals(noticeId, notice.getId());
        verify(noticeDtoToNotice, times(1)).convert(noticeToAdd);
        verify(noticeService, times(1)).addNotice(any());

    }

    @Test
    void updatePatchSubject() {
        Long noticeId = 1L;
        NoticeDto notice = NoticeDto.builder()
                .id(noticeId)
                .title("beforeT")
                .content("beforeC")
                .build();

        NoticeDto noticeToUpdate = NoticeDto.builder().title("Updated").build();

        Notice noticeDB = Notice.builder().build();
        Notice savedNotice = Notice.builder().build();

        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(noticeDB));
        when(noticeToNoticeDto.convert(noticeDB)).thenReturn(notice);

        when(noticeRepository.save(any())).thenReturn(savedNotice);
        when(noticeToNoticeDto.convert(savedNotice)).thenReturn(notice);

        NoticeDto noticeDto = userService.updatePatchNotice(noticeToUpdate, noticeId);

        assertEquals(noticeDto.getId(), notice.getId());
        assertEquals(noticeDto.getContent(), notice.getContent());
        assertEquals(noticeDto.getTitle(), noticeToUpdate.getTitle());

        verify(noticeRepository, times(1)).findById(noticeId);
        verify(noticeToNoticeDto, times(2)).convert(any());
        verify(noticeDtoToNotice, times(1)).convert(any());
        verify(noticeRepository, times(1)).save(any());
    }

    @Test
    void deleteSubject() {
        User user = User.builder().id(userId).build();

        Long noticeId = 2L;
        Notice notice = Notice.builder().id(noticeId).user(user).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        Boolean deleteStatus = userService.deleteNotice(userId, noticeId);

        assertTrue(deleteStatus);
        verify(noticeRepository, times(1)).findById(noticeId);
        verify(userRepository, times(1)).findById(userId);
        verify(noticeRepository, times(1)).delete(any());
    }

    @Test
    void deleteSubjectNotOwner() {
        User user = User.builder().id(userId).build();

        Long noticeId = 2L;
        Notice notice = Notice.builder().id(noticeId).user(User.builder().id(user.getId() + 1L).build()).build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.of(notice));

        Boolean deleteStatus = userService.deleteNotice(userId, noticeId);

        assertFalse(deleteStatus);
        verify(noticeRepository, times(1)).findById(noticeId);
        verify(userRepository, times(1)).findById(userId);
        verify(noticeRepository, times(0)).delete(any());
    }

    @Test
    void deleteSubjectNotFound() {
        User user = User.builder().id(userId).build();
        Long noticeId = 2L;

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(noticeRepository.findById(noticeId)).thenReturn(Optional.empty());

        Boolean deleteStatus = userService.deleteNotice(userId, noticeId);

        assertFalse(deleteStatus);
        verify(noticeRepository, times(1)).findById(noticeId);
        verify(userRepository, times(1)).findById(userId);
        verify(noticeRepository, times(0)).delete(any());
    }

}