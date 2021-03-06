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
			<form method="POST" id="form-user-register">
				<div class="form-group">
					<h3>Your nickname:</h3>
					<input type="text" class="form-control" name="name" id="input-name"
						placeholder="Type here your nickname" pattern="\w+" required>
					<div class="invalid-feedback">You must choose a nickname
						first!</div>
				</div>
				<hr class="my-4">
				<p class="lead" id="p-lead">In order to offer you a better
					experience, we may request to use your location.</p>
				<div class="form-row">
					<div class="col">
						<a class="btn btn-primary btn-lg btn-block" href="#" role="button"
							id="btn-use-location">Use my location</a>
					</div>
					<div class="col">
						<a class="btn btn-secondary btn-lg btn-block" href="#"
							role="button" id="btn-choose-location">Choose location</a>
					</div>
				</div>
				<input type="hidden" name="location" value="" id="hidden-location">
			</form>
			<div class="row my-3">
				<div class="col">
					<hr class="bg-white">
				</div>
				<div class="col-auto">ALSO</div>
				<div class="col">
					<hr class="bg-white">
				</div>
			</div>
			<div class="row justify-content-center">
				<div class="col-12 col-sm-8 col-md-6 col-lg-4">
					<% if (request.getSession().getAttribute("Spotify-id") == null) { %>
					<a class="btn btn-spotify btn-lg btn-block" href="/auth/spotify" role="button">
						<span class="icon icon-spotify mr-2"></span>
						Log in with Spotify
					</a>
					<% } else { %>
					<a class="btn btn-spotify btn-lg btn-block disabled" href="/auth/spotify" role="button">
						<span class="icon icon-spotify mr-2"></span>
						Logged with Spotify!
					</a>
					<% } %>
				</div>
				<div class="w-100 my-1"></div>
				<div class="col-12 col-sm-8 col-md-6 col-lg-4">
					<% if (request.getSession().getAttribute("Facebook-id") == null) { %>
					<a class="btn btn-facebook btn-lg btn-block" href="/auth/facebook" role="button">
						<span class="icon icon-facebook mr-2"></span>
						Log in with Facebook
					</a>
					<% } else { %>
					<a class="btn btn-facebook btn-lg btn-block disabled" href="/auth/facebook" role="button">
						<span class="icon icon-facebook mr-2"></span>
						Logged with Facebook!
					</a>
					<% } %>
				</div>
			</div>
		</div>
	</div>

	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/3.4.1/jquery.min.js"></script>
	<script
		src="https://cdn.jsdelivr.net/npm/popper.js@1.16.0/dist/umd/popper.min.js"
		integrity="sha384-Q6E9RHvbIyZFJoft+2mJbHaEWldlvI9IOYy5n3zV9zzTtmI3UksdQRVvoxMfooAo"
		crossorigin="anonymous"></script>
	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.4.1/js/bootstrap.min.js"
		integrity="sha384-wfSDF2E50Y2D1uUdj0O3uMBJnjuUD4Ih7YwaYd1iqfktj0Uod8GCExl3Og8ifwB6"
		crossorigin="anonymous"></script>
	<script>
	var isGeolocating = false;
	
	function submitForm(action) {
		if (!$("#input-name").val()) {
			$("#input-name").addClass("is-invalid");	
		} else {
			let submit = jQuery.Event("submit");
			submit._ready = true;
			$("#form-user-register")
				.prop("action", action)
				.trigger(submit)
		}
	}
	
	function isGeolocated() {
		return $("#hidden-location").val() != "";
	}
	
	function geolocate() {
		if (isGeolocating || isGeolocated()) return;
		isGeolocating = true;
		$("#btn-use-location").html("Trying to locate...");
		navigator.geolocation.getCurrentPosition(
			(position) => {
				isGeolocating = false;
				$("#p-lead").html("We got it! now you can go ahead using your location or choosing one")
				$("#btn-use-location")
					.html("Use my location")
					.addClass("btn-success")
				$("#hidden-location").val(position.coords.latitude + "," + position.coords.longitude);
			},
			(error) => {
				isGeolocating = false;
				$("#btn-use-location").html("Use my location")
				switch (error.code) {
			    case error.PERMISSION_DENIED:
			    	$("#p-lead").html("We understand it! If you change your mind, you will probably need to ask your browser for allowing us to locate you");
			      	break;
			    case error.TIMEOUT:
			    	$("#p-lead").html("Don't worry! take your time to decide")
			      	break;
			    default:
			    	$("#p-lead").html("Sorry, but we were unable to geolocate you")
			      	break;
			  	}
			}
		);
	}
	
	$(document).ready(function() {
		$("#form-user-register").on("submit", (e) => {
			if (!e._ready) {
				//Prevents default browser behaviour when
				//submiting a cached form
				e.preventDefault();
			}
		});
		
		if (navigator.geolocation) {
			$("#btn-use-location").on("click", () => {
				if (isGeolocated()) {
					submitForm("/registerUser");
				} else {
					geolocate();
				}
			});
			
			if (navigator.permissions) {
				navigator.permissions.query({name: "geolocation"}).then((status) => {
					if (status.state == "granted") {
						geolocate();
					}
				});
			}
		} else {
			$("#p-lead").html("Oops! it seems your browser doesn't support geolocation. But you are still able to choose a location!");
			$("#btn-use-location").parent().remove();
			$("#btn-choose-location")
				.addClass("btn-primary")
				.removeClass("btn-secondary");
		}
		
		$("#btn-choose-location").on("click", () => {
			submitForm("/chooseLocation");
		});
	});
	</script>
</body>

</html>