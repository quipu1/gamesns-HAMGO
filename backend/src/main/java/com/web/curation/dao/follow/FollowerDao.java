package com.web.curation.dao.follow;

import com.web.curation.model.follow.Follower;
import com.web.curation.model.follow.Following;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowerDao extends JpaRepository<Follower, Long> {

    List<Follower> findFollowerByToId(String toId);

    Long countFollowerByToId(String toId);

}
