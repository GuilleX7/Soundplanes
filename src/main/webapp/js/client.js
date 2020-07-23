// Clases
var Icons = {
	plane: L.icon({
		iconUrl: "/images/user-icon.png",
		iconRetinaUrl: "/images/user-icon.svg",
		iconSize: [32, 32],
		iconAnchor: [16, 16]
	}),
	airport: L.icon({
		iconUrl: "/images/airport-icon.png",
		iconRetinaUrl: "/images/airport-icon.svg",
		iconSize: [32, 32],
		iconAnchor: [16, 16]
	}),
};

class User {
	constructor(uuid, name, geolocation, spotifyId, facebookId) {
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
		this.moving = false;
		this.canMove = false;
		this.spotifyId = spotifyId;
		this.facebookId = facebookId;
		this.landedOn = null;
		this.chatToken = null;
	}
	
	static fromData(uuid, name, geolocation, spotifyId, facebookId) {
		return new User(uuid, name, geolocation, spotifyId, facebookId);
	}
	
	static loadClientUserProfile() {	
		$.getJSON("/client/user/profile")
		.done((response) => {
			onClientUserProfileLoaded(response);
		})
		.fail((xhr, message, error) => {
			onClientUserProfileFailed(xhr, message, error);
		});
	}
	
	static reloadClientUserProfile() {
		$.getJSON("/client/user/profile")
		.done((response) => {
			onClientUserProfileReloaded(response);
		})
		.fail((xhr, message, error) => {
			onClientUserProfileFailed(xhr, message, error);
		});
	}
	
	isLanded() {
		return this.landedOn != null;
	}
	
	disableMovement() {
		this.canMove = false;
	}
	
	enableMovement() {
		this.canMove = true;
	}
	
	moveTo(geolocation) {
		planeRepository.sendMove(geolocation);
	}
	
	update() {
        if (this.moving && this.canMove) {
        	this.moveTo(cursor);
        }
	}
	
	onUpdate() {
		this.update();
	}
	
	onMouseDown(e) {
		if (e.sourceTarget._popup == null) {
			this.moving = true;
		}
	}
	
	onMouseUp(e) {
		this.moving = false;
	}
};

class Plane {
	constructor(uuid, geolocation){
		this.uuid = uuid;
		this.marker = L.marker(geolocation, {
		    icon: Icons.plane
		});
		this.marker.setRotationAngle(45);
	}
	
	static from(uuid, geolocation){
		return new Plane(uuid, geolocation);
	}
	
	getPos() {
		return this.marker.getLatLng();
	}
	
	setPos(geolocation) {
		this.marker.setLatLng(geolocation);
	}
}
class PlaneRepository {
	constructor() {
		this.planes = {};
		this.socket = null;
		this.pendingMovement = null;
	}
	
	static create() {
		return new PlaneRepository();
	}
	
	start() {
		this.socket = io.connect('https://movement.meantoplay.games/',{query: "id=" + user.uuid + "&lat=" + user.geolocation.lat + "&lng=" + user.geolocation.lng});
	    this.socket.on("connect", () => {
	    	if (this.pendingMovement != null) {
	    		this.sendMove(this.pendingMovement);
	    	}
	    })
		this.socket.on('movement', (data) => {
		    this.onPlanesUpdate(JSON.parse(data));
	    });
	    this.socket.on('remove', (id) => {
	    	this.onPlaneDisconnect(id);
	    });
	    this.socket.on('alreadyConnected', (id) => {
	    	statusModal.title.text("You are already connected from another device");
	    	statusModal.body.html("");
	    	statusModal.show();
	    });
	}
	
	addPlane(plane) {
		plane.marker.addTo(map);
		this.planes[plane.uuid] = plane;
	}
	
	getPlane(uuid) {
		return this.planes[uuid];
	}
	
	deletePlane(uuid){
		if (this.planes[uuid] != undefined) {
			map.removeLayer(this.planes[uuid].marker);
		}
		delete this.planes[uuid];
	}
	
	planesUpdate(positions) {
		for (let uuid in positions){
			const geolocation = positions[uuid];
			let plane = this.planes[uuid];
			if (plane) {
				plane.setPos(geolocation);
			} else {
				plane = Plane.from(uuid, geolocation);
				this.addPlane(plane);
			}	
			plane.marker.setRotationAngle(geolocation.angle);
		}
	}
	
	onPlanesUpdate(positions) {
		this.planesUpdate(positions);
	}
	
	onPlaneDisconnect(uuid) {
		if (uuid != user.uuid) {
			this.deletePlane(uuid);
		}
	}
	
	sendMove(geolocation) {
		if (this.socket.connected) {
			this.socket.emit('updatePos', JSON.stringify({
				lat: geolocation.lat,
				lng: geolocation.lng
			}));
		} else {
			this.pendingMovement = geolocation;
		}
	}
}

class Airport {
	constructor(uuid, name, geolocation, creationTimestamp) {
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
		this.creationTimestamp = creationTimestamp;
		this.marker = L.marker(geolocation, {
		    icon: Icons.airport,
		    riseOnHover: true,
		    riseOffset: 1001,
		    zIndexOffset: 1000
		});
		this.marker.uuid = uuid;
		
		this.marker.on("click", (e) => {
			overlay.overlayAirports.onAirportMarkerClick(e.target.uuid);
		})
	}
	
