package com.ediary.services;

import com.ediary.domain.Message;
import com.ediary.domain.security.User;
import com.ediary.repositories.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public List<Message> listReadMessageByUser(User user) {
        return messageRepository.findAllByReadersOrderByDateDesc(user);
    }

    @Override
    public List<Message> listSendMessageByUser(User user) {
        return messageRepository.findAllBySendersOrderByDateDesc(user);
    }

    @Override
    public Message initNewMessageBySender(User user) {
        return Message.builder().senders(user).build();
    }

    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

}
