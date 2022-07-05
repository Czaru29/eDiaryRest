package com.ediary.services;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.PasswordUpdateDto;
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
import com.ediary.util.UserDetailsHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService {

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

    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream().map(userToUserDto::convert).collect(Collectors.toList());
    }

    @Override
    public Boolean updatePassword(User user, String newPassword, String oldPassword) {

        if (newPassword.isEmpty() || oldPassword.isEmpty()) {
            return false;
        }

        User userDB = userRepository.findById(user.getId())
                .orElseThrow(() -> new NotFoundException("Nie znaleziono użytkownika od id: " + user.getId()));

        if (!passwordEncoder.matches(oldPassword, userDB.getPassword())) {
            return false;
        }

        userDB.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userDB);
        return true;
    }

    @Override
    public List<MessageDto> listReadMessage(Integer page, Integer size, Long userId) {
        if (page < 0) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size);

        User user = getUserById(userId);

        return messageRepository.findAllByReadersOrderByDateDesc(user, pageable)
                .stream()
                .map(messageToMessageDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<MessageDto> listSendMessage(Integer page, Integer size, Long userId) {
        if (page < 0) {
            return null;
        }

        Pageable pageable = PageRequest.of(page, size);

        User user = getUserById(userId);

        return messageRepository.findAllBySendersOrderByDateDesc(user, pageable)
                .stream()
                .map(messageToMessageDto::convertWithReaders)
                .collect(Collectors.toList());
    }

    @Override
    public MessageDto initNewMessage(Long userId) {

        User user = getUserById(userId);

        Message message = messageService.initNewMessageBySender(user);
        return messageToMessageDto.convertWithReaders(message);
    }

    @Override
    public MessageDto replyMessage(Long userId, MessageDto messageDto, String date) {
        User user = getUserById(userId);
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

        Set<User> s = new HashSet<User>();
        s.add(messageAuthor);
        replyMessage.setReaders(s);

    return messageToMessageDto.convertWithReaders(replyMessage);
    }

    @Override
    public MessageDto addReaderToMessage(MessageDto messageDto, Long readerId) {
        Message message = messageDtoToMessage.convert(messageDto);

        Optional<User> userOptional = userRepository.findById(readerId);
        if (userOptional.isPresent()) {
            Set<User> s = new HashSet<User>();
            s.addAll(message.getReaders());
            s.add(userOptional.get());
            message.setReaders(s);
        }
        return messageToMessageDto.convertWithReaders(message);
    }

    @Override
    public MessageDto getReadMessageById(Long messageId, Long userId) {
        User user = getUserById(userId);

        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (!messageOptional.isPresent()){
            throw new NotFoundException("Message Not Found.");
        }

        Message message = messageOptional.get();
        if (message.getReaders().contains(user)) {
            if (message.getStatus().equals(Message.Status.SENT)) {
                message.setStatus(Message.Status.READ);
                messageRepository.save(message);
            }
            return messageToMessageDto.convert(message);

        } else {
            throw new NoAccessException();
        }
    }

    @Override
    public MessageDto getSendMessageById(Long messageId, Long userId) {
        User user = getUserById(userId);

        Optional<Message> messageOptional = messageRepository.findById(messageId);
        if (!messageOptional.isPresent()){
            throw new NotFoundException("Message Not Found.");
        }

        Message message = messageOptional.get();
        return messageToMessageDto.convertWithReaders(message);
    }

    @Override
    public Message sendMessage(MessageDto messageDto) {
        messageDto.setDate(new Date());
        messageDto.setStatus(Message.Status.SENT);

        Message message = new Message();
        if(!messageDto.getReadersId().isEmpty()) {
            message = messageService.saveMessage(messageDtoToMessage.convert(messageDto));
        }

        return message;
    }

    @Override
    public List<NoticeDto> listNotices() {
        return noticeService.listNotices().stream()
                .map(noticeToNoticeDto::convert)
                .collect(Collectors.toList());
    }

    @Override
    public NoticeDto getNoticeById(Long userId, Long noticeId) {
        User user = getUserById(userId);

        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (!noticeOptional.isPresent()) {
            throw new NotFoundException("Notice Not Found.");
        }
        Notice notice = noticeOptional.get();

        if(!notice.getUser().getId().equals(user.getId())) {
            throw new NoAccessException();
        }

        return noticeToNoticeDto.convert(notice);
    }

    @Override
    public NoticeDto initNewNotice(Long userId) {

        User user = getUserById(userId);

        Notice notice = noticeService.initNewNotice(user);
        return noticeToNoticeDto.convert(notice);
    }

    @Override
    public Notice addNotice(NoticeDto noticeDto) {

        return noticeService.addNotice(noticeDtoToNotice.convert(noticeDto));
    }

    @Override
    public NoticeDto updatePatchNotice(NoticeDto noticeUpdated, Long noticeId) {
        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (!noticeOptional.isPresent()) {
            throw new NotFoundException("Notice Not Found.");
        }

        NoticeDto notice = noticeToNoticeDto.convert(noticeOptional.get());

        if(noticeUpdated.getTitle() != null) {
            notice.setTitle(noticeUpdated.getTitle());
        }

        if (noticeUpdated.getContent() != null) {
            notice.setContent(noticeUpdated.getContent());
        }

        Notice savedNotice = noticeRepository.save(noticeDtoToNotice.convert(notice));
        return  noticeToNoticeDto.convert(savedNotice);
    }

    @Override
    public Boolean deleteNotice(Long userId, Long noticeId) {
        User user = getUserById(userId);

        Optional<Notice> noticeOptional = noticeRepository.findById(noticeId);
        if (!noticeOptional.isPresent()) {
            return false;
        }
        Notice notice = noticeOptional.get();

        if (!notice.getUser().getId().equals(user.getId())) {
            return false;
        } else {
            noticeRepository.delete(notice);
            return true;
        }
    }

    private User getUserById(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);

        if (!userOptional.isPresent()) {
            throw new NotFoundException("User Not Found.");
        }

        return userOptional.get();
    }
}