	static fromData(uuid, name, geolocation, creationTimestamp) {
		return new Airport(uuid, name, geolocation, creationTimestamp);
	}
}

class AirportRepository {
	constructor() {
		this.airports = null;
		this.lastTimestamp = null;
		this.timer = null;
	}
	
	static create() {
		return new AirportRepository();
	}
	
	static getUpdateInterval() {
		return 10000;
	}
	
	start() {
		this.airports = new Map();
		this.lastTimestamp = 0;
		this.updateAirports();
	}
	
	getAirport(uuid) {
		return this.airports.get(uuid);
	}
	
	getNearAirports(airport, distance) {
		let nearAirports = []
		this.airports.forEach((value, key, map) => {
			if (value.marker.getLatLng().distanceTo(airport.marker.getLatLng()) <= distance) {
				nearAirports.push(value);
			}
		});
		return nearAirports;
	}
	
	deleteAirport(uuid) {
		const airport = this.getAirport(uuid);
		if (airport != undefined) {
			map.removeLayer(airport.marker);
			this.airports.delete(airport.uuid);
		}
	}
	
	updateAirports() {
		if (this.timer != null) {
			clearTimeout(this.timer);
			this.timer = null;
		}
		$.getJSON("/client/airports", {query: "all", lastTimestamp: this.lastTimestamp})
		.done((response) => {
			this.onAirportsLoaded(response);
		})
		.fail((xhr, message, error) => {
			this.onAirportsFailed(xhr, message, error);
		})
		.always(() => {
			this.timer = setTimeout(() => {
				this.updateAirports()
			}, AirportRepository.getUpdateInterval());
		});
	}
	
	onAirportsLoaded(response) {
		this.putAirports(response);
	}
	
	onAirportsFailed(xhr, message, error) {
		console.log("WARNING: Couldn't fetch airports!: " + error + " / Retrying in " + AirportRepository.getUpdateInterval() + "ms");
	}
	
	putAirport(airport, update = true) {
		if (this.airports.get(airport.uuid) != undefined) {
			return;
		}
		
		var airportEntity = Airport.fromData(airport.uuid, airport.name, airport.geolocation, airport.creationTimestamp);
		this.airports.set(airportEntity.uuid, airportEntity);
		map.addLayer(airportEntity.marker);
		
		if (update && this.lastTimestamp < airportEntity.creationTimestamp) {
			this.lastTimestamp = airportEntity.creationTimestamp;
		}
	}
	
	putAirports(airports, update = true) {
		for (var i = 0; i < airports.length; i++) {
			this.putAirport(airports[i], update);
		}
	}
}

class Modal {
	constructor(id, options) {
		this.id = id;
		this.container = $("#" + id);
		this.container.modal(options);
		this.shown = options.show;
		this.container.on("show.bs.modal", () => {
			this.shown = true;
		});
		this.container.on("hide.bs.modal", () => {
			this.shown = false;
		});
		this.container.on("shown.bs.modal", () => {
			if (!this.shown) {
				this.container.modal("hide");
			}
		});
		this.container.on("hidden.bs.modal", () => {
			if (this.shown) {
				this.container.modal("show");
			}
		});
		this.title = this.container.find(".modal-title");
		this.body = this.container.find(".modal-body");
	}
	
	static from(id, options={}) {
		return new Modal(id, options);
	}
	
	show() {
		this.shown = true;
		this.container.modal("show");
	}
	
	hide() {
		this.shown = false;
		this.container.modal("hide");
	}
	
	toggle() {
		this.shown = !this.shown;
		this.container.modal("toggle");
	}
}

class Overlay {
	constructor(id, hidden) {
		this.id = id;
		this.hidden = hidden;
		this.container = $("#" + id);
		this.tabsContent = $("#" + id + "-tabs-content");
		this.toggler = $("#" + id + "-toggler");
		this.toggler.on("click", () => {
			this.toggle();
		});
		
		this.overlayProfileSocial = OverlayProfileSocial.from("profile-social");
		this.overlayProfileAirport = OverlayProfileAirport.from("profile-airport");
		this.overlayAirports = OverlayAirports.from("airports");
		this.overlayChat = OverlayChat.from("chat");
		
		this.updateVisibility();
	}
	
	static from(id, hidden) {
		return new Overlay(id, hidden);
	}
	
	updateVisibility() {
		if (this.hidden) {
			this.container.addClass("overlay-hidden");
		} else {
			this.container.removeClass("overlay-hidden");
		}
	}

	update() {
		this.updateVisibility();
		
		this.overlayProfileSocial.update();
		this.overlayProfileAirport.update();
		this.overlayAirports.update();
	}
	
	toggle() {
		this.hidden = !this.hidden;
		this.updateVisibility();
	}
	
	show() {
		this.hidden = false;
		this.updateVisibility();
	}
	
	hide() {
		this.hidden = true;
		this.updateVisibility();
	}
}

class OverlayProfileSocial {
	constructor(id) {
		this.id = id;
		this.socialTitle = $("#" + id + "-title");
		this.spotifyBtn = $("#" + id + "-spotify-btn");
		this.facebookBtn = $("#" + id + "-facebook-btn");
	}
	
