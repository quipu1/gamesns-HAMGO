package com.web.curation.dao.follow;

import com.web.curation.model.follow.Following;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FollowingDao extends JpaRepository<Following, Long> {

    List<Following> findFollowingByFromId(String fromId);

}
