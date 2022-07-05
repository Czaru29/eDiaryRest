package com.ediary.services.rest;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.PasswordUpdateDto;
import com.ediary.DTO.UserDto;
import com.ediary.domain.Message;
import com.ediary.domain.Notice;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRestService {

    UserDto getUserProfile();
    List<UserDto> listUsers();
    List<MessageDto> listReadMessage(Pageable pageable);
    List<MessageDto> listSendMessage(Pageable pageable);
    MessageDto replyMessage(MessageDto messageDto, String date);
    MessageDto addReaderToMessage(MessageDto messageDto, Long readerId);
    MessageDto getReadMessageById(Long messageId);
    MessageDto getSendMessageById(Long messageId);
    Message sendMessage( MessageDto messageDto);
    List<NoticeDto> listNotices(Pageable pageable);
    NoticeDto getNoticeById(Long noticeId);
    Notice addNotice(NoticeDto notice);
    NoticeDto updatePatchNotice(NoticeDto noticeUpdated, Long noticeId);
    Boolean deleteNotice(Long noticeId);
}
