package kr.spring.member.controller;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.AuthCheckException;


@Controller
public class MemberController {
	//로그생성 
	private static final Logger logger = 
			LoggerFactory.getLogger(MemberController.class);

	@Autowired //memberSerVice 주입 
	private MemberService memberService;

	//폼을 호출하기 위한 자바빈(VO) 초기화
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}

	//=========회원가입============//
	//아이디 중복  체크
	@RequestMapping("/member/confirmId.do")
	@ResponseBody
	public Map<String,String> Idprocess(
			             @RequestParam String mem_id){
		logger.debug("<<id>> : " + mem_id);
		
		//Map에 담아서 데이터를 처리
		Map<String,String> mapAjax = 
				new HashMap<String,String>();
		
		MemberVO member = 
				memberService.selectCheckMember(mem_id);
		if(member!=null) {
			//아이디 중복
			mapAjax.put("result", "idDuplicated");
		}else {
			if(!Pattern.matches("^[A-Za-z0-9]{4,12}$", mem_id)) {
				//패턴 불일치
				mapAjax.put("result", "notMatchPattern");
			}else {
				//패턴 일치하면서 아이디 미중복
				mapAjax.put("result", "idNotFound");
			}
		}
		
		return mapAjax;
	}
	
	//닉네임 중복 체크
		@RequestMapping("/member/confirmNickname.do")
		@ResponseBody
		public Map<String,String> Nicknameprocess(
				             @RequestParam String mem_nickname){
			logger.debug("<<nickname>> : " + mem_nickname);
			
			//Map에 담아서 데이터를 처리
			Map<String,String> mapAjax = 
					new HashMap<String,String>();
			
			MemberVO member = 
					memberService.selectCheckNickname(mem_nickname);
			if(member!=null) {
				//닉네임 중복
				mapAjax.put("result", "nicknameDuplicated");
			}else {
				//닉네임 미중복
					mapAjax.put("result", "nicknameNotFound");
			}
			
			return mapAjax;
		}

	
	
	//회원가입 폼 호출
	@GetMapping("/member/registerUser.do")
	public String form(Model model) {
		
		//선호지역 목록 
		List<MemberVO> countryList = memberService.getCountryList();
		logger.debug("<<회원가입 - countryList>> : " + countryList);
		model.addAttribute("countryList", countryList);
		
		//관심사 목록
		List<MemberVO> likeList = memberService.getLikeList();
		logger.debug("<<회원가입 - likeList>> : " + likeList);	
		model.addAttribute("likeList", likeList);
		
  	  
		
		return "memberRegister";//타일스 설정값
	}

	//회원가입 데이터 전송
	@PostMapping("/member/registerUser.do")
	public String submit(@Valid MemberVO memberVO,
			BindingResult result, Model model) {

		//로그처리
		//memberVO에 담긴 오류를 보여줌
		logger.debug("<<회원가입>> : " + memberVO);

		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form(model);
		}

		//회원가입
		memberService.insertMember(memberVO);

		model.addAttribute("accessMsg", 
				"회원가입이 완료되었습니다.");

		//tiles 를 설정하지 않고 독립적으로 페이지가 보여지게함
		return "common/notice";

	}
	
	//=========회원로그인============//
	//로그인 폼 호출
		@GetMapping("/member/login.do")
		public String formLogin() {
			return "memberLogin";
		}
		
	
	//로그인 폼에 전송된 데이터 처리
		@PostMapping("/member/login.do")
		                      //자동 로그인 처리에 필요한 session,response 저장 
		public String submitLogin(@Valid MemberVO memberVO,
				              BindingResult result,
				              HttpSession session,
				              HttpServletResponse response) {
			
			logger.debug("<<회원로그인>> : " + memberVO);
			
			//유효성 체크 결과 오류가 있으면 폼을 호출
			//id와 passwd 필드만 체크
			if(result.hasFieldErrors("mem_id") || 
					result.hasFieldErrors("mem_pw")) {
				return formLogin();
			}
			
			//로그인 체크
			MemberVO member = null;
			//예외를 던지는 방법을 사용 
			                   //id를 selectCheckMember에 넘겨서 존재하는지 안하는지 체크 
			try {
				member = memberService.selectCheckMember(
						                   memberVO.getMem_id());
				//check가 false면 로그인 실패
				boolean check = false;
				
				if(member!=null) {
					//비밀번호 일치 여부 체크
					                  //입력한 비밀번호 넣어주기
					check = member.isCheckedPassword(
							       memberVO.getMem_pw());
				}
				if(check) {//인증 성공(chect값 확인하기)
					
					//자동로그인 체크
					boolean autoLogin = memberVO.getAuto() != null 
							          && memberVO.getAuto().equals("on");
					if(autoLogin) {
						//자동로그인 체크를 한 경우
						String mem_au_id = member.getMem_au_id();
						if(mem_au_id==null) {
							//자동로그인 체크 식별값 생성
							mem_au_id = UUID.randomUUID().toString();
							logger.debug("<<au_id>> : " + mem_au_id);
							memberService.updateAu_id(mem_au_id, 
									           memberVO.getMem_id());
						}
						
						Cookie auto_cookie = 
								  new Cookie("au-log",mem_au_id);
						//쿠키의 유효기간은 1주일
						auto_cookie.setMaxAge(60*60*24*7);
						auto_cookie.setPath("/");
						
						//생성한 쿠키를 클라이언트에 전송
						response.addCookie(auto_cookie);
						
					}
					
					//인증 성공, 로그인 처리
					//필요한 내용을 user에 저장해서 필요하면 가져다쓰기 
					session.setAttribute("user", member);
					
					logger.debug("<<인증 성공>> : " + member.getMem_id());
					
					
					//관리자는 관리자 메인으로 이동
					if(member.getMem_auth() == 9) {
						return "redirect:/main/admin.do";
						
					//사용자는 사용자 메인으로 이동
					}else {
						return "redirect:/main/main.do";
					}
				}
				//인증 실패 > 예외 처리
				throw new AuthCheckException();
				 //예외처리 
			}catch(AuthCheckException e) {
				//인증 실패로 로그인폼 호출
				if(member!=null && member.getMem_auth()==0) {
					//정지회원 메시지 표시
					result.reject("noAuthority");
				}else {
					result.reject("invalidIdOrPassword");
				}
				
				logger.debug("<<인증 실패>>");
				
				return formLogin();
			}
		}
		//=========회원로그아웃============//
		@RequestMapping("/member/logout.do")
		public String processLogout(HttpSession session,
				          HttpServletResponse response) {
			
			//로그아웃
			session.invalidate();
			
			//자동로그인 클라이언트 쿠키 처리
			//자동로그인 쿠키 삭제
			Cookie auto_cookie = new Cookie("au-log","");
			
			auto_cookie.setMaxAge(0);//쿠키 유효시간 만료
			auto_cookie.setPath("/");
			
			//클라이언트에 쿠키 전송
			response.addCookie(auto_cookie);		
			
			return "redirect:/main/main.do";
		}
		
		
		//=========아이디찾기============//
		//아이디찾기 폼 호출 
		@GetMapping("/member/idSearch.do")
		public String idSearchForm() {
			logger.debug("<<아이디 찾기 진입>>");
			
			return "memberIdSearch";
		}
		//아이디찾기 폼에 전송된 데이터 처리
		  
		
	
		
}









