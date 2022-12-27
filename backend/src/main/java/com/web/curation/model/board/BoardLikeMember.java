package com.web.curation.model.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BoardLikeMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    // 좋아요를 누른 사람
    private String uid;
    private Long bid;

}

