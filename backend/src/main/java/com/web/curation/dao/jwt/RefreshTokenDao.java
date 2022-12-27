package com.web.curation.dao.jwt;

import com.web.curation.model.jwt.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface RefreshTokenDao extends CrudRepository<RefreshToken, String> {
    Optional<RefreshToken> findByKey(String key);
}
