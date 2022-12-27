package com.web.curation.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.web.curation.dao.ImgFile.ImgFileDao;
import com.web.curation.dao.board.BoardDao;
import com.web.curation.dao.follow.FollowingDao;
import com.web.curation.dao.member.MemberDao;
import com.web.curation.model.board.AddBoard;
import com.web.curation.model.board.Board;
import com.web.curation.model.board.ResponseBoard;
import com.web.curation.model.file.ImgFile;

@Service
public class BoardService {

	@Autowired
	BoardDao boardDao;
	@Autowired
	FollowingDao followingDao;
	@Autowired
	ImgFileDao imgFileDao;
	@Autowired
	MemberDao memberDao;
	
	
	public Object bList(String uid,String bid){

		long longbid;
		if(bid == null) longbid = Long.MAX_VALUE;//없으면 최대값
		else longbid = Long.parseLong(bid);//있으면 해당 bid 밑으로

		Pageable paging = PageRequest.of(0, 10);//최신부터 10개(0페이지에 10개)
		
		List<Board> boardList;

		if(followingDao.findFollowingByFromId(uid).size() > 0) {//내가 follow 하는 사람의 리스트
			boardList = boardDao.findFollowFeedByUid(longbid, uid, paging);
		}
		else {
			boardList = boardDao.findFollowFeed(longbid, paging);
		}
		List<ResponseBoard> resboard = new ArrayList<>();

		for (Board board : boardList) {
			resboard.add(new ResponseBoard(board,imgFileDao.findImgFileByBid(board.getBid()),memberDao.findByUid(board.getUid()).get().getNickname()));
		}

		return resboard;

	}

	public Object bEqualList(String uid, String bid){
		
		long longbid;
		if(bid == null) longbid = Long.MAX_VALUE;//없으면 최대값
		else longbid = Long.parseLong(bid);//있으면 해당 bid 밑으로
		
		
		//Pageable paging = PageRequest.of(0, 10);//최신부터 10개(0페이지에 10개)
		Pageable pageRequest = PageRequest.of(0, 10, Sort.Direction.DESC, "createDate");
		List<Board> boardList = boardDao.findBoardByUidAndBidLessThan(uid, longbid, pageRequest);

		List<ResponseBoard> resboard = new ArrayList<>();

		for (Board board : boardList) {
			resboard.add(new ResponseBoard(board,imgFileDao.findImgFileByBid(board.getBid()),memberDao.findByUid(board.getUid()).get().getNickname()));
		}

		return resboard;

	}
	
	@Transactional
	public void addBoard(AddBoard newBoard) throws IllegalStateException, IOException{

		Board board = new Board();
		board.setUid(newBoard.getUid());
		board.setContents(newBoard.getContent());
		board.setHashtags(newBoard.getHashtags());
		board = boardDao.save(board);
		
		String path = "C:\\upload";
		File Folder = new File(path);
		if(!Folder.exists()) Folder.mkdir();
		
		String fileName;
		MultipartFile[] multipartFiles;
		
		if(newBoard.getMultipartFiles() != null) {//파일이 존재할 때에만,
			multipartFiles = newBoard.getMultipartFiles();
			
			for (int i = 0; i < multipartFiles.length; i++) {
	
				MultipartFile multipartFile = multipartFiles[i];
				UUID uuid = UUID.randomUUID();
	
				fileName = uuid.toString()+"_"+multipartFile.getOriginalFilename();
				multipartFile.transferTo(new File("C:\\upload"+"\\"+fileName));
				String base_url = "C:\\upload"+"\\"+fileName;
	
				ImgFile file = new ImgFile();//이미지 파일 세팅
				file.setFile_name(fileName);
				file.setFile_base_url(base_url);
				file.setFile_size(Long.toString(multipartFile.getSize()));
				file.setBid(board.getBid());
	
				imgFileDao.save(file);
			}
		}
	}

	public void modifyBoard(long bid ,AddBoard newBoard) throws IllegalStateException, IOException{

		Board board = boardDao.findBoardByBid(bid);
		board.setUid(newBoard.getUid());
		board.setContents(newBoard.getContent());
		String fileName;


		List<ImgFile> imgList = imgFileDao.findImgFileByBid(bid);//연관된 파일 삭제
		imgFileDao.deleteAll(imgList);

		for (ImgFile imgFile : imgList) {
			File file = new File(imgFile.getFile_base_url());//연관된 파일 삭제
			file.delete();
		}

		if(newBoard.getMultipartFiles() != null) {//파일이 존재할 때에만,
			MultipartFile[] multipartFiles = newBoard.getMultipartFiles();
	
			for (int i = 0; i < multipartFiles.length; i++) {//재등록
	
				MultipartFile multipartFile = multipartFiles[i];
				UUID uuid = UUID.randomUUID();
	
				fileName = uuid.toString()+"_"+multipartFile.getOriginalFilename();
				multipartFile.transferTo(new File("C:\\upload"+"\\"+fileName));
				String base_url = "C:\\upload"+"\\"+fileName;
	
				ImgFile file = new ImgFile();//이미지 파일 세팅
				file.setFile_name(fileName);
				file.setFile_base_url(base_url);
				file.setFile_size(Long.toString(multipartFile.getSize()));
				file.setBid(board.getBid());
	
				imgFileDao.save(file);
			}
		}
		board = boardDao.save(board);
	}
	
	@Transactional
	public void deleteBoard(long bid) throws IllegalStateException, IOException{

		List<ImgFile> imgList = imgFileDao.findImgFileByBid(bid); //연관된 파일 검색

		for (ImgFile imgFile : imgList) {
			File file = new File(imgFile.getFile_base_url()); //연관된 파일 삭제
			if(file.exists()) file.delete();
		}

		boardDao.delete(boardDao.findBoardByBid(bid)); //삭제

	}
}
