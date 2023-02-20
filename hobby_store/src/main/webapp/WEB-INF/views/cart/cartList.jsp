<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.css">
<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-3.6.0.min.js"></script>

<div style="padding: 7rem 7rem;">
	<!-- 클래스 장바구니 -->
	<h5>Course Cart [ ${courseCount} items ]</h5>
	
	<c:if test="${courseCount == 0}">
		<div>장바구니에 담은 클래스가 없습니다</div>
	</c:if>
	<c:if test="${courseCount > 0}">
		<table class="table table-borderless">
		<colgroup>
			<col style="width: 1rem">
			<col style="width: 45rem">
			<col style="width: 15rem">
		</colgroup>
		<tr>
			<th colspan="2" style="text-align:left;border-bottom: 1px solid #ccc;">Course</th>
			<th style="border-bottom: 1px solid #ccc;">Price</th>
		</tr>
		<c:forEach var="cart" items="${courseList}">
		<tr style="border-bottom: 1px solid #ccc">
			<td style="text-align:left;">
			<img src="${pageContext.request.contextPath}/images/${cart.course_photo1}"></td>
			<td style="text-align:left;">${cart.course_name}
			<br>
				<c:if test="${cart.cate_parent!=0}">
				${cart.cate_parent}/</c:if>
				${cart.cate_name}
			<br>
				${cart.course_onoff}
			</td>
			<td>${cart.course_price}</td>
		</tr>
		</c:forEach>
		<tr>
			<td colspan="2"></td>
			<td>${courseTotal}</td>
		</tr>
		<tr>
			<td colspan="3" style="text-align:right;">
			<input type="button" value="구매하기">
			</td>
		</tr>
		</table>
	</c:if>
	
	<br>
	
	<!-- 상품 장바구니 -->
	<h5>Item Cart List [ ${itemCount} items ]</h5>
	<c:if test="${itemCount == 0}">
		<div>장바구니에 담은 상품이 없습니다</div>
	</c:if>
	<c:if test="${itemCount > 0}">
		<table class="table table-borderless">
		<colgroup>
			<col style="width: 1rem">
			<col style="width: 25rem">
			<col style="width: 10rem">
			<col style="width: 10rem">
			<col style="width: 15rem">
		</colgroup>
		<tr>
			<th colspan="2" style="text-align:left;border-bottom: 1px solid #ccc;">Item</th>
			<th style="border-bottom: 1px solid #ccc;">Price</th>
			<th style="border-bottom: 1px solid #ccc;">Quantity</th>
			<th style="border-bottom: 1px solid #ccc;">TotalPrice</th>
		</tr>
		<c:forEach var="cart" items="${itemList}">
  
		<tr style="border-bottom: 1px solid #ccc;">
			
		<td style="text-align:left;">
			<img src="${pageContext.request.contextPath}/images/${cart.items_photo1}"></td>
			<td style="text-align:left;">${cart.items_name}
			<br>
				<c:if test="${cart.cate_parent!=0}">
				${cart.cate_parent}/</c:if>
				${cart.cate_name}
			</td>
			<td>${cart.items_price}</td>
			<td>
			<input class="cart_num" type="text" value="${cart.cart_num}"/>
			<input class="item_quan" type="text" value="${cart.items_quantity}"/>
						
			<input type="button" value="-" class="quan_dec" data-cartnum="${cart.cart_num}">
			<input type="number" class="quantity" value="${cart.quantity}">						
			
			<input class="quan_inc" type="button" value="증가"/>
 			</td>
			<td>${cart.items_total}</td>
		</tr>
		</c:forEach>
		<tr>
			<td colspan="4"></td>
			<td>${itemTotal}</td>
		</tr>
		<tr>
			<td colspan="5" style="text-align:right;">
			<input type="button" value="구매하기">
			</td>
		</tr>
		</table>
	</c:if>
</div>

<script>
	$(".quan_inc").click(function() {
		var quantity = $(this).parent().find('.quantity').val();
		quantity = Number(quantity) + Number(1);

		var item_quan = $(this).parent().find('.item_quan').val();

		/*  
			수량 차감할 때
			if(quantity < 1){
			alert('상품을 1개 이상 넣어주세요');
			return;
		}
		 */

		if (quantity > item_quan) {
			alert('주문 가능한 수량을 초과했습니다');
			return;
		}

		var cart_num = $(this).parent().find('.cart_num').val();

		var data = {
			quantity : quantity,
			cart_num : cart_num
		};

		$.ajax({
			url : "/updateCart",
			type : "post",
			data : data,
			success : function(result) {
				console.log("수량+1\n"+"cart_num:"+cart_num+" quantity:"+quantity);

			},
			error : function() {
				alert("수량 변경 실패");
			}
		});
	});
</script>