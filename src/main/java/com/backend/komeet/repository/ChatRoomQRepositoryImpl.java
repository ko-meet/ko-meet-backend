package com.backend.komeet.repository;

import com.backend.komeet.domain.QChat;
import com.backend.komeet.domain.QChatRoom;
import com.backend.komeet.domain.User;
import com.backend.komeet.dto.ChatRoomDto;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class ChatRoomQRepositoryImpl implements ChatRoomQRepository {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 채팅방 목록을 조회하는 메서드
     * @param user 사용자
     * @param pageable 페이지 정보
     * @return 채팅방 목록
     */
    @Override
    public Page<ChatRoomDto> getChatRooms(User user, Pageable pageable) {
        QChatRoom chatRoom = QChatRoom.chatRoom;
        Predicate predicate =
                chatRoom.sender.eq(user).or(chatRoom.recipient.eq(user));
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
     * @param user1 사용자1
     * @param user2 사용자2
     * @return 채팅방
     */
    public Optional<ChatRoomDto> getChatRoom(User user1, User user2) {
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
     * 전체 결과 개수를 조회하는 메서드
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
