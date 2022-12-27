package com.web.curation.dao.badge;

import java.util.ArrayList;
import java.util.List;

import com.web.curation.model.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BadgeDao extends JpaRepository<Badge, Integer> {

    List<Badge> findBadgeByTypeAndLowcutLessThanEqual(String type, int lowcut);

}