	static from(id) {
		return new OverlayProfileSocial(id);
	}
	
	updateUserData() {
		this.socialTitle.text(user.name);
		
		if (user.spotifyId == null) {
			this.spotifyBtn.removeClass("disabled");
			this.spotifyBtn.html("Log in with Spotify");
		} else {
			this.spotifyBtn.addClass("disabled");
			this.spotifyBtn.html("Logged in with Spotify!");
		}
		
		if (user.facebookId == null) {
			this.facebookBtn.removeClass("disabled");
			this.facebookBtn.html("Log in with Facebook");
		} else {
			this.facebookBtn.addClass("disabled");
			this.facebookBtn.html("Logged in with Facebook!");
		}
	}
	
	update() {
		this.updateUserData();
	}
}

class OverlayProfileAirport {
	constructor(id) {
		this.id = id;
		this.containers = {
			create: $("#" + id + "-create"),
			manage: $("#" + id + "-manage"),
			playlists: $("#" + id + "-playlists")
		};
		this.createStatus = $("#" + id + "-create-status");
		this.createBtn = $("#" + id + "-create-btn");
		this.manageStatus = $("#" + id + "-manage-status");
		this.managePlaylistName = $("#" + id + "-manage-playlist-name");
		this.managePlaylistImage = $("#" + id + "-manage-playlist-image");
		this.manageTravelBtn = $("#" + id + "-manage-travel-btn");
		this.playlistsStatus = $("#" + id + "-playlists-status");
		this.updatePlaylistsBtn = $("#" + id + "-playlists-update-btn");
		this.playlists = $("#" + id + "-playlists-list");
	}
	
	static from(id) {
		return new OverlayProfileAirport(id);
	}
	
	onCreateAirportSuccess(response) {
		this.onUserAirportLoaded(response);
	}
	
	onCreateAirportFailed(xhr, message, error) {
		User.reloadClientUserProfile();
	}
	
	onUserAirportLoaded(response) {
		const airport = response[0];
		
		airportRepository.putAirport(airport, false);
		this.containers.create.addClass("d-none");
		this.containers.manage.removeClass("d-none");
		this.containers.playlists.removeClass("d-none");
		
		if (!airport.playlistLoaded) {
			this.showCurrentPlaylist("(NO PLAYLIST)", []);
		} else {
			this.showCurrentPlaylist(airport.playlistInfo.name, airport.playlistInfo.images);
		}
		
		this.manageTravelBtn.removeClass("disabled");
		this.manageTravelBtn.on("click", () => {
			this.manageTravel();
		});
		
		this.playlistsStatus.text("You haven't retrieved your Spotify playlists yet!");
		this.playlists.html("<li class='list-group-item'>No playlists found!</li>");
		this.updatePlaylistsBtn.removeClass("disabled");
		this.updatePlaylistsBtn.on("click", () => {
			this.updatePlaylists();
		})
	}
	
	onUserAirportFailed(xhr, message, error) {
		if (xhr.status == 404) {
			this.containers.create.removeClass("d-none");
			this.createStatus.text("You don't have an airport yet!");
			this.createBtn.removeClass("disabled");
			this.createBtn.on("click", () => {
				this.createAirport();
			})
		} else {
			User.reloadClientUserProfile();
		}
	}
	
	onPlaylistsUpdateSuccess(response) {
		this.clearPlaylists();
		this.putPlaylists(response);
		this.playlistsStatus.text("Pick a playlist to load on your airport!");
	}
	
	onPlaylistsUpdateFailed(xhr, message, error) {
		if (xhr.status == 303) {
			this.playlistsStatus.text("Sorry, but we need to refresh your Spotify token. You will be redirected in 5 seconds.");
			setTimeout(() => {
				window.location.replace(xhr.responseJSON);
			}, 5000);
		} else {
			this.playlistsStatus.text("We couldn't retrieve your playlists");
		}
	}
	
	onLoadAirportPlaylistSuccess(response) {
		this.showCurrentPlaylist(response.name, response.images);
	}
	
	onLoadAirportPlaylistFailed(xhr, message, error) {
		if (xhr.status == 303) {
			this.manageStatus.text("Sorry, but we need to refresh your Spotify token. You will be redirected in 5 seconds.");
			setTimeout(() => {
				window.location.replace(xhr.responseJSON);
			}, 5000);
		} else if (xhr.status == 404) {
			airportRepository.deleteAirport(xhr.responseJSON);
			User.reloadClientUserProfile();
		} else {
			this.manageStatus("Some unknown error happened during the loading");
		}
	}
	
	manageTravel() {
		if (user != undefined && !user.isLanded()) {
			const airport = airportRepository.getAirport(user.uuid);
			if (airport != undefined) {
				user.moveTo(airport.marker.getLatLng());
				overlay.hide();
			}
		}
	}
	
	showCurrentPlaylist(name, images) {
		let imageUrl = "/images/unknown.png";
		if (images != null && images.length > 0) {
			imageUrl = images[0].url;
		}
		this.manageStatus.text("Currently loaded on your airport:");
		this.managePlaylistName.text(name);
		this.managePlaylistImage.attr("src", imageUrl);
	}
	
