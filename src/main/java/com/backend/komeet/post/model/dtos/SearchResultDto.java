package com.backend.komeet.post.model.dtos;

import com.backend.komeet.post.enums.Categories;
import com.backend.komeet.post.enums.PostStatus;
import com.backend.komeet.post.model.entities.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 검색 결과 DTO
 */
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
     */
    public static SearchResultDto from(
            Post post,
            String keyword
    ) {
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
                .tags(extractTags(post.getTags(), keyword))
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
     */
    private static String extractKeyword(
            String text,
            String keyword,
            int after,
            int before
    ) {

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
     */
    private static List<String> extractTags(
            List<String> tags,
            String keyword
    ) {
        if (tags.isEmpty()) {
            return Collections.emptyList();
        }

        // keyword를 포함하는 첫 번째 태그를 리스트로 변환
        List<String> keywordTagList = tags.stream()
                .filter(tag -> tag.contains(keyword))
                .limit(1)
                .collect(Collectors.toList());

        boolean isAdded = !keywordTagList.isEmpty();

        // 남는 태그들을 섞고 적절한 개수 선택하기
        List<String> shuffledTags = tags.stream()
                .filter(tag -> !keywordTagList.contains(tag)) // 이미 추가된 태그는 제외
                .collect(Collectors.collectingAndThen(
                        Collectors.toList(), collected -> {
                            Collections.shuffle(collected);
                            return collected.stream()
                                    .limit(isAdded ?
                                            Math.min(2, collected.size()) :
                                            Math.min(3, collected.size())
                                    ).collect(Collectors.toList());
                        }
                ));

        // 두 리스트를 합쳐서 결과 리스트 생성
        List<String> result = new ArrayList<>(keywordTagList);
        result.addAll(shuffledTags);
        return result;
    }

}
