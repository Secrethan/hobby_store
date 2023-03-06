package kr.spring.member.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.member.vo.MemberVO;

@Mapper
public interface MemberMapper {
	//회원관리 - 일반회원
	@Select("SELECT member_seq.nextval FROM dual")
	public int selectMem_num();
	@Insert("INSERT INTO member (mem_num,mem_id,mem_nickname) VALUES (#{mem_num},#{mem_id},#{mem_nickname})")
	public void insertMember(MemberVO member);
	@Insert("INSERT INTO member_detail (mem_num,"
			  + "mem_name,mem_pw,mem_cell,mem_email,mem_zipcode,mem_address1,"
			  + "mem_address2,country_num,like_num) VALUES (#{mem_num},"
			  + "#{mem_name},#{mem_pw},#{mem_cell},#{mem_email},"
			  + "#{mem_zipcode},#{mem_address1},#{mem_address2},#{country_num},#{like_num})")
	public void insertMember_detail(MemberVO member);
	@Select("SELECT m.mem_num,m.mem_id,m.mem_auth,m.mem_au_id,"
			  + "d.mem_pw,m.mem_nickname,d.mem_email,d.mem_cell FROM "
			  + "member m LEFT OUTER JOIN member_detail d "
			  + "ON m.mem_num=d.mem_num WHERE m.mem_id=#{mem_id}")
	public MemberVO selectCheckMember(String mem_id);
	@Select("SELECT m.mem_num,m.mem_id,m.mem_auth,m.mem_au_id,"
			  + "d.mem_pw,m.mem_nickname,d.mem_email FROM "
			  + "member m LEFT OUTER JOIN member_detail d "
			  + "ON m.mem_num=d.mem_num WHERE m.mem_nickname=#{mem_nickname}")
	public MemberVO selectCheckNickname(String mem_nickname);
  
	//선호지역
	@Select("SELECT * FROM member_country")
	public List<MemberVO> getCountryList();
		
	//주요관심사
	@Select("SELECT * FROM member_like")
	public List<MemberVO> getLikeList();
	
	//자동로그인
	@Update("UPDATE member SET mem_au_id=#{mem_au_id} WHERE mem_id=#{mem_id}")
	public void updateAu_id(@Param("mem_au_id") String mem_au_id, @Param("mem_id") String mem_id);
	@Select("SELECT m.mem_num,m.mem_id,m.mem_auth,m.mem_au_id,"
		  + "d.mem_pw,m.mem_nickname,d.mem_email FROM "
		  + "member m JOIN member_detail d ON "
		  + "m.mem_num=d.mem_num WHERE m.mem_au_id=#{mem_au_id}")
	public MemberVO selectAu_id(String mem_au_id);
	@Update("UPDATE member SET mem_au_id='' WHERE mem_num=#{mem_num}")
	public void deleteAu_id(int mem_num);
	
	//아이디찾기
	@Select("SELECT mem_id FROM member m LEFT OUTER JOIN "
			+ "member_detail d ON m.mem_num=d.mem_num "
			+ "WHERE d.mem_email=#{mem_email} AND d.mem_cell=#{mem_cell}")
	public String selectIdSearch(MemberVO vo);	
	
	//비밀번호 변경 
	@Update("UPDATE member_detail SET mem_pw = #{mem_pw} WHERE mem_num=#{mem_num}")
	public void updateMemberPasswd(MemberVO member); // 마이페이지 - 회원 비밀번호 변경

}