	updatePlaylists() {
		this.playlistsStatus.text("Retrieving your playlists...");
		
		$.getJSON("/client/user/playlist")
		.done((response) => {
			this.onPlaylistsUpdateSuccess(response);
		})
		.fail((xhr, message, error) => {
			this.onPlaylistsUpdateFailed(xhr, message, error);
		})
	}
	
	putPlaylists(playlists) {
		if (playlists.length == 0) {
			this.playlists.html("<li class='list-group-item'>No playlists found!</li>");
		}
		
		for (let i = 0; i < playlists.length; i++) {
			this.putPlaylist(playlists[i]);
		}
		
		$(".user-playlist").on("click", (e) => {
			this.loadAirportPlaylist($(e.target).data("id"));
		});
	}
	
	putPlaylist(playlist) {
		let imageUrl = "/images/unknown.png";
		if (playlist.images != null && playlist.images.length > 0) {
			imageUrl = playlist.images[0].url;
		}
		this.playlists.append("<li class='list-group-item list-group-item-action playlist user-playlist'>" +
				"<div class='row no-gutters d-flex flew-nowrap text-break' data-id='" + playlist.id + "'><div class='col d-flex flex-column justify-content-center flex-shrink-0 flex-grow-0' data-id='" + playlist.id + "'><img src='" + imageUrl + "' class='card-img' data-id='" + playlist.id + "'></div><div class='col d-flex flex-column justify-content-center' data-id='" + playlist.id + "'><div class='card-body' data-id='" + playlist.id + "'><p class='card-text' data-id='" + playlist.id + "'>" + playlist.name +  "</p></div></div></div>"
				+ "</li>");
	}
	
	clearPlaylists() {
		this.playlists.html("");
	}
	
	loadAirportPlaylist(id) {
		this.playlists.html("<li class='list-group-item'>No playlists found!</li>");
		this.playlistsStatus.text("You haven't retrieved your Spotify playlists yet!");
		
		this.manageStatus.text("Loading your playlist...");
		
		$.ajax("/client/airport/playlist", {
			method: "PUT",
			dataType: "json",
			data: {
				playlistId: id
			}
		})
		.done((response) => {
			this.onLoadAirportPlaylistSuccess(response);
		})
		.fail((xhr, message, error) => {
			this.onLoadAirportPlaylistFailed(xhr, message, error);
		});
	}
	
	createAirport() {
		this.createStatus.text("Creating your airport...");
		$.ajax("/client/airports", {
			method: "PUT",
			dataType: "json"
		})
		.done((response) => {
			this.onCreateAirportSuccess(response);
		})
		.fail((xhr, message, error) => {
			this.onCreateAirportFailed(xhr, message, error);
		});
	}
	
	updateUserAirport() {
		this.containers.create.addClass("d-none");
		this.containers.manage.addClass("d-none");
		this.containers.playlists.addClass("d-none");
		
		this.createBtn.addClass("disabled");
		this.createBtn.off("click");
		this.manageTravelBtn.addClass("disabled");
		this.manageTravelBtn.off("click");
		this.updatePlaylistsBtn.addClass("disabled");
		this.updatePlaylistsBtn.off("click");
		
		if (user.spotifyId == null) {
			this.containers.create.removeClass("d-none");
			this.createStatus.text("Sorry! you need to link with your Spotify account first in order to create your own airport");
		} else {
			$.getJSON("/client/airports", {query: "me"})
			.done((response) => {
				this.onUserAirportLoaded(response);
			})
			.fail((xhr, message, error) => {
				this.onUserAirportFailed(xhr, message, error);
			});
		}
	}
	
	update() {
		this.updateUserAirport();
	}
}

class OverlayAirports {
	constructor(id) {
		this.id = id;
		this.popup = null;
	}
	
	static from(id) {
		return new OverlayAirports(id);
	}
	
	updateLandStatus() {
		$.getJSON("/client/airport/landing")
		.done((response) => {
			this.onLandStatusUpdateSuccess(response);
		})
		.fail((xhr, message, error) => {
			this.onLandStatusUpdateFailed(xhr, message, error);
		});
	}
	
	onLandStatusUpdateSuccess(response) {
		const tokenExpiration = JSON.parse(atob(response.user.chatToken.split(".")[1])).exp;
		if (tokenExpiration <= Date.now() / 1000) {
			return this.land(response.user.landedOn);
		}
		
		user.disableMovement();
		user.landedOn = response.user.landedOn;
		user.chatToken = response.user.chatToken;
		overlay.overlayChat.update();
		overlay.show();
		if (player != null && player.isReady()) {
			player.update();
		}
		
		airportRepository.putAirport(response.airport);
		user.moveTo(response.airport.geolocation);
		
		if (this.popup != null) {
			map.closePopup(this.popup);
			this.popup = null;
		}
		
		planeRepository.getPlane(user.uuid).marker.closePopup().unbindPopup();
		
		let popup = L.popup({
			closeOnClick: false,
			closeButton: false,
			autoClose: false,
			closeOnEscapeKey: false
		})
		.setContent(
			"<p>You are currently landed on <strong>" + response.airport.name + "</strong><p>" +
			"<button class='btn btn-danger btn-block " + this.id+ "-takeoff-btn'>Take off</button>"
		);
		planeRepository.getPlane(user.uuid).marker.bindPopup(popup).openPopup();
		
		$("." + this.id + "-takeoff-btn").on("click", () => {
			const userMarker = planeRepository.getPlane(user.uuid).marker;
			userMarker.getPopup().setContent("<p>Taking off... this may take a moment!</p>");
			userMarker.getPopup().update();
			this.takeoff();
		});
	}
	
