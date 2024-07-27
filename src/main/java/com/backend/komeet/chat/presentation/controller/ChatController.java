package com.backend.komeet.chat.presentation.controller;

import com.backend.komeet.base.presentation.response.ApiResponse;
import com.backend.komeet.chat.application.ChatRoomService;
import com.backend.komeet.chat.application.ChatService;
import com.backend.komeet.chat.model.dtos.ChatDto;
import com.backend.komeet.chat.model.dtos.ChatRoomDto;
import com.backend.komeet.chat.presentation.request.ChatContentRequest;
import com.backend.komeet.infrastructure.security.JwtProvider;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

/**
 * 채팅 관련 컨트롤러
 */
@Api(tags = "Chat API", description = "채킹 관련 API")
@RequestMapping("/api/v1/chat/")
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final ChatService chatService;
    private final ChatRoomService chatRoomService;
    private final JwtProvider jwtProvider;

    /**
     * 채팅방 목록조회
     */
    @ApiOperation(value = "채팅방 목록조회", notes = "채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse> getChatRooms(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        Page<ChatRoomDto> chatList = chatRoomService.getChatRooms(userSeq, page);
        return ResponseEntity.status(OK).body(new ApiResponse(chatList));
    }

    /**
     * 채팅 내역 조회
     */
    @ApiOperation(value = "채팅 내역 조회", notes = "채팅 내역을 조회합니다.")
    @GetMapping("/rooms/{chatRoomSeq}")
    public ResponseEntity<ApiResponse> getChats(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long chatRoomSeq,
            @RequestParam(value = "page", defaultValue = "0") Integer page
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        Page<ChatDto> chatList = chatService.getChats(chatRoomSeq, userSeq, page);
        return ResponseEntity.status(OK).body(new ApiResponse(chatList));
    }

    /**
     * 채팅방 생성
     */
    @ApiOperation(value = "채팅방 생성", notes = "채팅방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse> createChatRoom(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam Long counterpartSeq
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        Long chatRoomSeq = chatRoomService.createChatRoom(userSeq, counterpartSeq);
        return ResponseEntity.status(OK).body(new ApiResponse(chatRoomSeq));
    }

    /**
     * 채팅 추가
     */
    @ApiOperation(value = "채팅 추가", notes = "채팅을 추가합니다.")
    @PostMapping("/rooms/{chatRoomSeq}")
    public ResponseEntity<ApiResponse> addChat(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long chatRoomSeq,
            @RequestBody ChatContentRequest content
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        chatService.addChat(chatRoomSeq, userSeq, content);
        return ResponseEntity.status(OK).body(new ApiResponse(OK.value()));
    }

    /**
     * 채팅방 삭제
     */
    @ApiOperation(value = "채팅방 삭제", notes = "채팅방을 삭제합니다.")
    @DeleteMapping("/rooms/{chatRoomSeq}")
    public ResponseEntity<ApiResponse> deleteChatRoom(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long chatRoomSeq
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        chatRoomService.deleteChatRoom(chatRoomSeq, userSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 채팅방 검색
     */
    @ApiOperation(value = "채팅방 검색", notes = "채팅방을 검색합니다.")
    @GetMapping("/rooms/search")
    public ResponseEntity<ApiResponse> searchChatRooms(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam String keyword
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(chatRoomService.searchChatRooms(userSeq, keyword)));
    }

    /**
     * 신규 채팅 여부 조회
     */
    @ApiOperation(value = "신규 채팅 여부 조회", notes = "신규 채팅 여부를 조회합니다.")
    @GetMapping("/rooms/new")
    public ResponseEntity<ApiResponse> getNewChatRooms(
            @RequestHeader(AUTHORIZATION) String token
    ) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(chatRoomService.getNewChatRooms(userSeq)));
    }
}
