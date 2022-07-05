package com.ediary.services;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.UserDto;
import com.ediary.domain.Message;
import com.ediary.domain.Notice;
import com.ediary.domain.security.User;

import java.util.List;

public interface UserService {

    List<UserDto> listUsers();
    Boolean updatePassword(User user, String password, String oldPassword);
    List<MessageDto> listReadMessage(Integer page, Integer size, Long userId);
    List<MessageDto> listSendMessage(Integer page, Integer size, Long userId);
    MessageDto initNewMessage(Long userId);
    MessageDto replyMessage(Long userId, MessageDto messageDto, String date);
    MessageDto addReaderToMessage(MessageDto messageDto, Long readerId);
    MessageDto getReadMessageById(Long messageId, Long userId);
    MessageDto getSendMessageById(Long messageId, Long userId);
    Message sendMessage(MessageDto messageDto);
    List<NoticeDto> listNotices();
    NoticeDto getNoticeById(Long userId, Long noticeId);
    NoticeDto initNewNotice(Long userId);
    Notice addNotice(NoticeDto notice);
    NoticeDto updatePatchNotice(NoticeDto noticeUpdated, Long noticeId);
    Boolean deleteNotice(Long userId, Long noticeId);
}