	onLandStatusUpdateFailed(xhr, message, error) {
		user.enableMovement();
		user.landedOn = null;
		user.chatToken = null;
		overlay.overlayChat.update();
		
		if (player != null && player.isReady()) {
			player.update();
		}
		
		if (this.popup != null) {
			map.closePopup(this.popup);
			this.popup = null;
		}
		
		planeRepository.getPlane(user.uuid).marker.closePopup().unbindPopup();
	}
	
	onLandingSuccess(response) {
		this.onLandStatusUpdateSuccess(response);
	}
	
	onLandingFailed(xhr, message, error) {
		if (xhr.status == 404) {
			airportRepository.deleteAirport(xhr.responseJSON);
		}
		
		User.reloadClientUserProfile();
	}
	
	onTakeoffSuccess(response) {
		this.onLandStatusUpdateFailed(response);
	}
	
	onTakeoffFailed(xhr, message, error) {
		if (xhr.status == 404) {
			airportRepository.deleteAirport(xhr.responseJSON);
		}
		
		User.reloadClientUserProfile();
	}
	
	onAirportMarkerClick(uuid) {
		if (!user.isLanded()) {
			const airport = airportRepository.getAirport(uuid);
			
			let nearAirports = airportRepository.getNearAirports(airport, 50);
			if (nearAirports.length == 1) {
				this.popup = L.popup()
				.setLatLng(airport.geolocation)
				.setContent(
					"<p><strong>" + airport.name + "</strong><p>" +
					"<button class='btn btn-success btn-block " + this.id+ "-land-btn'>Land here</button>"
				);
				map.openPopup(this.popup);
				
				$("." + this.id + "-land-btn").on("click", () => {
					this.popup.setContent("<p>Landing... this may take a moment!</p>");
					this.popup.update();
					this.land(airport.uuid);
				});
			} else {
				let content = "<p><strong>Select airport:</strong><p><ul class='list-group'>";
				for (let i = 0; i < nearAirports.length; i++) {
					const nearAirport = nearAirports[i];
					content += "<li class='list-group-item list-group-item-action selectable-airport' data-uuid='" + nearAirport.uuid + "'>" + nearAirport.name + "</li>";
				}
				content += "</ul>";
				
				this.popup = L.popup({
					maxHeight: 300
				})
				.setLatLng(airport.geolocation)
				.setContent(content);
				map.openPopup(this.popup);
				
				$(".selectable-airport").on("click", (e) => {
					this.popup.setContent("<p>Landing... this may take a moment!</p>");
					this.popup.update();
					this.land($(e.target).data("uuid"));
				});
			}
		}
	}
	
	land(uuid) {
		$.ajax("/client/airport/landing", {
			method: "PUT",
			dataType: "json",
			data: {
				airportUUID: uuid
			}
		})
		.done((response) => {
			this.onLandingSuccess(response);
		})
		.fail((xhr, message, error) => {
			this.onLandingFailed(xhr, message, error);
		});
	}
	
	takeoff() {
		$.ajax("/client/airport/landing", {
			method: "DELETE",
			dataType: "json"
		})
		.done((response) => {
			this.onTakeoffSuccess(response);
		})
		.fail((xhr, message, error) => {
			this.onTakeoffFailed(xhr, message, error);
		});
	}
	
	update() {
		this.updateLandStatus();
	}
}

class OverlayChat {
	constructor(id) {
		this.id = id;
		this.messages = $("#" + id + "-messages");
		this.users = $("#" + id + "-users");
		this.form = $("#" + id + "-form");
		this.input = $("#" + id + "-input");
		this.sendBtn = $("#" + id + "-send-btn");
		this.usersBtn = $("#" + id + "-users-btn");
		this.tab = $("#" + id + "-tab");
		this.socket = null;
		this.channelId = null;
		this.lastAirport = null;
		
		this.sendBtn.on("click", () => {
			this.sendMessage();
		});
		
		this.usersBtn.on("click", () => {
			this.users.toggleClass("d-none");
			this.messages.toggleClass("d-none");
			this.usersBtn.toggleClass("btn-success");
			this.usersBtn.toggleClass("btn-secondary");
		})
		
		this.form.on("keydown", (e) => {
			if (event.which == 13 || event.keyCode == 13) {
				e.preventDefault();
				this.sendMessage();
        		return false;
   			}
		});
		
		this.tab.on("shown.bs.tab", () => {
			overlay.tabsContent.scrollTop(overlay.tabsContent[0].scrollHeight);
		});
	}
	
	static from(id) {
		return new OverlayChat(id);
	}
	
	clearMessages() {
		this.messages.html("");
	}
	
