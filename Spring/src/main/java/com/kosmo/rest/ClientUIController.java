package com.kosmo.rest;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsAsyncClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kosmo.rest.service.MemberDAO;
import com.kosmo.rest.service.MessageDTO;

@Controller
public class ClientUIController {
	//스프링 서버에서 Rest한 방식으로 http 통신을 하기위한 API
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private MemberDAO dao;
	
	//사용자 UI처리용(제목 및 내용 입력 UI)
	@GetMapping("/message")
	public String message() {	
		
		return "PushMessageSend";
	}
	@PostMapping("/message")
	public String sendMessage(MessageDTO messages,Model model) throws IOException {
		
		//RestTemplate API로 파이어베이스 클라우드 메시징 서버쪽에
		//내가 만든 앱을 설치한 모든 스마트폰에 메시지를 푸쉬하도록 POST요청 보내기
		//-Spring 3.0부터 지원하는 내장 클래스
		//-Rest방식으로 HTTP 통신을 동기 방식(AsyncRestTemplate는 비동기)으로 쉽게 할수 있는  템플릿
		//-RestTemplate은 기본적으로 커넥션풀을 지원하지 않는다(요청이 많은 경우 지연이 발생할 수 있다)
		// 커넥션풀을 사용하는 경우 아래 라이브러리 추가
		//-요청을 보낼때는 HttpEntity< Map혹은 DTO,HttpHeaders>타입에 요청헤더와 요청바디(데이타) 설정
		//-응답을 받을때는 ResponseEntity<Map혹은 DTO>로
		// 이때 DTO는 파이어베이스에서 응답해주는 JSON구조에 맞게 DTO를 생성해야한다
		/*
		 * <dependency>
		    <groupId>org.apache.httpcomponents</groupId>
		    <artifactId>httpclient</artifactId>
		    <version>4.5.13</version>
			</dependency>
		 */	
		//모든 토큰값 가져오기
		List<Map> tokens= dao.selectTokens();
		//각 토큰을 갖고 있는 스마트폰으로 푸쉬를 보내기위한 요청(파이어베이스로)
		int success=0;
		for(Map token:tokens) {
			for(Object tok:token.values()) {
				ResponseEntity<Map> responseEntity=requestToFCMServer(messages,tok.toString());
				System.out.println("상태코드:"+responseEntity.getStatusCodeValue());
				System.out.println("응답헤더:"+responseEntity.getHeaders());
				ObjectMapper mapper = new ObjectMapper();
				Map responseMap=mapper.readValue(mapper.writeValueAsString(responseEntity.getBody()),Map.class);
				if(Integer.parseInt(responseMap.get("success").toString())==1)
					success++;
				System.out.println("데이타(응답 바디-JSON):"+mapper.writeValueAsString(responseEntity.getBody()));
				System.out.println("데이타(응답 바디):"+responseEntity.getBody());
			}
		}
		
		model.addAttribute("success", success);
		return "PushMessageSend";
	}
	
	private ResponseEntity<Map> requestToFCMServer(MessageDTO messages,String token){
		
			
		//요청헤더 설정용 객체 생성
		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Type", "application/json");
		headers.add("Authorization", "key=AAAA4lrOPEA:APA91bH4JqGpUBaKzdwXIR3hUm06mrigyUVIpyS49qKOVUSESsPrWLTYcRyHNM1XQO94gjL5mQZ5R6731eG6UAcSY950csktwUB2B5JKSQ8wToO8cIjjd76Q31MRvc_0atWquuBYj-uG");
	   
		Map<String,Object> body = new HashMap<String, Object>();
		body.put("data",messages);
		body.put("to",token);
		//HttpEntity타입에 요청을 보낼 데이타와 헤더 설정
		//data:요청바디
		//headers:요청헤더
		//요청 헤더정보등을 담은 HttpEntity객체 생성]
		/*
		 * {"data":{"dataTitle":"제목입니다","dataBody":"내용입니다"},"to":"토큰값"}
		 * 
		 * 
		 */
		HttpEntity entity = new HttpEntity(body,headers);	    
		//RestTemplate으로 요청 보내기
		String uri ="https://fcm.googleapis.com/fcm/send";
		//한글 포함시
		UriComponents uriComponents= UriComponentsBuilder.fromHttpUrl(uri).build();
		//RestTemplate객체로 요청 보내기
		return restTemplate.exchange(
				uriComponents.toString(),//요청 URI
				HttpMethod.POST,//요청 메소드
				entity,//HttpEntity(요청바디와 요청헤더)
				Map.class,//응답데이타 타입
				String.class//uriVariables the variables to expand in the template
				);		
	}////////////////////

	
	

}
