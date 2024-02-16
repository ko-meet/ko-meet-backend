package com.backend.komeet.dto;

import com.backend.komeet.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultDto {
    private Long postSeq;
    private String country;
    private String author;
    private String title;
    private String content;
    private LocalDateTime createdAt;

    /**
     * 게시물을 검색 결과로 변환하는 메서드
     * @param post 검색된 게시물
     * @param keyword 검색 키워드
     * @return 검색 결과 DTO
     */
    public static SearchResultDto from(Post post, String keyword) {
        String contentResult = extractKeyword(
                post.getContent(), keyword, 50, 5
        );

        String titleResult = extractKeyword(
                post.getTitle(), keyword, 20, 5
        );

        return SearchResultDto.builder()
                .postSeq(post.getSeq())
                .country(post.getCountry().name())
                .author(post.getUser().getNickName())
                .title(titleResult)
                .content(contentResult)
                .createdAt(post.getCreatedAt())
                .build();
    }

    /**
     * 키워드를 기준으로 앞뒤로 일정 길이만큼의 문자열을 추출하는 메서드
     * @param text 추출할 문자열
     * @param keyword 키워드
     * @param after 키워드 뒤로 추출할 길이
     * @param before 키워드 앞으로 추출할 길이
     * @return 추출된 문자열
     */
    private static String extractKeyword(String text,
                                         String keyword,
                                         int after,
                                         int before) {

        int keywordIndex = text.indexOf(keyword);
        if (keywordIndex != -1) {
            int start = Math.max(keywordIndex - before, 0);
            int end = Math.min(keywordIndex + keyword.length() + after, text.length());
            return text.substring(start, end);
        } else {
            return text.substring(0, Math.min(text.length(), after));
        }
    }


}
