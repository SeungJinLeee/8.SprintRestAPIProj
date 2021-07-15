package com.kosmo.rest;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kosmo.rest.service.MemberDAO;
import com.kosmo.rest.service.MemberDTO;
import com.kosmo.rest.service.PhotoDTO;

import lombok.Delegate;

@RestController
public class RestAPIController {
	
	@Autowired
	private MemberDAO memberDAO;
	
	/*
	 * POST http://localhost:9090/member
	 * :key=value 형태로 전송
	 * :jackson-databind가 작동하지 않음
	 * 
	 * -form태그를 이용해서 전송하거나
	 *  <form method="post" action="http://localhost:9090/member1">
	 *  	<input type="text" name="username"/>
	 *  	<input type="password" name="password"/>
	 *  	<input type="text" name="name"/>
	 *  	<<input type="submit" value="입력"/>
	 *  </form>
	 *  -jQuery ajax로 전송시에는 data속성에 key=value 형태로
	 *  $.ajax({
	 *  	url:"http://localhost:9090/member1",
	 *  	dataType:"text",
	 *  	type:"post",
	 *      data:"username=KIM&password=1234&name=홍길동",
	 *      ~
	 *  
	 *  });
	 *  -postman으로 전송시에는 Body탭의 x-www-form-urlencoded 선택후
	 *   key와 value입력
	 * 
	 */
	@CrossOrigin
	@PostMapping(value="/member1",produces = "text/plain;charset=UTF-8")
	public String insert1(MemberDTO member) {
		int affected=memberDAO.insert(member);
		return "1행이 입력됨(key=value쌍으로 받기)";
	}
	/*
	 * POST http://localhost:9090/member2
	 * :json으로 데이터 받을때
	 * :jackson-databind가 작동해서 json으로 받은 데이타를 DTO로 컨버팅
	 	-jQuery ajax로 전송시에는 data속성에 자바스크립트의 객체로
	 	var json = {"username":"KIM","password":"1234","name":"홍길동"};
	 *  $.ajax({
	 *  	url:"http://localhost:9090/member1",
	 *  	dataType:"text",
	 *  	type:"post",
	 *      data:json,
	 *      contentType:"application/json;charset=UTF-8",
	 *      ~
	 *  
	 *  });
	 *  -postman으로 전송시에는 Body탭의 raw 선택 및 json선택후
	 *   json형태로 데이터 작성
	 */
	@CrossOrigin
	@PostMapping(value="/member2",produces = "text/plain;charset=UTF-8")
	public String insert2(@RequestBody MemberDTO member) {
		int affected=memberDAO.insert(member);
		return "1행이 입력됨(JSON으로 데이타 받기)";
	}
	/*
	 * GET http://localhost:9090/members
	 * :JSON으로 반환
	 * :jackson-databind가 작동해서 DTO를 JSON으로 컨버팅해서 반환
	 * -form태그 혹은 a태그
	 * -jQuery ajax
	 * -postman
	 */
	@CrossOrigin
	@GetMapping("/members")
	public List<MemberDTO> selectList(){
		List<MemberDTO> members=memberDAO.selectList();
		return members;
	}/////////////////
	/*
	 * GET http://localhost:9090/members/{username}
	 * :JSON으로 반환
	 * :jackson-databind가 작동해서 DTO를 JSON으로 컨버팅해서 반환
	 * -form태그 혹은 a태그
	 * -jQuery ajax
	 * -postman
	 */
	@GetMapping("/members/{username}")
	public MemberDTO selectOne(@PathVariable String username){
		MemberDTO member=memberDAO.selectOne(username);
		return member;
	}/////////////////
	//※PUT이나 DELETE도 데이타는 요청바디에 싣는다
	//반드시 JSON으로 받는다 @RequestBody MemberDTO dto
	@CrossOrigin
	@PutMapping("/members/{username}")
	public MemberDTO update(@PathVariable String username,@RequestBody MemberDTO dto){
		dto.setUsername(username);
		dto.setPostdate(memberDAO.selectOne(username).getPostdate());//날짜가 null반환되지 않도록
		
		memberDAO.update(dto);		
		return dto;
	}/////////////////
	@CrossOrigin
	@DeleteMapping("/members/{username}")
	public MemberDTO delete(@PathVariable String username){
		//삭제전 반환할 DTO얻기
		MemberDTO dto=memberDAO.selectOne(username);
		memberDAO.delete(username);
		
		return dto;//삭제된 멤버 반환
	}/////////////////
	/*
	 * POST http://localhost:9090/file
	 * :key=value 형태로 전송
	 * 
	 * 
	 * -form태그를 이용해서 전송하거나
	 *  <form method="post" action="http://localhost:9090/file" enctype="multipart/form-data">
	 *  	<input type="file" name="attachFile"/>
	 *  	<input type="text" name="title"/>	 *  	
	 *  	<<input type="submit" value="입력"/>
	 *  </form>
	 *  
	 *  
	 *  });
	 *  -postman으로 전송시에는 Body탭의 form-data 선택후
	 *   key와 value입력
	 *   파일인 경우 key입력시 옆에 file선택 
	 * 
	 */
	@CrossOrigin
	@PostMapping(value="/file",produces = "text/plain;charset=UTF-8")
	//attachFile는 파라미터명
	public String upload(@RequestPart MultipartFile attachFile,HttpServletRequest req) throws IllegalStateException, IOException {
		//서버의 물리적 경로 얻기
		String path = req.getSession().getServletContext().getRealPath("/uploads");
		//File객체 생성
		File file = new File(path+File.separator+attachFile.getOriginalFilename());
		//업로드
		attachFile.transferTo(file);
		
		System.out.println("기타 파라미터 받기:"+req.getParameter("title"));
		return "파일 업로드 성공";
	}
	
