package kr.spring.course.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import kr.spring.course.vo.CourseFavVO;
import kr.spring.course.vo.CourseReplyFavVO;
import kr.spring.course.vo.CourseReplyVO;
import kr.spring.course.vo.CourseTimeVO;
import kr.spring.course.vo.CourseVO;

@Mapper
public interface CourseMapper {
	//메인페이지 클래스 목록
	@Select("SELECT * FROM(SELECT a.*,rownum rnum FROM (SELECT * FROM course ORDER BY course_num DESC)a) WHERE rnum>=#{start} AND rnum<=#{end}")
	public List<CourseVO> selectCourseMainList(Map<String,Object> map);
	@Select("SELECT * FROM(SELECT a.*,rownum rnum FROM (SELECT * FROM course "
			+ "LEFT JOIN (SELECT course_num,COUNT(fav_num) AS fav "
			+ "FROM course_fav GROUP BY course_num) USING(course_num)"
			+ "ORDER BY fav DESC)a) WHERE rnum>=#{start} AND rnum<=#{end}")
	public List<CourseVO> selectCourseMainList2(Map<String,Object> map2);
	@Select("SELECT * FROM(SELECT a.*,rownum rnum FROM (SELECT * FROM course WHERE course_price <= 10000 ORDER BY course_num DESC)a) WHERE rnum>=#{start} AND rnum<=#{end}")
	public List<CourseVO> selectCourseMainList3(Map<String,Object> map3);
	
	//부모글
	public List<CourseVO> selectCourseList(Map<String,Object> map);
	@Select("SELECT course_seq.nextval FROM dual")
	public int selectCourse_num();
	public int selectCourseCount(Map<String,Object> map);
	//지도에 검색할 클래스 주소 가져오기
	public List<CourseVO> selectCourseAddress(Map<String,Object> map);
	//클래스 등록
	@Insert("INSERT INTO course (course_num,mem_num,course_name,status,course_onoff,course_oneweek,cate_nums,course_zipcode,course_address1,course_address2,"
			+ "course_photo1,course_photo_name1,course_photo2,course_photo_name2,course_photo3,course_photo_name3,"
			+ "course_startdate,course_month,course_count,course_limit,course_price,course_content) VALUES "
			+ "(#{course_num},#{mem_num},#{course_name},#{status},#{course_onoff},#{course_oneweek, jdbcType=VARCHAR},#{cate_nums},#{course_zipcode},"
			+ "#{course_address1},#{course_address2},#{course_photo1},#{course_photo_name1},#{course_photo2},#{course_photo_name2},#{course_photo3},#{course_photo_name3},"
			+ "#{course_startdate},#{course_month},#{course_count},#{course_limit},#{course_price},#{course_content})")
	public void insertCourse(CourseVO course);
	//클래스 시간 등록
	@Insert("INSERT INTO course_time (ctime_num,course_num,mem_num,course_reg_date,course_reg_time) VALUES "
			+ "(course_time_seq.nextval,#{course_num},#{mem_num},#{course_reg_date},#{course_reg_time})")
	public void insertCourse_time(CourseTimeVO vo);
	@Select("SELECT * FROM course_cate")
	public List<CourseVO> selectCate();
	@Select("SELECT cate_num FROM course_cate WHERE cate_name=#{cate_name}")
	public int selectCate_num(String cate_name);
	@Select("SELECT * FROM course JOIN member USING (mem_num) JOIN member_detail USING (mem_num) WHERE course_num = #{course_num}")
	public CourseVO selectCourse(Integer course_num);
	//해당 클래스의 요일,시간 리스트 호출
	@Select("SELECT * FROM course_time WHERE course_num = #{course_num}")
	public List<CourseTimeVO> selectCourseTime(Integer course_num);
	//선택한 요일에 해당하는 시간 호출
	@Select("SELECT * FROM course_time WHERE course_num = #{course_num} AND course_reg_date = #{course_reg_date}")
	public CourseTimeVO selectCourseTimes(Integer course_num,String course_reg_date);
	@Update("UPDATE course SET course_hit=course_hit+1 WHERE course_num=#{course_num}")
	public void updateHit(Integer course_num);
	@Select("SELECT reply_num FROM course_reply WHERE course_num = #{course_num}")
	public List<Integer> selectReplyNum(Integer course_num);
	//클래스 수정
	public void updateCourse(CourseVO course);
	@Delete("DELETE FROM course_time WHERE course_num = #{course_num}")
	public void deleteCourse_time(Integer course_num);
	//클래스 삭제
	@Delete("DELETE FROM course WHERE course_num = #{course_num}")
	public void deleteCourse(Integer course_num);
	//글삭제시 포함된 내용 다 삭제
	public void deleteCourseWithAll(Integer course_num);
	@Delete("DELETE FROM course_time WHERE course_num=#{course_num}")
	public void deleteCourseTime(Integer course_num);
	
	
	//클래스 사진 2,3 삭제
	@Update("UPDATE course SET course_photo2='',course_photo_name2='' WHERE course_num = #{course_num}")
	public void deletePhoto2(Integer course_num);
	@Update("UPDATE course SET course_photo1='',course_photo_name3='' WHERE course_num = #{course_num}")
	public void deletePhoto3(Integer course_num);
	//클래스가 담긴 장바구니 삭제
	@Delete("DELETE FROM course_cart WHERE course_num = #{course_num}")
	public void deleteCourseCart(Integer course_num);
	 
