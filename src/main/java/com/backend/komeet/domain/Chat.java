package com.backend.komeet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

    @Column(nullable = false)
    private Long senderSeq;

    @Column(nullable = false)
    private Long recipientSeq;

    @Column(nullable = false)
    private Boolean readStatus;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<String> attachments;

    public static Chat from(ChatRoom chatRoom,
                            String content,
                            Long senderSeq,
                            Long recipientSeq,
                            Boolean readStatus,
                            List<String> attachments) {
        return Chat.builder()
                .chatRoom(chatRoom)
                .content(content)
                .senderSeq(senderSeq)
                .recipientSeq(recipientSeq)
                .readStatus(readStatus)
                .attachments(attachments)
                .build();
    }
}
