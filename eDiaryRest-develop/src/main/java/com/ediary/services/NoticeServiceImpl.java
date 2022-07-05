package com.ediary.services;

import com.ediary.domain.Notice;
import com.ediary.domain.security.User;
import com.ediary.repositories.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@Service
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    @Override
    public List<Notice> listNotices(Pageable pageable) {
        return noticeRepository.findAllByOrderByDateDesc(pageable);
    }

    @Override
    public List<Notice> listNotices() {
        return noticeRepository.findAllByOrderByDateDesc();
    }

    @Override
    public Notice initNewNotice(User user) {
        return Notice.builder().user(user).build();
    }

    @Override
    public Notice addNotice(Notice notice) {
        notice.setDate(new Date());
        return noticeRepository.save(notice);
    }
}
