$(function(){
	//페이지와 관련된 정보를 가지는 변수
    let pageSize = 5;//화면에 보여줄 레코드 수
    let pageBlock = 3;//페이지 표시 단위
    let currentPage;//현재 보고 있는 화면
    let totalItem;//총 레코드 수

	
	//댓글 목록
	function selectList(pageNum){
		currentPage = pageNum;
		
		//로딩 이미지 노출
		$('#loading').show();
		
		$.ajax({
			url:'listReply.do',
			type:'post',
			data:{pageNum:pageNum,space_num:$('#space_num').val()},
			dataType:'json',
			success:function(param){
				//로딩 이미지 감추기
				$('#loading').hide();
				
				//호출시 해당 ID의 div의 내부 내용물 제거
				$('#output').empty();
				$('.paging-btn').empty();
				
				//댓글 목록 작업
				$(param.list).each(function(index,item){
					let output = '<div class="wid"><span class="r-list-star">';
					for(let i=1;i<=5;i++){
						output += '<i class="fa-regular fa-star"></i>';
					}
					output += '</span>';
					output += '<span class="r-list-fav"><i class="fa-regular fa-thumbs-up"></i> 100</span></div>';
					output += '<div class="item">';
					output += '<ul class="detail-info">';
					output += '<li>';
					output += '<img width="50" height="50" class="my-photo">';
					//output += '<img src="../member/viewProfile.do?mem_num='+item.mem_num+'" width="50" height="50" class="my-photo">';
					output += '</li>';
					output += '<li>';
					if(item.mem_nickname){
						output += item.mem_nickname + '<br>';
					}else{
						output += item.mem_id + '<br>';
					}
					if(item.reply_mdate){
						output += '<span class="modify-date">' + item.reply_mdate + '(수정일)</span>';
					}else{
						output += '<span class="modify-date">' + item.reply_date + '</span>';
					}
					output += '</li></ul>';
					output += '<div class="sub-item">';
					output += '<p>' + item.reply_content.replace(/\r\n/g,'<br>') + '</p>';
					if(param.user_num == item.mem_num){
						output += '<div class="reply-btn">'
						output += ' <input type="button" data-num="'+item.reply_num+'" value="수정" class="modify-btn">';
						output += ' <input type="button" data-num="'+item.reply_num+'" value="삭제" class="delete-btn">';
						output += '</div>'
						output += '</div>'
					}
					output += '</div>';
					output += '<hr size="1" noshade style="width:70%;margin:16px auto;color:gray;">'
					
					//문서 객체에 추가
					$('#output').append(output);
				});	
					
				//댓글 페이징 처리	
				totalItem = param.count;
                if(totalItem == 0){
                   return;
                }
                
                var totalPage = Math.ceil(totalItem/pageSize);
                
                if(currentPage == undefined || currentPage == ''){
                   currentPage = 1;
                }
                //현재 페이지가 전체 페이지 수보다 크면 전체 페이지수로 설정
                if(currentPage > totalPage){
                   currentPage = totalPage;
                }
                
                //시작 페이지와 마지막 페이지 값을 구하기
                var startPage = Math.floor((currentPage-1)/pageBlock)*pageBlock + 1;
                var endPage = startPage + pageBlock - 1;
                
                //마지막 페이지가 전체 페이지 수보다 크면 전체 페이지 수로 설정
                if(endPage > totalPage){
                   endPage = totalPage;
                }
               
                var add='';
                if(startPage>pageBlock){
                   add += '<li data-page='+(startPage-1)+'>[이전]</li>';
                }
                
                for(var i=startPage;i<=endPage;i++){
					if(currentPage==i){
	                   add += '<li data-page='+i+' style="color:#FF4E02;"><b>'+i+'</b></li>';
					}else{
	                    add += '<li data-page='+i+'>'+i+'</li>';
					}
                }
                if(endPage < totalPage){
                   add += '<li data-page='+(startPage+pageBlock)+'>[다음]</li>';;
                }
                //ul 태그에 생성한 li를 추가
                $('.paging-btn').append(add);
			
					
			},
			errror:function(){
				//로딩 이미지 감추기
				$('#loading').hide();
				Swal.fire({
                    icon: 'error',
                    title:'네트워크 오류 발생!',
                    showCancelButton: false,
                    confirmButtonText: "확인",
                    confirmButtonColor: "#FF4E02"
                });
			}
		});
		
	}

	
	//후기 등록
	$('#reply_form').submit(function(event){
		//기본 이벤트 제거
		event.preventDefault();
		
		if($('#reply_content').val().trim()==''){
			Swal.fire({
                    icon: 'warning',
                    title:'내용을 입력하세요!',
                    showCancelButton: false,
                    confirmButtonText: "확인",
                    confirmButtonColor: "#FF4E02"
                });
			$('#reply_content').val('').focus();
			return false;
		}
		
		let form_data = $(this).serialize();
		$.ajax({
			url:'writeReply.do',
			type:'post',
			data:form_data,
			dataType:'json',
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 작성할 수 있습니다.');
				}else if(param.result == 'success'){
					//폼 초기화
					initForm();
					//등록된 데이터가 표시될 수 있도록 
					//목록 갱신
					selectList(1);
				}
			},
			error:function(){
				Swal.fire({
                    icon: 'error',
                    title:'네트워크 오류 발생',
                    showCancelButton: false,
                    confirmButtonText: "확인",
                    confirmButtonColor: "#FF4E02"
                });
			}
		});
		
		
	});
	
	//후기 작성 폼 초기화
	function initForm(){
		$('textarea').val('');
		$('#reply_form .letter-count').text('300/300');
	}
	
	
	
	//textarea에 내용 입력시 글자수 체크
	$(document).on('keyup','textarea',function(){
		//입력한 글자수 구하기
		let inputLength = $(this).val().length;
		
		if(inputLength>300){//300자를 넘어선 경우
			$(this).val($(this).val().substring(0,300));
		}else{//300자 이하인 경우
			//남은 글자수 구하기
			let remain = 300 - inputLength;
			remain += ' / 300';
			if($(this).attr('id') == 'reply_content'){
				//등록 폼 글자수
				$('#reply_form .letter-count').text(remain);
			}else{
				//수정 폼 글자수
				$('#mreply_form .letter-count').text(remain);
			}
		}
		
	});
	
	//댓글 수정
	//댓글 수정 버튼 클릭시 수정폼 노출
	$(document).on('click','.modify-btn',function(){
		//댓글 글번호 
		let reply_num = $(this).attr('data-num');
		//댓글 내용
		let content = $(this).parents('.item').find('p').html().replace(/<br>/g,'\r\n');
		
		//댓글수정 폼 UI
		let modifyUI = '<form id="mreply_form">';
		modifyUI += '<input type="hidden" name="reply_num" id="mreply_num" value="'+reply_num+'">';
		modifyUI += '<span class="letter-count mletter-count">300 / 300</span>';
		modifyUI += '<textarea rows="3" cols="50" name="reply_content" id="mreply_content" class="reply-content">'+content+'</textarea>';
		modifyUI += '<div id="mre_second" class="align-right">';
		modifyUI += '<div class="reply-photo">';
		modifyUI += '<ul class="image">';
		for(let i=1;i<=3;i++){
			modifyUI += '<li>';
			modifyUI += '<img class="space-photo'+i+'">';
			modifyUI += '<label for="upload'+i+'" class="label1 l'+i+'">';
			modifyUI += '<i class="fa-solid fa-circle-plus"></i><br>';
			modifyUI += '</label>';
			modifyUI += '<i class="fa-solid fa-circle-xmark d'+i+'"></i>';
			modifyUI += '<input type="file" name="upload'+i+'" id="upload'+i+'" style="display:none;" accept="image/jpeg,image/png,image/gif">';
			modifyUI += '</li>';
		}
		modifyUI += '<input type="submit" value="수정">';
		modifyUI += ' <input type="button" value="취소" class="reply-reset">';
		modifyUI += '</div>';
		modifyUI += '</form>';
		
		//이전에 이미 수정하는 댓글이 있을 경우 수정버튼을 클릭하면 숨김
		//sub-item을 환원시키고 수정 폼을 초기화함
		initModifyForm();
		//지금 클릭해서 수정하고자 하는 데이터는 감추기
		//수정버튼을 감싸고 있는 div
		$(this).parents('.sub-item').hide();
		
		//수정폼을 수정하고자 하는 데이터가 있는 div에 노출
		$(this).parents('.item').append(modifyUI);
		
		//입력한 글자수 셋팅
		let inputLength = $('#mreply_content').val().length;
		let remain = 300 - inputLength;
		remain += '/300';
		
		//문서 객체에 반영
		$('#mreply_form .letter-count').text(remain);		
	});
	
	//수정폼에서 취소 버튼 클릭시 수정폼 초기화
	$(document).on('click','.reply-reset',function(){
		initModifyForm();
	});
	
	//댓글 수정 폼 초기화
	function initModifyForm(){
		$('.sub-item').show();
		$('#mreply_form').remove();
	}
	
	//댓글 수정 처리
	$(document).on('submit','#mre_form',function(event){
		//기본 이벤트 제거
		event.preventDefault();
		
		if($('#mre_content').val().trim()==''){
			alert('내용을 입력하세요!');
			$('#mre_content').val('').focus();
			return false;
		}
		
		//폼에 입력한 데이터 반환
		let form_data = $(this).serialize();
		
		//서버와 통신
		$.ajax({
			url:'updateReply.do',
			type:'post',
			data:form_data,
			dataType:'json',
			success:function(param){
				if(param.result=='logout'){
					alert('로그인해야 수정할 수 있습니다.');
				}else if(param.result == 'success'){
					//수정 데이터 표시
					$('#mre_form').parent().find('p')
					   .html($('#mre_content').val()
		                   .replace(/</g,'&lt;')
                           .replace(/>/g,'&gt;')
                           .replace(/\r\n/g,'<br>')
                           .replace(/\r/g,'<br>')
                           .replace(/\n/g,'<br>'));
					//최근 수정일 표시
					$('#mre_form').parent().find('.modify-date')
					                       .text('최근 수정일 : 5초미만');
					//수정 폼 초기화
					initModifyForm();
				}else if(param.result == 'wrongAccess'){
					alert('타인의 글은 수정할 수 없습니다.');
				}else{
					alert('댓글 수정시 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
		
	});
	
	//댓글 삭제
	$(document).on('click','.delete-btn',function(){
		//댓글 번호
		let re_num = $(this).attr('data-num');
		
		//서버와 통신
		$.ajax({
			url:'deleteReply.do',
			type:'post',
			data:{re_num:re_num},
			dataTpye:'json',
			success:function(param){
				if(param.result == 'logout'){
					alert('로그인해야 삭제할 수 있습니다.');
				}else if(param.result == 'success'){
					alert('삭제 완료!');
					selectList(1);
				}else if(param.result == 'wrongAccess'){
					alert('타인의 글을 삭제할 수 없습니다.');
				}else{
					alert('댓글 삭제시 오류 발생');
				}
			},
			error:function(){
				alert('네트워크 오류 발생');
			}
		});
	});
	
	
	
	//후기 좋아요 클릭시
	$(document).on('click','.r-list-fav',function(){
		alert('aa');
		$(this).find('.fa-thumbs-up').css('color','#FF4E02');
		$(this).find('.fa-thumbs-up').css('transform','scale(1.1)');
	});
	
	
	
	//페이지 버튼 이벤트 연결
	$(document).on('click','.paging-btn li',function(){
	   //페이지 번호를 읽어들임
	   currentPage = $(this).attr('data-page');

	   //클릭한 번호 저장(색강조)
	   

	   //목록 호출
	   selectList(currentPage);
	});
	
	
	//초기 데이터(목록) 호출
	selectList(1);
	
});



