<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="spring-form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
    <h2>hello.jsp</h2>
    <hr />
    <spring-form:form modelAttribute="helloForm" servletRelativeAction="/hello">
        <spring-form:button name="action" value="button1">Button1</spring-form:button>
    </spring-form:form>
</body>
</html>