	//[안드로이드 앱에 데이타 제공용 추가]
	@CrossOrigin
	@GetMapping("/member/join")
	public MemberDTO join(@RequestParam Map map) {
		
		MemberDTO dto = memberDAO.join(map);
		System.out.println("회원 여부:"+dto);
		if(dto ==null) {
			dto = new MemberDTO(null, null, null, null);
		}
		return dto;
	}
	@CrossOrigin
	@PostMapping("/member/join")
	public MemberDTO joinPost(@RequestParam Map map) {		
		MemberDTO dto = memberDAO.join(map);
		System.out.println("회원 여부:"+dto);
		if(dto ==null) {
			dto = new MemberDTO(null, null, null, null);
		}
		return dto;
	}
	@CrossOrigin
	@GetMapping("/photos")
	public List<PhotoDTO> photos(){
		//데이타베이스 연동하지 않고 하드 코딩하자
		List<PhotoDTO> photos = new Vector<PhotoDTO>();
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/92c952.png", "첫번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/ffaabb.png", "두번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/46fa99.png", "세번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/997788.png", "네번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/9abbaa.png", "다섯번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/115588.png", "여섯번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/888888.png", "일곱번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/5599aa.png", "여덟번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/bb1144.png", "아홉번째 이미지"));
		photos.add(new PhotoDTO("http://192.168.0.25:9090/uploads/ffbbaa.png", "열번째 이미지"));
		
		return photos;
	}
	@CrossOrigin
	@PostMapping(value="/photo/upload",produces = "text/plain;charset=UTF-8")
	//attachFile는 파라미터명
	public String upload_photo(@RequestPart MultipartFile attachFile,HttpServletRequest req) throws IllegalStateException, IOException {
		//서버의 물리적 경로 얻기
		String path = req.getSession().getServletContext().getRealPath("/uploads");
		//File객체 생성
		File file = new File(path+File.separator+attachFile.getOriginalFilename());
		//업로드
		attachFile.transferTo(file);		
		System.out.println("기타 파라미터 받기:"+req.getParameter("title"));
		return "파일 업로드 성공";
	}
	
	//데이타베이스(FCM_TOKENS)에 토큰 입력처리용
	@CrossOrigin
	@PostMapping(value="/token",produces = "text/plain;charset=utf-8")
	public String insertToken(@RequestParam String token) {
		int affected=memberDAO.insertToken(token);
		return affected==1?"입력 성공":"입력 실패";
	}/////////////////
	
	
	
	
}
