package com.backend.komeet.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
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

}