	putMessage(messageText, style) {
		let htmlMessage = $("<li></li>", {
			"class": style == undefined ? "list-group-item" : "list-group-item list-group-item-" + style,
			"text": messageText
		})
		this.messages.append(htmlMessage);
		overlay.tabsContent.scrollTop(overlay.tabsContent[0].scrollHeight + 75);
	}
	
	connect(token) {
		if (this.socket != null) {
			this.disconnect();
		}
		
		this.token = token;
		this.channelId = JSON.parse(atob(this.token.split(".")[1])).channel;
		this.socket = io.connect('https://chat.meantoplay.games/chat', {query: "token=Bearer " + token});
		this.socket.on("join", (message) => {
			this.onChatJoin(message);
		});
		this.socket.on("message", (message) => {
			this.onChatMessage(message);
		});
		this.socket.on("disconnected",(message) => {
			this.onChatDisconnected(message);
		});
		this.socket.on("connect_error", (error) => {
			this.onChatConnectionError(error);
		});
		this.socket.on("invalidate", (error) => {
			this.onChatConnectionInvalidate(error);
		});
	}
	
	disconnect() {
		if (this.socket != null) {
			this.socket.close();
		}
		this.socket = null;
		this.token = null;
		this.channelId = null;	
	}
	
	emptyUsers() {
		this.users.html("<li class='list-group-item list-group-item-dark text-center'>Current landed users (0)</li>" + 
		"<li class='list-group-item list-group-item-light'>Nothing here!</li>");
	}
	
	retrieveUsers() {
		if (this.socket == null || !this.socket.connected || !user.isLanded()) {
			this.emptyUsers();
			return;
		}
		
		$.ajax({
			url: "https://chat.meantoplay.games/api/v1/channel/" + this.channelId,
			method: "GET",
			headers: {
				"Authorization": "Bearer " + this.token	
			},
			dataType: "json"
		})
		.done((response) => {
			this.onUsersRetrievalSuccess(response);
		})
	}
	
	updateUsers(users) {
		this.users.html("<li class='list-group-item list-group-item-dark text-center'>Current landed users (" + users.length + ")</li>");
		if (users.length == 0) {
			this.users.append("<li class='list-group-item list-group-item-light'>Nothing to see here!</li>");
		} else {
			for (let i = 0; i < users.length; i++) {
				let listItem = $("<li></li>", {
					"class": "list-group-item",
					"text": users[i].name
				});
				this.users.append(listItem);
			}
		}
	}
	
	onUsersRetrievalSuccess(response) {
		if (response.error != null) {
			return;
		}
		
		this.updateUsers(response.success.users);
	}
	
	updateConnection() {
		if (user.isLanded()) {
			if (this.lastAirport != user.landedOn) {
				this.clearMessages();
			}
			this.connect(user.chatToken);
			this.lastAirport = user.landedOn;
		} else {
			if (this.lastAirport == null) {
				this.putMessage("Land on an airport to start chating with other people!", "primary");
			} else {
				this.putMessage("You have been disconnected from the chat.", "secondary");
				this.disconnect();
			}
		}
		this.retrieveUsers();
	}
	
	update() {
		this.updateConnection();
	}
	
	onChatJoin(message) {
		let formattedMessage = message.userId == user.uuid ?
		"You have entered the chat!" :
		message.userName + " has entered the chat!";
		
		this.putMessage(formattedMessage, "info");
		
		this.retrieveUsers();
	}
	
	onChatMessage(message) {
		let formattedMessage = message.userId == user.uuid ?
		"You say: " + message.data :
		message.userName + " says: " + message.data;
		
		this.putMessage(formattedMessage);
	}
	
	onChatDisconnected(message) {
		let formattedMessage = message.userId == user.uuid ?
		"You have left the chat!" :
		message.userName + " has left the chat!";
		
		this.putMessage(formattedMessage, "secondary");
		
		this.retrieveUsers();
	}
	
	onChatConnectionError(message) {
		this.disconnect();
		this.putMessage("We have some trouble connecting to the chat, probably because we are carrying out some maintenance!", "danger");
		user.reloadClientUserProfile();
	}
	
	onChatConnectionInvalidate(message) {
		this.disconnect();
		this.putMessage("Your session has expired, please wait a second!", "warning");
		user.reloadClientUserProfile();
	}
	
	sendMessage() {
		if (this.socket != null && this.socket.connected) {
			this.socket.emit("message", this.input.val());
		} else {
			this.putMessage("You need to be landed on an airport first!", "danger");
		}
		this.input.val("");
	}
}

class OverlayPlayer {
	constructor(id) {
		this.id = id;
		this.player = new YT.Player(id, {
	    	height: "320",
	        playerVars: {
	        	autoplay: 0,
	        	controls: 0,
	        	disablekb: 1,
	        	rel: 0,
	        	showinfo: 0
	        },
	        events: {
	            onReady: () => {
	            	this.onYoutubePlayerReady();
	            },
	            onError: (e) => {
	            	this.onYoutubePlayerError(e);
	            },
	            onStateChange: (e) => {
	            	this.onYoutubePlayerStateChange(e);
	            }
	        }
	    });
		
		this.ready = false;
		
		this.trackName = $("#" + id + "-track-name");
		this.trackArtist = $("#" + id + "-track-artist");
		this.trackImage = $("#" + id + "-track-image");
		this.playBtn = $("#" + id + "-play-btn");
		this.pauseBtn = $("#" + id + "-pause-btn");
		this.skipBtn = $("#" + id + "-skip-btn");
		this.volume = $("#" + id + "-volume");
		this.exportBtn = $("#" + id + "-export-btn");
		this.lyrics = $("#" + id + "-lyrics");
		
		this.playBtn.on("click", () => {
			this.resume();
		});
		this.pauseBtn.on("click", () => {
			this.pause();
		});
		this.skipBtn.on("click", () => {
			this.update();
		});
		this.volume.on("input", () => {
			this.setVolume(this.volume.val());
		});
	}
	
