// Clases
class User {
	constructor(uuid, name, geolocation, spotifyId, facebookId) {
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
		this.moving = false;
		this.baseSpeed = 0.3;
		this.speedFactor = {
			min: 0.3,
			max: 1.2
		};
		this.spotifyId = spotifyId;
		this.facebookId = facebookId;
		this.icon = L.icon({
			iconUrl: "/images/user-icon.png",
			iconRetinaUrl: "/images/user-icon.svg",
			iconSize: [32, 32],
			iconAnchor: [16, 16]
		})
		this.marker = L.marker(L.latLng(geolocation.lat, geolocation.lng), {
		    icon: this.icon
		});
	}
	
	static fromData(uuid, name, geolocation, spotifyId, facebookId) {
		return new User(uuid, name, geolocation, spotifyId, facebookId);
	}
	
	update(delta) {
		if (this.moving) {
			var position = L.Projection.LonLat.project(this.marker.getLatLng());
			var screenCenter = map.getSize().divideBy(2);
			var angle = Math.atan2(screenCenter.y - cursor.y, cursor.x - screenCenter.x);
			var speedFactor = this.speedFactor.min + 
				Math.min(
						screenCenter.distanceTo(cursor) / Math.min(map.getSize().x, map.getSize().y),
						1
				) * (this.speedFactor.max - this.speedFactor.min);
			var displacement = L.point(
					Math.cos(angle),
					Math.sin(angle)
				).multiplyBy(this.baseSpeed * speedFactor * delta / 1000);
			this.marker.setLatLng(L.Projection.LonLat.unproject(position.add(displacement)));
			this.marker.setRotationAngle(90 - angle * 180 / Math.PI);
		}
	}
	
	onUpdate(delta) {
		this.update(delta);
	}
	
	onMouseDown(e) {
		this.moving = true;
	}
	
	onMouseUp(e) {
		this.moving = false;
	}
};

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
		this.toggler = $("#" + id + "-toggler");
		this.toggler.on("click", () => {
			this.toggle();
		});
		this.title = $("#" + id + "-title");
		this.update();
	}
	
	static from(id, hidden) {
		return new Overlay(id, hidden);
	}
	
	update() {
		if (this.hidden) {
			this.container.addClass("overlay-hidden");
		} else {
			this.container.removeClass("overlay-hidden");
		}
	}
	
	toggle() {
		this.hidden = !this.hidden;
		this.update();
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
var map, tile, user, statusModal, player, overlay, cursor, lastUpdate = null;

// Load functions
function loadYoutubeIframeAPI() {
	let tag = document.createElement('script');
	tag.src = "https://www.youtube.com/iframe_api";
	let firstScriptTag = document.getElementsByTagName('script')[0];
	firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}

function getClientUserProfile() {	
	$.getJSON("/client/user/profile")
		.done((response) => {
			onClientUserProfileLoaded(response);
		})
		.fail((xhr, message, error) => {
			onClientUserProfileFailed(xhr, message, error);
		});
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
		// Keyboard Navigation options
		keyboard: false,
		// Touch Interaction options
		touchZoom: false,
		// Mousewheel options
		scrollWheelZoom: false
	})
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
	
	// Request client user data
	getClientUserProfile();
}

function onWindowResize() {
	$("#map").width(window.innerWidth);
	$("#map").height(window.innerHeight);
	map.invalidateSize();
}

function onClientUserProfileLoaded(data) {
	user = User.fromData(data.uuid, data.name, data.geolocation, data.spotifyId, data.facebookId);
	map.addLayer(user.marker);
	map.panTo(L.latLng(user.geolocation.lat, user.geolocation.lng));
	overlay.title.text(user.name);
	statusModal.hide();
}

function onClientUserProfileFailed() {
	window.location.replace("/logout");
}

function onMapUpdate() {
	if (lastUpdate != null) {
		var delta = Date.now() - lastUpdate;
		lastUpdate = Date.now();
		
		if (user != undefined) {
			user.onUpdate(delta);
			map.panTo(user.marker.getLatLng());
		}
	} else {
		lastUpdate = Date.now();
	}
	
	L.Util.requestAnimFrame(onMapUpdate);
}

function onMapMouseDown(e) {
	cursor = e.containerPoint;
	if (user != undefined) {
		user.onMouseDown(e);
	}
}

function onMapMouseUp(e) {
	cursor = e.containerPoint;
	if (user != undefined) {
		user.onMouseUp(e);
	}
}

function onMapMouseMove(e) {
	cursor = e.containerPoint;
}