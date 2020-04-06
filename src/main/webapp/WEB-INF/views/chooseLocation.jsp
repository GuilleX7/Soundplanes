<!DOCTYPE html>
<html>

<head>
<title>Soundplanes</title>
<meta charset="utf-8">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, shrink-to-fit=no">
<link rel="stylesheet"
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/css/bootstrap.min.css"
	integrity="sha384-Vkoo8x4CGsO3+Hhxv8T/Q5PaXtkKtu6ug5TOeNV6gBiFeWPGFN9MuhOf23Q9Ifjh"
	crossorigin="anonymous">
<link rel="stylesheet" href="/css/global.css">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page import="java.util.List"%>
</head>

<body class="bg-dark-sp">
	<div class="background-sp fixed-bottom"></div>

	<div class="navbar">
		<p class="navbar-brand">
			<img class="d-inline-flex align-middle"
				src="/images/nav-white-logo.png" />
		</p>
	</div>
	<div class="container-sm">
		<div class="jumbotron text-center text-white bg-transparent">
			<h3>
				It seems you are visiting us from
				<%=request.getAttribute("country")%>
			</h3>
			<form action="/registerUser" method="POST">
				<%
					List<String> states = (List<String>) request.getAttribute("states");
				%>
				<h1 class="lead">Select the region where you wish to spawn:</h1>
				<div class="form-group">
					<label for="select-state">State...</label>
					<select
						class="form-control" id="select-state" name="state">
						<c:forEach items="${states}" var="state">
							<option><c:out value="${state}" /></option>
						</c:forEach>
					</select>
				</div>
				<div class="form-row">
					<div class="col">
						<a class="btn btn-secondary btn-lg btn-block" href="/registerUser"
							role="button">Go back</a>
					</div>
					<div class="col">
						<button class="btn btn-success btn-lg btn-block" type="submit">Use
							this location</button>
					</div>
				</div>
				<input type="hidden" name="name"
					value="<%=request.getParameter("name")%>">
				<input type="hidden" name="country"
					value="<%=request.getAttribute("country")%>">
			</form>
		</div>
	</div>

	<script src="https://code.jquery.com/jquery-3.4.1.slim.min.js"
		integrity="sha384-J6qa4849blE2+poT4WnyKhv5vZF5SrPo0iEjwBvKU7imGFAV0wwj1yYfoRSJoZ+n"
		crossorigin="anonymous"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
		integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
		integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
		crossorigin="anonymous"></script>
</body>

</html>