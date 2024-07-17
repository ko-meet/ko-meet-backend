package com.backend.komeet.chat.model.entities;

import com.backend.komeet.base.model.entities.BaseEntity;
import com.backend.komeet.user.model.entities.User;
import lombok.*;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Entity
public class Chat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatRoomSeq")
    private ChatRoom chatRoom;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "sender_seq", nullable = false)
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.DETACH)
    @JoinColumn(name = "recipient_seq", nullable = false)
    private User recipient;

    @Column
    @Setter
    private Long invisibleToSender;

    @Column
    @Setter
    private Long invisibleToRecipient;

    @Column(nullable = false)
    @Setter
    private Boolean readStatus;

    @ElementCollection(fetch = FetchType.LAZY)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

    /**
     * 채팅 팩토리 메서드
     *
     * @param chatRoom  채팅방
     * @param content   내용
     * @param sender    발신자
     * @param recipient 수신자
     * @param readStatus 읽음 상태
     * @param attachments 첨부파일
     * @return Chat
     */
    public static Chat from(ChatRoom chatRoom,
                            String content,
                            User sender,
                            User recipient,
                            Boolean readStatus,
                            List<String> attachments) {
        return Chat.builder()
                .chatRoom(chatRoom)
                .content(content)
                .sender(sender)
                .recipient(recipient)
                .readStatus(readStatus)
                .attachments(attachments)
                .build();
    }
}
