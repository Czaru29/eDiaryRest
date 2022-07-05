package com.ediary.services.rest;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.UserDto;
import com.ediary.converters.*;
import com.ediary.domain.Message;
import com.ediary.domain.Notice;
import com.ediary.domain.security.User;
import com.ediary.exceptions.NoAccessException;
import com.ediary.exceptions.NotFoundException;
import com.ediary.repositories.MessageRepository;
import com.ediary.repositories.NoticeRepository;
import com.ediary.repositories.security.UserRepository;
import com.ediary.services.MessageService;
import com.ediary.services.NoticeService;
import com.ediary.util.UserDetailsHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserRestServiceImpl implements UserRestService {

    private final MessageService messageService;
    private final UserRepository userRepository;
    private final NoticeService noticeService;

    private final MessageToMessageDto messageToMessageDto;
    private final MessageDtoToMessage messageDtoToMessage;
    private final MessageRepository messageRepository;

    private final NoticeToNoticeDto noticeToNoticeDto;
    private final NoticeDtoToNotice noticeDtoToNotice;
    private final UserToUserDto userToUserDto;
    private final NoticeRepository noticeRepository;

    @Override
    public UserDto getUserProfile() {
        User currentUser = UserDetailsHelper.getCurrentUser();

        return userToUserDto.convertForViewProfile(currentUser);
    }

    @Override
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(userToUserDto::convert).collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> listReadMessage(Pageable pageable) {
        User user = getCurrentUser();

        return messageRepository.findAllByReadersOrderByDateDesc(user, pageable)
                .stream()
                .map(messageToMessageDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> listSendMessage(Pageable pageable) {
        User user = getCurrentUser();

        return messageRepository.findAllBySendersOrderByDateDesc(user, pageable)
                .stream()
                .map(messageToMessageDto::convertWithReaders)
                .collect(Collectors.toList());
    }


    @Override
    public MessageDto replyMessage(MessageDto messageDto, String date) {
        User user = getCurrentUser();
        User messageAuthor = getUserById(messageDto.getSendersId());

        Message source = messageDtoToMessage.convert(messageDto);
        Message replyMessage = new Message();

        replyMessage.setTitle("Re: " + source.getTitle());
        replyMessage.setContent("\n"
                + "\nOd: " + source.getSenders().getFirstName() + " " + source.getSenders().getLastName()
                + "\nDo: " + user.getFirstName() + " " + user.getLastName()
                + "\nWysłane: " + date
                + "\nTemat: " + source.getTitle()
                + "\n\n" + source.getContent()
        );
        replyMessage.setSenders(user);

        Set<User> s = new HashSet<>();
        s.add(messageAuthor);
        replyMessage.setReaders(s);

        return messageToMessageDto.convertWithReaders(replyMessage);
    }

    @Override
    public MessageDto addReaderToMessage(MessageDto messageDto, Long readerId) {
        Message message = messageDtoToMessage.convert(messageDto);

        Optional<User> userOptional = userRepository.findById(readerId);
        if (userOptional.isPresent()) {
            Set<User> s = new HashSet<>(message.getReaders());
            s.add(userOptional.get());
            message.setReaders(s);
        }
        return messageToMessageDto.convertWithReaders(message);
    }

    @Override
    public MessageDto getReadMessageById(Long messageId) {
        User user = getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message with given id not found: " + messageId));

        if (isUserAllowed(user, message)) {
            if (message.getStatus().equals(Message.Status.SENT)) {
                message.setStatus(Message.Status.READ);
                messageRepository.save(message);
            }
            return messageToMessageDto.convert(message);

        } else {
            throw new NoAccessException();
        }
    }

    private boolean isUserAllowed(User user, Message message) {
        return message.getReaders().stream().anyMatch(reader -> reader.getId().equals(user.getId()));
    }

    @Override
    public MessageDto getSendMessageById(Long messageId) {
        User user = getCurrentUser();

        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new NotFoundException("Message with given id not found: " + messageId));

        return messageToMessageDto.convertWithReaders(message);
    }

    @Override
    public Message sendMessage(MessageDto messageDto) {
        User user = UserDetailsHelper.getCurrentUser();

        messageDto.setDate(new Date());
        messageDto.setStatus(Message.Status.SENT);
        messageDto.setSendersId(user.getId());

        if (!messageDto.getReadersId().isEmpty()) {
            return messageService.saveMessage(messageDtoToMessage.convert(messageDto));
        }

        throw new IllegalStateException("Message cannot be send:" + messageDto.toString());
    }

    @Override
    public List<NoticeDto> listNotices(Pageable pageable) {
        return noticeService.listNotices(pageable).stream()
                .map(noticeToNoticeDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDto getNoticeById(Long noticeId) {
        User user = getCurrentUser();

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException("Notice with given id not found" + noticeId));

        if (!notice.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        return noticeToNoticeDto.convert(notice);
    }

    @Override
    public Notice addNotice(NoticeDto noticeDto) {
        User user = getCurrentUser();
        Notice notice = noticeDtoToNotice.convert(noticeDto);
        notice.setUser(user);

        return noticeService.addNotice(notice);
    }

    @Override
    public NoticeDto updatePatchNotice(NoticeDto noticeUpdated, Long noticeId) {
        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException("Notice with given id not found: " + noticeId));

        NoticeDto noticeDto = noticeToNoticeDto.convert(notice);

        if (noticeUpdated.getTitle() != null) {
            noticeDto.setTitle(noticeUpdated.getTitle());
        }

        if (noticeUpdated.getContent() != null) {
            noticeDto.setContent(noticeUpdated.getContent());
        }

        Notice savedNotice = noticeRepository.save(noticeDtoToNotice.convert(noticeDto));
        return noticeToNoticeDto.convert(savedNotice);
    }

    @Override
    public Boolean deleteNotice(Long noticeId) {
        User user = getCurrentUser();

        Notice notice = noticeRepository.findById(noticeId)
                .orElseThrow(() -> new NotFoundException("Notice with given id not found: " + noticeId));

        if (!notice.getUser().getId().equals(user.getId())) {
            return false;
        } else {
            noticeRepository.delete(notice);
            return true;
        }
    }

    private User getCurrentUser() {
        return UserDetailsHelper.getCurrentUser();
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with given id not found: " + userId));
    }
}
