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

    @Column(nullable = false)
    private Long senderSeq;

    @Column(nullable = false)
    private String senderNickName;

    @Column(nullable = false)
    private String senderProfileImage;

    @Column(nullable = false)
    private Long recipientSeq;

    @Column(nullable = false)
    private String recipientNickName;

    @Column(nullable = false)
    private String recipientProfileImage;

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
                .senderSeq(sender.getSeq())
                .senderNickName(sender.getNickName())
                .senderProfileImage(sender.getImageUrl())
                .recipientSeq(recipient.getSeq())
                .recipientNickName(recipient.getNickName())
                .recipientProfileImage(recipient.getImageUrl())
                .readStatus(readStatus)
                .attachments(attachments)
                .build();
    }
}
