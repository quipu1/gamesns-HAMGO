package com.web.curation.model.file;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.web.curation.model.board.Board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="file")
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImgFile {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long no;

    private String file_name;
    private String file_base_url;
    private String file_size;

    private long bid;
	
	
}