	onYoutubePlayerReady() {
		this.ready = true;
		this.update();
	}
	
	onYoutubePlayerError() {
		this.nextVideo();
	}
	
	onYoutubePlayerStateChange(e) {
		switch (e.data) {
		case YT.PlayerState.ENDED:
			this.onYoutubePlayerStop(e);
			break;
		case YT.PlayerState.PLAYING:
			this.onYoutubePlayerPlay(e);
			break;
		case YT.PlayerState.PAUSED:
			this.onYoutubePlayerPause(e);
			break;
		default:
			break;
		}
	}
	
	onYoutubePlayerStop(e) {
		this.update();
	}
	
	onYoutubePlayerPlay(e) {
		this.setVolume(this.volume.val())
	}
	
	onYoutubePlayerPause(e) {
		this.pause();
	}
	
	onPlayerTrackLoaded(data, message, xhr) {
		if (xhr.status == 204 || !user.isLanded()) {
			this.emptyTrack();
			return;
		}
		
		const artists = data.track.artists.map((artist) => {
			return artist.name;
		}).join(" & ");
		const fullName = data.track.name + " - " + artists;
	
		if (user.spotifyId != null) {
			this.exportBtn.removeClass("disabled");
			this.exportBtn.on("click", () => {
				this.exportPlaylist();
			})
		} else {
			this.exportBtn.text("Log in with Spotify to export playlists!");
		}
		
		this.trackName.text(data.track.name);
		this.trackArtist.text(artists);
		let imageUrl = "/images/unknown.png";
		if (data.track.album.images.length > 0) {
			imageUrl = data.track.album.images[0].url;
		}
		this.trackImage.attr("src", imageUrl);
		
		if (data.lyrics != null) {
			if (data.lyrics.substr(0, 4) == "null") {
				this.lyrics.html(data.lyrics.substr(4).replace(/<(a|em|i|b)( ([^>]+)?)?>/g, "").replace(/<\/(a|em|i|b)>/, ""));
			} else {
				this.lyrics.html(data.lyrics.replace(/\n/g,'<br/>'));
			}
		} else {
			this.lyrics.html("(No lyrics)")
		}
		
		this.searchAndPlay(fullName);
	}
	
	onPlayerTrackFailed(xhr, message, error) {
		this.pause();
		this.emptyTrack();
		if (xhr.status == 404) {
			airportRepository.deleteAirport(xhr.responseJSON);
		}
	}
	
	onExportPlaylistSuccess(response, message, xhr) {
		if (xhr.status == 201) {
			this.exportBtn.text("Playlist successfully exported!");
		} else {
			this.exportBtn.text("Playlist seems to be empty...");
		}
	}
	
	onExportPlaylistFailed(xhr, message, response) {
		if (xhr.status == 303) {
			this.exportBtn.text("Sorry, but we need to refresh your Spotify token. You will be redirected in 5 seconds.");
			setTimeout(() => {
				window.location.replace(xhr.responseJSON);
			}, 5000);
		} else if (xhr.status == 404) {
			airportRepository.deleteAirport(xhr.responseJSON);
			User.reloadClientUserProfile();
		} else {
			this.exportBtn.text("Woops! we weren't able to export this playlist");
		}
	}
	
	isReady() {
		return this.ready;
	}
	
	emptyTrack() {
		this.trackName.text("(NO TRACK)");
		this.trackArtist.text("");
		this.trackImage.attr("src", "/images/unknown.png");
		this.lyrics.html("(No lyrics)");
	}
	
	searchAndPlay(fullName) {
		if (!this.isReady()) return -1;
		this.player.loadPlaylist({
			"list": fullName,
			"listType": "search"
		})
		return 0;
	}
	
	stop() {
		if (!this.isReady()) return -1;
		this.player.stopVideo();
		this.player.clearVideo();
		this.emptyTrack();
		this.track = null;
		return 0;
	}
	
	pause() {
		if (!this.isReady()) return -1;
		this.player.pauseVideo();
		return 0;
	}
	
	resume() {
		if (!this.isReady()) return -1;
		if (this.player.getPlayerState() == YT.PlayerState.PAUSED) {
			this.player.playVideo();
			return 0;
		}
		return 1;
	}
	
	setVolume(volume) {
		if (!this.isReady()) return -1;
		this.player.setVolume(volume);
		return 0;
	}
	
	nextVideo() {
		if (!this.isReady()) return -1;
		this.player.nextVideo();
		return 0;
	}
	
	previousVideo() {
		if (!this.isReady()) return -1;
		this.player.previousVideo();
		return 0;
	}
	
