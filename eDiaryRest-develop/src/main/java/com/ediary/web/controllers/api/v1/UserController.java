package com.ediary.web.controllers.api.v1;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.DTO.UserDto;
import com.ediary.security.perms.NoticePermission;
import com.ediary.services.rest.UserRestService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.ediary.web.controllers.api.v1.UserController.BASE_USER_URL;

@Profile("rest")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = BASE_USER_URL)
public class UserController {

    private final UserRestService userRestService;

    public static final String BASE_USER_URL = "/api/v1/user";

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userRestService.listUsers());
    }

    @GetMapping("/read-messages")
    public ResponseEntity<List<MessageDto>> getReadMessages(Pageable pageable) {
        return ResponseEntity.ok(userRestService.listReadMessage(pageable));
    }

    @GetMapping("/read-messages/{messageId}")
    public ResponseEntity<MessageDto> viewReadMessage(@PathVariable Long messageId) {
        return ResponseEntity.ok(userRestService.getReadMessageById(messageId));
    }

    @PostMapping("/read-messages")
    public ResponseEntity<MessageDto> replyReadMessage(@Valid @RequestBody MessageDto messageDto,
                                                       @RequestParam(name = "messageDate", required = false) String date) {
        return ResponseEntity.ok(userRestService.replyMessage(messageDto, date));
    }

    @GetMapping("/send-messages")
    public ResponseEntity<List<MessageDto>> getSendMessages(Pageable pageable) {
        return ResponseEntity.ok(userRestService.listSendMessage(pageable));
    }

    @GetMapping("/send-messages/{messageId}")
    public ResponseEntity<MessageDto> viewSendMessage(@PathVariable Long messageId) {
        return ResponseEntity.ok(userRestService.getSendMessageById(messageId));
    }

    @PostMapping("/new-message")
    public ResponseEntity<?> sendNewMessage(@Valid @RequestBody MessageDto messageDto) {
        userRestService.sendMessage(messageDto);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/new-message/reader/{readerId}")
    public ResponseEntity<MessageDto> addReaderToMessage(@PathVariable Long readerId, @Valid @RequestBody MessageDto message) {
        return ResponseEntity.ok(userRestService.addReaderToMessage(message, readerId));
    }

    @GetMapping("/notices")
    public ResponseEntity<List<NoticeDto>> getReadNotices(Pageable pageable) {
        return ResponseEntity.ok(userRestService.listNotices(pageable));
    }

    @NoticePermission
    @PostMapping("/notices")
    public ResponseEntity<?> processNewNotice(@Valid @RequestBody NoticeDto noticeDto) {
        userRestService.addNotice(noticeDto);

        return ResponseEntity.ok().build();
    }

    @NoticePermission
    @GetMapping("/notices/{noticeId}")
    public ResponseEntity<NoticeDto> getNotice(@PathVariable Long noticeId) {
        return ResponseEntity.ok(userRestService.getNoticeById(noticeId));
    }

    @NoticePermission
    @PutMapping("/notices/{noticeId}")
    public ResponseEntity<NoticeDto> updateNotice(@PathVariable Long noticeId, @Valid @RequestBody NoticeDto notice) {
        return ResponseEntity.ok(userRestService.updatePatchNotice(notice, noticeId));
    }

    @NoticePermission
    @DeleteMapping("/notices/{noticeId}")
    public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId) {
        userRestService.deleteNotice(noticeId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getUserProfile() {
        return ResponseEntity.ok(userRestService.getUserProfile());
    }
}
