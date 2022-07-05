package com.ediary.web.controllers;

import com.ediary.DTO.MessageDto;
import com.ediary.DTO.NoticeDto;
import com.ediary.converters.UserToUserDto;
import com.ediary.domain.security.User;
import com.ediary.security.perms.NoticePermission;
import com.ediary.security.perms.UserPermission;
import com.ediary.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.Optional;

@Profile("default")
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    private final UserToUserDto userToUserDto;

    private final int pageSize = 15;

    @ModelAttribute
    public void addAuthenticatedUser(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("user", userToUserDto.convert(user));
    }

    @InitBinder
    public void dataBinder(WebDataBinder dataBinder){
        dataBinder.setDisallowedFields("id");

        dataBinder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport(){
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                setValue(LocalDate.parse(text));
            }
        });
    }

    @UserPermission
    @GetMapping("/{userId}/updatePassword")
    public String updatePassword(@PathVariable Long userId) {
        return "user/updatePassword";
    }

    @UserPermission
    @PostMapping("/{userId}/updatePassword")
    public String processUpdatePassword(@PathVariable Long userId,
                                        @RequestParam("newPassword") String password,
                                        @RequestParam("oldPassword") String oldPassword,
                                        @AuthenticationPrincipal User user,
                                        Model model) {

        userService.updatePassword(user, password, oldPassword);
        return "redirect:/user/profil";
    }

    @UserPermission
    @GetMapping("/{userId}/messages")
    public String mainMessages(@PathVariable Long userId, Model model) {
        return "user/messages";
    }


    @UserPermission
    @GetMapping("/{userId}/readMessages")
    public String getReadMessages(@PathVariable Long userId,
                                  @RequestParam(name = "page", required = false) Optional<Integer> page,
                                  Model model) {

        model.addAttribute("page", page);
        model.addAttribute("readMessages", userService.listReadMessage(page.orElse(0), pageSize, userId));
        return "user/readMessages";
    }

    @UserPermission
    @GetMapping("/{userId}/sendMessages")
    public String getSendMessages(@PathVariable Long userId,
                                  @RequestParam(name = "page", required = false) Optional<Integer> page,
                                  Model model) {

        model.addAttribute("page", page);
        model.addAttribute("sendMessages", userService.listSendMessage(page.orElse(0), pageSize, userId));
        return "user/sendMessages";
    }

    @UserPermission
    @GetMapping("/{userId}/newMessages")
    public String newMessage(@PathVariable Long userId, Model model) {

        model.addAttribute("readers", userService.listUsers());
        model.addAttribute("messageDto", userService.initNewMessage(userId));
        return "user/newMessages";
    }

    @UserPermission
    @PostMapping("/{userId}/newMessages")
    public String processNewMessage(@Valid @ModelAttribute MessageDto message, BindingResult result,
                                    @PathVariable Long userId, Model model) {
        if (result.hasErrors()){
            model.addAttribute("readers", userService.listUsers());
            return "user/newMessages";
        } else {
            userService.sendMessage(message);
            return "redirect:/user/" + message.getSendersId() + "/sendMessages";
        }
    }

    @UserPermission
    @PostMapping("/{userId}/newMessages/addReader/{readerId}")
    public String addReaderToMessage(@PathVariable Long userId,
                                     @PathVariable Long readerId,
                                     @ModelAttribute MessageDto message,
                                     Model model) {
        model.addAttribute("readers", userService.listUsers());
        model.addAttribute("messageDto", userService.addReaderToMessage(message, readerId));
        return "user/newMessages";

    }

    @UserPermission
    @GetMapping("/{userId}/readMessages/{messageId}")
    public String viewReadMessage(@PathVariable Long userId, @PathVariable Long messageId, Model model) {

        model.addAttribute("message", userService.getReadMessageById(messageId, userId));
        return "/user/viewReadMessage";
    }

    @UserPermission
    @PostMapping("/{userId}/readMessages/{messageId}")
    public String replyReadMessage(@PathVariable Long userId,
                                   @PathVariable Long messageId,
                                   @ModelAttribute MessageDto messageDto,
                                   @RequestParam(name = "messageDate", required = false) String date,
                                   Model model) {

        model.addAttribute("readers", userService.listUsers());
        model.addAttribute("messageDto", userService.replyMessage(userId, messageDto, date));
        return "/user/newMessages";
    }

    @UserPermission
    @GetMapping("/{userId}/sendMessages/{messageId}")
    public String viewSendMessage(@PathVariable Long userId, @PathVariable Long messageId, Model model) {

        model.addAttribute("message", userService.getSendMessageById(messageId, userId));
        return "/user/viewSendMessage";
    }

    @GetMapping("/notice")
    public String getAllNotices(Model model) {

        return "user/notices";
    }

    @UserPermission
    @GetMapping("/{userId}/readNotices")
    public String getReadNotices(@PathVariable Long userId, Model model) {
        model.addAttribute("notices", userService.listNotices());
        return "user/readNotices";
    }

    @NoticePermission
    @GetMapping("/{userId}/newNotice")
    public String newNotice(@PathVariable Long userId, Model model) {

        model.addAttribute("notice", userService.initNewNotice(userId));
        return "user/newNotice";
    }

    @NoticePermission
    @PostMapping("/{userId}/newNotice")
    public String processNewNotice(@PathVariable Long userId,
                                   @Valid @ModelAttribute("notice") NoticeDto notice, BindingResult result) {
        if (result.hasErrors()){
            return "user/newNotice";
        } else {
            userService.addNotice(notice);
            return "redirect:/user/" + userId + "/readNotices";
        }
    }

    @NoticePermission
    @GetMapping("/{userId}/editNotice/{noticeId}")
    public String editNotice(@PathVariable Long userId,
                             @PathVariable Long noticeId,
                             Model model) {

        model.addAttribute("notice", userService.getNoticeById(userId,noticeId));
        return "user/editNotice";
    }

    @NoticePermission
    @PostMapping("/{userId}/updateNotice/{noticeId}")
    public String updatePatchNotice(@PathVariable Long userId,
                                    @PathVariable Long noticeId,
                                    @Valid @ModelAttribute("notice") NoticeDto notice, BindingResult result) {
        if (result.hasErrors()){
            notice.setId(noticeId);
            return "user/editNotice";
        } else {
            userService.updatePatchNotice(notice, noticeId);
            return "redirect:/user/" + userId + "/readNotices";
        }
    }

    @NoticePermission
    @PostMapping("/{userId}/deleteNotice/{noticeId}")
    public String deleteNotice(@PathVariable Long userId,
                               @PathVariable Long noticeId) {

            userService.deleteNotice(userId, noticeId);
            return "redirect:/user/" + userId + "/readNotices";
    }

    @GetMapping("/profil")
    public String profil(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("userDetails", userToUserDto.convertForViewProfile(user));

        return "user/profil";
    }
}
