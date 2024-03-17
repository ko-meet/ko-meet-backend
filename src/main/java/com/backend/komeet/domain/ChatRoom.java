package com.backend.komeet.domain;

import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class ChatRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seq;

    @ManyToOne
    private User sender;

    @ManyToOne
    private User recipient;

    @OneToMany(mappedBy = "chatRoom")
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<Chat> chats;

    /**
     * 채팅방 팩토리 메서드
     *
     * @param sender    발신자
     * @param recipient 수신자
     * @return ChatRoom
     */
    public static ChatRoom from(User sender, User recipient) {
        return ChatRoom.builder()
                .sender(sender)
                .recipient(recipient)
                .build();
    }
}