	//좋아요
	@Select("SELECT * FROM course FULL OUTER JOIN course_fav USING(course_num)")
	public List<CourseVO> selectFavCheck();
	@Select("SELECT * FROM course_fav WHERE course_num=#{course_num} AND fmem_num=#{fmem_num}")
	public CourseFavVO selectFav(CourseFavVO fav);
	@Select("SELECT COUNT(*) FROM course_fav WHERE course_num=#{course_num}")
	public int selectFavCount(Integer course_num);
	@Insert("INSERT INTO course_fav (fav_num,course_num,fmem_num) VALUES (course_fav_seq.nextval,#{course_num},#{fmem_num})")
	public void insertFav(CourseFavVO fav);
	@Delete("DELETE FROM course_fav WHERE fav_num=#{fav_num}")
	public void deleteFav(Integer fav_num);
	@Delete("DELETE FROM course_fav WHERE course_num=#{course_num}")
	public void deleteFavByCourseNum(Integer course_num);
	
	
	//후기
	public List<CourseReplyVO> selectListReply(Map<String,Object> map);
	public int selectReplyCount(Map<String,Object> map);
	@Select("SELECT * FROM course_reply WHERE reply_num=#{reply_num}")
	public CourseReplyVO selectReply(Integer reply_num);
	@Insert("INSERT INTO course_reply (reply_num,star_auth,reply_content,reply_photo1,reply_photo2,reply_photo3,reply_photo_name1,reply_photo_name2,reply_photo_name3,course_num,mem_num) "
			+ "VALUES (course_reply_seq.nextval,#{star_auth},#{reply_content},#{reply_photo1,jdbcType=BLOB},#{reply_photo2,jdbcType=BLOB},#{reply_photo3,jdbcType=BLOB},"
			+ "#{reply_photo_name1,jdbcType=VARCHAR},#{reply_photo_name2,jdbcType=VARCHAR},#{reply_photo_name3,jdbcType=VARCHAR},#{course_num},#{mem_num})")
	public void insertReply(CourseReplyVO courseReply);
	public void updateReply(CourseReplyVO courseReply);
	@Delete("DELETE FROM course_reply WHERE course_num=#{course_num}")
	public void deleteReplyByCourseNum(Integer course_num);
	@Delete("DELETE FROM course_reply WHERE reply_num=#{reply_num}")
	public void deleteReply(Integer reply_num);
	public void deleteReplyWithAll(Integer reply_num);
	//후기 별점평균
	@Select("SELECT ROUND(AVG(star_auth),1) AS star_avg FROM course_reply WHERE course_num = #{course_num} GROUP BY course_num")
	public Float selectStar(Integer course_num);
	@Select("SELECT COUNT(reply_num) AS star5 FROM course_reply WHERE star_auth = 5 AND course_num = #{course_num}")
	public int select5star(Integer course_num);
	@Select("SELECT COUNT(reply_num) AS starall FROM course_reply WHERE course_num = #{course_num}")
	public int selectallstar(Integer course_num);
	//후기 사진 1,2,3 삭제
	@Update("UPDATE course_reply SET reply_photo1='',reply_photo_name1='' WHERE reply_num=#{reply_num}")
	public void deleteReplyPhoto1(Integer reply_num);
	@Update("UPDATE course_reply SET reply_photo2='',reply_photo_name2='' WHERE reply_num=#{reply_num}")
	public void deleteReplyPhoto2(Integer reply_num);
	@Update("UPDATE course_reply SET reply_photo3='',reply_photo_name3='' WHERE reply_num=#{reply_num}")
	public void deleteReplyPhoto3(Integer reply_num);
	
	//후기 좋아요
	@Select("SELECT * FROM course_reply_fav WHERE fmem_num=#{mem_num} AND reply_num=#{mem_num}")
	public CourseReplyFavVO selectReplyFavCheck();
	@Select("SELECT * FROM course_reply_fav WHERE reply_num=#{reply_num} AND fmem_num=#{fmem_num}")
	public CourseReplyFavVO selectReplyFav(CourseReplyFavVO fav);
	@Select("SELECT COUNT(*) FROM course_reply_fav WHERE reply_num=#{reply_num}")
	public int selectReplyFavCount(Integer reply_num);
	@Insert("INSERT INTO course_reply_fav (fav_num,reply_num,fmem_num,course_num) VALUES (course_reply_fav_seq.nextval,#{reply_num},#{fmem_num},#{course_num})")
	public void insertReplyFav(CourseReplyFavVO fav);
	@Delete("DELETE FROM course_reply_fav WHERE fav_num=#{fav_num}")
	public void deleteReplyFav(Integer fav_num);
	@Delete("DELETE FROM course_reply_fav WHERE reply_num=#{reply_num}")
	public void deleteReplyFavByReplyNum(Integer reply_num);

}
