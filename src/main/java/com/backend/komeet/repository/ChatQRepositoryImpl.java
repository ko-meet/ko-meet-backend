package com.backend.komeet.repository;

import com.backend.komeet.domain.QChat;
import com.backend.komeet.dto.ChatDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ChatQRepositoryImpl implements ChatQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 채팅 목록을 조회 하는 메서드
     *
     * @param charRoomSeq 채팅방 식별자
     * @param userSeq     사용자 식별자
     * @param pageable    페이지 정보
     * @return 채팅방 목록
     */
    @Override
    public Page<ChatDto> getChats(Long counterpartSeq, Long userSeq, Pageable pageable) {

        QChat chat = QChat.chat;
        Predicate predicate = chat.chatRoom.sender.seq.eq(counterpartSeq).and(chat.chatRoom.recipient.seq.eq(userSeq))
                .or(chat.chatRoom.sender.seq.eq(userSeq).and(chat.chatRoom.recipient.seq.eq(counterpartSeq)));
        Long total = getLength(predicate);
        OrderSpecifier<?> orderSpecifier = chat.createdAt.desc();

        List<ChatDto> results = jpaQueryFactory.selectFrom(chat)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(ChatDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 전체 결과 개수를 조회하는 메서드
     *
     * @param predicate 조건
     * @return 전체 결과 개수
     */
    private Long getLength(Predicate predicate) {
        QChat post = QChat.chat;
        return jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .fetchCount();
    }
}

