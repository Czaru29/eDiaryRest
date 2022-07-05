package com.ediary.services;

import com.ediary.domain.Message;
import com.ediary.domain.security.User;

import java.util.List;

public interface MessageService {

    List<Message> listReadMessageByUser(User user);
    List<Message> listSendMessageByUser(User user);
    Message initNewMessageBySender(User user);
    Message saveMessage(Message message);
}
