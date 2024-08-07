package com.backend.komeet.chat.repositories;

import com.backend.komeet.chat.model.dtos.ChatRoomDto;
import com.backend.komeet.chat.model.entities.QChat;
import com.backend.komeet.chat.model.entities.QChatRoom;
import com.backend.komeet.user.model.entities.User;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 채팅방 관련 Querydsl 레포지토리 구현체
 */
@RequiredArgsConstructor
public class ChatRoomQRepositoryImpl implements ChatRoomQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 채팅방 목록을 조회하는 메서드
     */
    @Override
    public Page<ChatRoomDto> getChatRooms(
            User user,
            Pageable pageable
    ) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        Predicate predicate =
                (chatRoom.sender.eq(user).and(chatRoom.isVisibleToSender.isTrue()))
                        .or(chatRoom.recipient.eq(user).and(chatRoom.isVisibleToRecipient.isTrue()));

        Long total = getLength(predicate);
        OrderSpecifier<?> orderSpecifier = chatRoom.createdAt.desc();

        List<ChatRoomDto> results = jpaQueryFactory.selectFrom(chatRoom)
                .where(predicate)
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch()
                .stream()
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());

        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 채팅방을 조회하는 메서드
     */
    public Optional<ChatRoomDto> getChatRoom(
            User user1,
            User user2
    ) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        Predicate predicate =
                chatRoom.sender.eq(user1).and(chatRoom.recipient.eq(user2))
                        .or(chatRoom.sender.eq(user2).and(chatRoom.recipient.eq(user1)));

        return Optional.ofNullable(jpaQueryFactory.selectFrom(chatRoom)
                        .where(predicate)
                        .fetchOne())
                .map(ChatRoomDto::from);
    }

    /**
     * 채팅방 검색 메서드
     */
    @Override
    public List<ChatRoomDto> searchChatRoomsByUserNickName(
            User user,
            String keyword
    ) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        keyword = keyword.trim();
        Predicate predicate =
                (chatRoom.sender.eq(user)
                        .and(chatRoom.sender.eq(user)
                                .and(chatRoom.isVisibleToSender.isTrue()))
                        .or(chatRoom.recipient.eq(user)
                                .and(chatRoom.isVisibleToRecipient.isTrue()))
                        .and(chatRoom.sender.nickName.contains(keyword))
                        .or(chatRoom.recipient.nickName.contains(keyword)));

        Long total = getLength(predicate);
        OrderSpecifier<?> orderSpecifier = chatRoom.createdAt.desc();

        return jpaQueryFactory.selectFrom(chatRoom)
                .where(predicate)
                .orderBy(orderSpecifier)
                .fetch()
                .stream()
                .map(ChatRoomDto::from)
                .collect(Collectors.toList());
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

