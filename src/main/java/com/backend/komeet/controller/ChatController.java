package com.backend.komeet.controller;

import com.backend.komeet.config.JwtProvider;
import com.backend.komeet.dto.ChatDto;
import com.backend.komeet.dto.ChatRoomDto;
import com.backend.komeet.dto.request.ChatContentRequest;
import com.backend.komeet.dto.response.ApiResponse;
import com.backend.komeet.service.chat.ChatRoomService;
import com.backend.komeet.service.chat.ChatService;
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
     *
     * @param token 토큰
     * @param page  페이지
     * @return {@link ResponseEntity<ApiResponse>} 채팅방 목록
     */
    @ApiOperation(value = "채팅방 목록조회", notes = "채팅방 목록을 조회합니다.")
    @GetMapping("/rooms")
    public ResponseEntity<ApiResponse> getChatRooms(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        Page<ChatRoomDto> chatList = chatRoomService.getChatRooms(userSeq, page);
        return ResponseEntity.status(OK).body(new ApiResponse(chatList));
    }

    /**
     * 채팅 내역 조회
     *
     * @param token       토큰
     * @param chatRoomSeq 채팅방 번호
     * @param page        페이지
     * @return {@link ResponseEntity<ApiResponse>} 채팅 내역
     */
    @ApiOperation(value = "채팅 내역 조회", notes = "채팅 내역을 조회합니다.")
    @GetMapping("/rooms/{chatRoomSeq}")
    public ResponseEntity<ApiResponse> getChats(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long chatRoomSeq,
            @RequestParam(value = "page", defaultValue = "0") Integer page) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        Page<ChatDto> chatList = chatService.getChats(chatRoomSeq, userSeq, page);
        return ResponseEntity.status(OK).body(new ApiResponse(chatList));
    }

    /**
     * 채팅방 생성
     *
     * @param token          토큰
     * @param counterpartSeq 상대방 번호
     * @return {@link ResponseEntity<ApiResponse>} 채팅방 생성 결과
     */
    @ApiOperation(value = "채팅방 생성", notes = "채팅방을 생성합니다.")
    @PostMapping("/rooms")
    public ResponseEntity<ApiResponse> createChatRoom(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam Long counterpartSeq) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        Long chatRoomSeq = chatRoomService.createChatRoom(userSeq, counterpartSeq);
        return ResponseEntity.status(OK).body(new ApiResponse(chatRoomSeq));
    }

    /**
     * 채팅 추가
     *
     * @param token       토큰
     * @param chatRoomSeq 채팅방 번호
     * @param content     {@link ChatContentRequest} 채팅 내용
     * @return {@link ResponseEntity<ApiResponse>} 채팅 추가 결과
     */
    @ApiOperation(value = "채팅 추가", notes = "채팅을 추가합니다.")
    @PostMapping("/rooms/{chatRoomSeq}")
    public ResponseEntity<ApiResponse> addChat(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long chatRoomSeq,
            @RequestBody ChatContentRequest content) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        chatService.addChat(chatRoomSeq, userSeq, content);
        return ResponseEntity.status(OK).body(new ApiResponse(OK.value()));
    }

    /**
     * 채팅방 삭제
     *
     * @param token       토큰
     * @param chatRoomSeq 채팅방 번호
     * @return {@link ResponseEntity<ApiResponse>} 채팅방 삭제 결과
     */
    @ApiOperation(value = "채팅방 삭제", notes = "채팅방을 삭제합니다.")
    @DeleteMapping("/rooms/{chatRoomSeq}")
    public ResponseEntity<ApiResponse> deleteChatRoom(
            @RequestHeader(AUTHORIZATION) String token,
            @PathVariable Long chatRoomSeq) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        chatRoomService.deleteChatRoom(chatRoomSeq, userSeq);
        return ResponseEntity.status(NO_CONTENT).build();
    }

    /**
     * 채팅방 검색
     *
     * @param token   토큰
     * @param keyword 검색어
     * @return {@link ResponseEntity<ApiResponse>} 채팅방 목록
     */
    @ApiOperation(value = "채팅방 검색", notes = "채팅방을 검색합니다.")
    @GetMapping("/rooms/search")
    public ResponseEntity<ApiResponse> searchChatRooms(
            @RequestHeader(AUTHORIZATION) String token,
            @RequestParam String keyword) {
        Long userSeq = jwtProvider.getIdFromToken(token);
        return ResponseEntity
                .status(OK)
                .body(new ApiResponse(chatRoomService.searchChatRooms(userSeq, keyword)));
    }
}
