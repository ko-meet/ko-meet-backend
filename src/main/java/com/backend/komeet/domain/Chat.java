package com.backend.komeet.domain;

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

    @ManyToOne
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

    @Column(nullable = false)
    @Setter
    private Boolean readStatus;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

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
