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
	}
	
	static create() {
		return new PlaneRepository();
	}
	
	start() {
		this.socket = io.connect('https://movement.meantoplay.games/',{query: "id=" + user.uuid + "&lat=" + user.geolocation.lat + "&lng=" + user.geolocation.lng});
	    this.socket.on('movement', (data) => {
		    this.onPlanesUpdate(JSON.parse(data));
	    });
	    this.socket.on('remove', (id) => {
	    	this.onPlaneDisconnect(id);
	    });
	    this.socket.on('alreadyConnected',(id) => {
	    	statusModal.title.text("You are already connected from other device");
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
		if(uuid != user.uuid)
			this.deletePlane(uuid);
	}
	
	sendMove(geolocation) {
		if (this.socket.connected) {
			this.socket.emit('updatePos',JSON.stringify({
				lat: geolocation.lat,
				lng: geolocation.lng
			}));
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
		    riseOnHover: true
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
		return 5000;
	}
	
	start() {
		this.airports = new Map();
		this.lastTimestamp = 0;
		this.updateAirports();
	}
	
	getAirport(uuid) {
		return this.airports.get(uuid);
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
		this.title = $("#" + id + "-title");
		
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
	
	updateUserData() {
		this.title.text(user.name);
	}
	
	update() {
		this.updateVisibility();
		this.updateUserData();
		
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
		this.spotifyBtn = $("#" + id + "-spotify-btn");
		this.facebookBtn = $("#" + id + "-facebook-btn");
	}
	
	static from(id) {
		return new OverlayProfileSocial(id);
	}
	
	updateUserData() {
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
			manage: $("#" + id + "-manage")
		};
		this.createStatus = $("#" + id + "-create-status");
		this.createBtn = $("#" + id + "-create-btn");
		this.updateBtn = $("#" + id + "-manage-update-btn");
		this.playlists = $("#" + id + "-manage-playlists");
	}
	
	static from(id) {
		return new OverlayProfileAirport(id);
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
		this.createStatus.text("Wait a second! retrieving your current airport data...");
		$.getJSON("/client/airports", {query: "me"})
		.done((response) => {
			this.onUserAirportLoaded(response);
		})
		.fail((xhr, message, error) => {
			this.onUserAirportFailed(xhr, message, error);
		});
	}
	
	onCreateAirportSuccess(response) {
		this.onUserAirportLoaded(response);
	}
	
	onCreateAirportFailed(xhr, message, error) {
		user.reloadClientUserProfile();
	}
	
	onUserAirportLoaded(response) {
		airportRepository.putAirport(response[0], false);
		this.containers.create.addClass("d-none");
		this.containers.manage.removeClass("d-none");
	}
	
	onUserAirportFailed(xhr, message, error) {
		if (xhr.status == 404) {
			this.createStatus.text("You don't have an airport yet!");
			this.createBtn.removeClass("disabled");
			this.createBtn.on("click", () => {
				this.createAirport();
			})
		} else {
			this.createStatus.text("Oops! some unknown error happened, try refreshing the page");
		}
	}
	
	update() {
		this.containers.create.removeClass("d-none");
		this.containers.manage.addClass("d-none");
		this.createBtn.addClass("disabled");
		this.createBtn.off("click");
		
		if (user.spotifyId == null) {
			this.createStatus.text("Sorry! you need to link with your Spotify account first in order to create your own airport");
		} else {
			this.updateUserAirport();
		}
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
		user.disableMovement();
		user.landedOn = response.landedOn;
		user.chatToken = response.chatToken;
		overlay.overlayChat.update();
		
		const airport = airportRepository.getAirport(user.landedOn);
		user.moveTo(airport.geolocation);
		
		this.popup = L.popup({
			closeOnClick: false,
			closeButton: false,
			autoClose: false,
			closeOnEscapeKey: false
		})
		.setLatLng(airport.geolocation)
		.setContent(
			"<p>You are currently landed on <strong>" + airport.name + "</strong><p>" +
			"<button class='btn btn-danger btn-block " + this.id+ "-takeoff-btn'>Take off</button>"
		);
		map.openPopup(this.popup);
		
		$("." + this.id + "-takeoff-btn").on("click", () => {
			this.popup.setContent("<p>Taking off... this may take a moment!</p>");
			this.popup.update();
			this.takeoff();
		});
	}
	
	onLandStatusUpdateFailed(xhr, message, error) {
		user.enableMovement();
		user.landedOn = null;
		user.chatToken = null;
		overlay.overlayChat.update();
		
		if (this.popup != null) {
			map.closePopup(this.popup);
			this.popup = null;
		}
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
			
			this.popup = L.popup({
				closeOnClick: false
			})
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
		}
	}
	
	land(uuid) {
		if (user.isLanded()) {
			return;
		}
		
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
		if (!user.isLanded()) {
			return;
		}
		
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
		this.form = $("#" + id + "-form");
		this.input = $("#" + id + "-input");
		this.sendBtn = $("#" + id + "-btn");
		
		this.socket = null;
		this.lastAirport = null;
		
		this.sendBtn.on("click", () => {
			this.sendMessage();
		});
		
		this.form.on("submit", (e) => {
			e.preventDefault();
			this.sendMessage();
		})
	}
	
	static from(id) {
		return new OverlayChat(id);
	}
	
	clearMessages() {
		this.messages.html("");
	}
	
	putMessage(message) {
		this.messages.append("<li class='list-group-item'>" + message + "</li>");
	}
	
	connect(token) {
		if (this.socket != null) {
			this.disconnect();
		}
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
	}
	
	disconnect() {
		if (this.socket != null) {
			this.socket.close();
		}
		this.socket = null;
	}
	
	updateConnection() {
		if (user.isLanded()) {
			if (this.lastAirport != user.landedOn) {
				this.clearMessages();
			}
			this.connect(user.chatToken);
			this.lastAirport = user.landedOn;
		} else {
			this.putMessage("You have been disconnected from the chat.");
			this.disconnect();
		}
	}
	
	update() {
		this.updateConnection();
	}
	
	onChatJoin(message) {
		this.putMessage(message);
	}
	
	onChatMessage(message) {
		this.putMessage(message);
	}
	
	onChatDisconnected(message) {
		this.putMessage(message);
	}
	
	onChatConnectionError(message) {
		this.disconnect();
		this.putMessage("We have some trouble connecting to this channel");
		user.reloadClientUserProfile();
	}
	
	sendMessage() {
		if (this.socket != null) {
			this.socket.emit("message", this.input.val());
		}
		this.input.val("");
		overlay.tabsContent.scrollTop(overlay.tabsContent[0].scrollHeight);
	}
}

class Player {
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
		
		this.ui = {
			label: {
				playing: $("#" + id + "-label-playing")
			},
			p: {
				lyrics: $("#" + id + "-lyrics")
			},
			input: {
				search: $("#" + id + "-input-search"),
				volume: $("#" + id + "-volume")
			},
			btn: {
				play: $("#" + id + "-btn-play"),
				resume: $("#" + id + "-btn-resume"),
				pause: $("#" + id + "-btn-pause")
			}
		};
		
		this.playing = {
			title: null,
			position: null
		};
		
		this.ui.btn.play.on("click", () => {
			this.searchAndPlay(this.ui.input.search.val());
			this.loadLyrics();
		});
		
		this.ui.btn.resume.on("click", () => {
			this.resume();
		});
		
		this.ui.btn.pause.on("click", () => {
			this.pause();
		});
		
		this.ui.input.volume.on("input", () => {
			this.player.setVolume(this.ui.input.volume.val());
		});
	}
	
	onYoutubePlayerReady() {
		this.ready = true;
		this.ui.p.lyrics.text("Nothing to show!");
	}
	
	onYoutubePlayerError() {
		this.nextVideo();
	}
	
	onYoutubePlayerStateChange(e) {
		if (this.playing.title != null) {
			switch (e.data) {
			case YT.PlayerState.ENDED:
				this.ui.label.playing.text("Stopped: " + this.playing.title + "(" + this.playing.position + ")");
				this.stop();
				break;
			case YT.PlayerState.PLAYING:
				this.ui.label.playing.text("Playing: " + this.playing.title + "(" + this.playing.position + ")");
				break;
			case YT.PlayerState.PAUSED:
				this.ui.label.playing.text("Paused: " + this.playing.title + "(" + this.playing.position + ")");
				break;
			default:
				break;
			}
		}
	}
	
	isReady() {
		return this.ready;
	}
	
	searchAndPlay(title) {
		if (!this.isReady()) return -1;
		this.playing.title = title;
		this.playing.position = 1;
		this.player.loadPlaylist({
			"list": title,
			"listType": "search"
		})
		return 0;
	}
	
	stop() {
		if (!this.isReady()) return -1;
		this.player.stopVideo();
		this.playing.title = null;
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
	
	nextVideo() {
		if (!this.isReady()) return -1;
		this.player.nextVideo();
		this.playing.position++;
		return 0;
	}
	
	previousVideo() {
		if (!this.isReady()) return -1;
		this.player.previousVideo();
		this.playing.position--;
		return 0;
	}
	
	loadLyrics() {
		$.getJSON("/client/airport/track", {
			title: this.ui.input.search.val()
		}).done((data) => {
			this.onLyricsLoaded(data);
		}).fail((xhr, message, error) => {
			this.onLyricsFailed(xhr, message, error);
		})
	}
	
	onLyricsLoaded(lyrics) {
		this.ui.p.lyrics.html(lyrics.replace(/\n/g,'<br/>'));
	}
	
	onLyricsFailed() {
		this.ui.p.lyrics.text("No se pudo cargar la letra de esta canción");
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
    player = new Player("player");
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
	
	L.Util.requestAnimFrame(onMapUpdate);
	
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
	if (user != undefined) {
		const userPlane = planeRepository.getPlane(user.uuid);
		user.onUpdate();
		if (userPlane != undefined && userPlane.marker != undefined) {
			map.panTo(userPlane.marker.getLatLng());
		}
	}

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