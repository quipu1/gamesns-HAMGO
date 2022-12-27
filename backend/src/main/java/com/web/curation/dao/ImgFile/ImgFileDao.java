package com.web.curation.dao.ImgFile;

import com.web.curation.model.file.ImgFile;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImgFileDao extends JpaRepository<ImgFile, String> {
	
	List<ImgFile> findImgFileByBid(long bid);
}
