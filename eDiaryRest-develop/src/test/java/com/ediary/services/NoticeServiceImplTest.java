package com.ediary.services;

import com.ediary.domain.Notice;
import com.ediary.repositories.NoticeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class NoticeServiceImplTest {

    @Mock
    NoticeRepository noticeRepository;

    NoticeService noticeService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        noticeService = new NoticeServiceImpl(noticeRepository);
    }

    @Test
    void listNotices() {
        when(noticeRepository.findAllByOrderByDateDesc(any())).thenReturn(Arrays.asList(
                Notice.builder().id(1L).build(),
                Notice.builder().id(2L).build()
        ));

        List<Notice> notices = noticeService.listNotices(Pageable.unpaged());

        assertEquals(2, notices.size());
        assertEquals(1L, notices.get(0).getId());
        verify(noticeRepository, times(1)).findAllByOrderByDateDesc(Pageable.unpaged());
    }

    @Test
    void addNotice() {
        Long noticeId = 3L;
        Notice noticeToSave = Notice.builder().id(noticeId).build();

        when(noticeRepository.save(noticeToSave)).thenReturn(
                Notice.builder().id(noticeToSave.getId()).build()
        );

        Notice noticeSaved = noticeService.addNotice(noticeToSave);

        assertEquals(noticeId, noticeSaved.getId());
        verify(noticeRepository, times(1)).save(noticeToSave);
    }
}