package com.kosmo.rest.service;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

@Repository
public class MemberDAO {

	@Autowired
	private SqlSessionTemplate template;
	public int insert(MemberDTO member) {
		return template.insert("memberInsert", member);
	}
	public List<MemberDTO> selectList() {
		
		return template.selectList("memberSelectList");
	}
	public MemberDTO selectOne(String username) {		
		return template.selectOne("memberSelectOne",username);
	}
	public int update(MemberDTO dto) {	
	
		return template.update("memberUpdate",dto);
	}
	public int delete(String username) {
		return template.delete("memberDelete",username);		
	}
	public MemberDTO join(Map map) {
		
		return template.selectOne("memberJoin",map);
	}
	
	public List<Map> selectTokens() {
		return template.selectList("selectTokens");
	}
	public int insertToken(String token) {
		
		return template.insert("insertToken",token);
	}
}
