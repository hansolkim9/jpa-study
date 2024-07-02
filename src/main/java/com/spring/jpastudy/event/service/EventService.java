package com.spring.jpastudy.event.service;

import com.spring.jpastudy.event.dto.request.EventSaveDto;
import com.spring.jpastudy.event.entity.Event;
import com.spring.jpastudy.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional // 반드시 붙일것! (커밋, 롤백 해주는 기능)
public class EventService {

    private final EventRepository eventRepository;

    // 전체 조회 서비스
    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    // 이벤트 등록
    public List<Event> saveEvent(EventSaveDto dto) {
        Event SavedEvent = eventRepository.save(dto.toEntity());
        log.info("Saved event: {}", SavedEvent);
        return getEvents();
    }
}
