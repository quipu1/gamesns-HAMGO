package com.web.curation.dao.reply;

import com.web.curation.model.reply.Reply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import java.util.Optional;

public interface ReplyDao extends JpaRepository<Reply, Long> {
    // rid로 댓글 검삭
    Optional<Reply> findReplyByRid(Long rid);
    
    // bid에 해당하는 댓글 검색
    Optional<Page<Reply>> findByBid(Long bid, Pageable pageable);
    
    // bid로 검색하면서 rid입력으로 주어진 rid보다 큰 값을 pageable 속성에 맞게 검색  
    Slice<Reply> findByBidAndRidGreaterThan(Long bid, Long rid, Pageable pageable);

    List<Reply> findReplyByBid(Long bid);

    Long countReplyByUid(String uid);
}