	fetchTrack() {
		this.stop();
		this.emptyTrack();
		this.trackName.text("(FETCHING TRACK...)");
		this.exportBtn.text("Export this playlist to my Spotify account");
		this.exportBtn.addClass("disabled");
		this.exportBtn.off("click");
		
		$.getJSON("/client/airport/playlist")
		.done((response, message, xhr) => {
			this.onPlayerTrackLoaded(response, message, xhr);
		})
		.fail((xhr, message, error) => {
			this.onPlayerTrackFailed(xhr, message, error);
		});
	}
	
	exportPlaylist() {
		this.exportBtn.text("Exporting...");
		
		$.ajax("/client/user/playlist", {
			method: "PUT",
			dataType: "json"
		})
		.done((response, message, xhr) => {
			this.onExportPlaylistSuccess(response, message, xhr);
		})
		.fail((xhr, message, error) => {
			this.onExportPlaylistFailed(xhr, message, error);
		});
	}
	
	update() {
		this.fetchTrack();
	}
}

// Global variables
var map, tile, user, statusModal, player, overlay, cursor, 
	airportRepository, planeRepository, lastUpdate = null;

// Load functions
function loadYoutubeIframeAPI() {
	let tag = document.createElement('script');
	tag.src = "https://www.youtube.com/iframe_api";
	let firstScriptTag = document.getElementsByTagName('script')[0];
	firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}

// Listeners
$(document).ready(onDocumentReady);
$(window).resize(onWindowResize);

// Events
window.onYouTubeIframeAPIReady = () => {
    player = new OverlayPlayer("player");
};

function onDocumentReady() {
	// Load youtube iframe API
	loadYoutubeIframeAPI();

	// Load overlay
	overlay = Overlay.from("overlay", true);
	
	// Load Leaflet map
	map = L.map("map", {
		// Control options
		zoomControl: false,
		// Interaction options
		boxZoom: false,
		doubleClickZoom: false,
		dragging: false,
		// Map State options
		center: L.latLng(0.0, 0.0),
		zoom: 10,
		minZoom: 5,
		maxZoom: 12,
		worldCopyJump: true,
		// Keyboard Navigation options
		keyboard: false,
		// Touch Interaction options
		touchZoom: false,
		// Mousewheel options
		scrollWheelZoom: false
	})
	L.control.zoom({position: "bottomright"}).addTo(map);
	tile = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
		attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
	})
	tile.addTo(map);
	map.getContainer().addEventListener("touchstart", function (e) {
		simulatedEvent = {
			containerPoint: L.point(e.touches[0].clientX, e.touches[0].clientY)
		};
		onMapMouseDown(simulatedEvent);
	}, false);
	map.getContainer().addEventListener("touchend", function (e) {
		simulatedEvent = {
			containerPoint: L.point(e.touches[0].clientX, e.touches[0].clientY)
		};
		onMapMouseUp(simulatedEvent);
	}, false);
	map.getContainer().addEventListener("touchmove", function (e) {
		simulatedEvent = {
			containerPoint: L.point(e.touches[0].clientX, e.touches[0].clientY)
		};
		onMapMouseMove(simulatedEvent);
	}, false);
	map.on("mousedown", onMapMouseDown);
	map.on("mouseup", onMapMouseUp);
	map.on("mousemove", onMapMouseMove);
	
	// Load status modal
	statusModal = Modal.from("modal-status", {
		show: true,
		backdrop: "static"
	});
	statusModal.title.text("Loading...");
	statusModal.body.html("<p>Some magic is happening!</p>");
	
	// Dispatch initial resize event
	onWindowResize();
	
	// Create airport repository and start updating
	airportRepository = AirportRepository.create();
	airportRepository.start();
	
	// Create planes repository
	planeRepository = PlaneRepository.create();
	
	// Request client user data
	User.loadClientUserProfile();
}

function onWindowResize() {
	$("#map").width(window.innerWidth);
	$("#map").height(window.innerHeight);
	map.invalidateSize();
}

function onClientUserProfileLoaded(data) {
	user = User.fromData(data.uuid, data.name, data.geolocation, data.spotifyId, data.facebookId);
	map.panTo(L.latLng(user.geolocation.lat, user.geolocation.lng));
	planeRepository.start();
	planeRepository.addPlane(Plane.from(user.uuid, user.geolocation));
	overlay.update();
	statusModal.hide();
	L.Util.requestAnimFrame(onMapUpdate);
}

function onClientUserProfileReloaded(data) {
	user = User.fromData(data.uuid, data.name, data.geolocation, data.spotifyId, data.facebookId);
	overlay.update();
	statusModal.hide();
}

function onClientUserProfileFailed() {
	window.location.replace("/logout");
}

function onMapUpdate() {
	user.onUpdate();
	map.panTo(planeRepository.getPlane(user.uuid).marker.getLatLng());

	L.Util.requestAnimFrame(onMapUpdate);
}

function onMapMouseDown(e) {
	cursor = e.latlng;
	if (user != undefined) {
		user.onMouseDown(e);
	}
}

function onMapMouseUp(e) {
	cursor = e.latlng;
	if (user != undefined) {
		user.onMouseUp(e);
	}
}

function onMapMouseMove(e) {
	cursor = e.latlng;
}