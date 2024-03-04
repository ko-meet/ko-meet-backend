package com.backend.komeet.dto;

import com.backend.komeet.domain.Post;
import com.backend.komeet.enums.Categories;
import com.backend.komeet.enums.PostStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SearchResultDto {
    private Long seq;
    private String title;
    private String content;
    private Long userSeq;
    private Long likeCount;
    private Long viewCount;
    private String isPublic;
    private String userProfileUrl;
    private String userNickName;
    private Integer commentCounts;
    private List<String> tags;
    private List<String> attachments;
    private List<Long> likeUsers;
    private List<Long> bookmarkUsers;
    private String country;
    private String region;
    private Categories category;
    private PostStatus status;
    private String keyword;
    private String createdAt;

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
                .seq(post.getSeq())
                .title(titleResult)
                .content(contentResult)
                .userSeq(post.getUser().getSeq())
                .userProfileUrl(post.getUser().getImageUrl())
                .userNickName(post.getUser().getNickName())
                .commentCounts(post.getComments().size())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .tags(extractTags(post.getTags(),keyword))
                .attachments(post.getAttachments())
                .likeUsers(post.getLikeUsers())
                .bookmarkUsers(post.getBookmarkUsers())
                .isPublic(post.getIsPublic())
                .country(post.getUser().getCountry().getCountryName())
                .status(post.getStatus())
                .region(post.getUser().getRegion())
                .category(post.getCategory())
                .keyword(keyword)
                .createdAt(post.getCreatedAt().toString())
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

    /**
     * 키워드를 기준으로 태그를 추출하는 메서드
     * @param tags 태그 목록
     * @param keyword 키워드
     * @return 추출된 태그 목록
     */
    private static List<String> extractTags(List<String> tags, String keyword) {
        List<String> result = new ArrayList<>();
        List<String> shuffledTags = new ArrayList<>(tags);
        for (String tag : tags) {
            if (tag.contains(keyword)) {
                result.add(tag);
                shuffledTags.remove(tag);
                break;
            }
        }
        if (result.isEmpty()) {
            return result;
        }
        Collections.shuffle(shuffledTags);
        for (int i = 0; i < Math.min(2, shuffledTags.size()); i++) {
            result.add(shuffledTags.get(i));
        }
        return result;
    }



}
