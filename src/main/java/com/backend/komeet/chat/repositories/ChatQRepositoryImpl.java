package com.backend.komeet.chat.repositories;

import com.backend.komeet.chat.model.dtos.ChatDto;
import com.backend.komeet.chat.model.entities.QChat;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 채팅 관련 Querydsl 레포지토리 구현체
 */
@RequiredArgsConstructor
public class ChatQRepositoryImpl implements ChatQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 채팅 목록을 조회 하는 메서드
     */
    @Override
    public Page<ChatDto> getChats(
            Long chatRoomSeq,
            Long userSeq,
            Pageable pageable
    ) {

        QChat chat = QChat.chat;
        Predicate predicate =
                chat.chatRoom.seq.eq(chatRoomSeq)
                        .and(chat.sender.seq.eq(userSeq)
                                .or(chat.recipient.seq.eq(userSeq)))
                        .and(chat.invisibleToSender.isNull()
                                .or(chat.invisibleToSender.ne(userSeq)))
                        .and(chat.invisibleToRecipient.isNull()
                                .or(chat.invisibleToRecipient.ne(userSeq)));

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
     * 사용자가 읽지 않은 채팅이 있는지 확인하는 메서드
     */
    @Override
    public boolean hasUnreadMessages(
            Long userSeq
    ) {
        QChat chat = QChat.chat;
        Predicate predicate = chat.recipient.seq.eq(userSeq)
                .and(chat.readStatus.isFalse())
                .and(chat.invisibleToRecipient.ne(userSeq)
                        .or(chat.invisibleToRecipient.ne(userSeq)));

        return jpaQueryFactory.selectFrom(chat)
                .where(predicate)
                .fetchCount() > 0;
    }

    /**
     * 전체 결과 개수를 조회하는 메서드
     */
    private Long getLength(
            Predicate predicate
    ) {
        QChat post = QChat.chat;
        return jpaQueryFactory.selectFrom(post)
                .where(predicate)
                .fetchCount();
    }
}

