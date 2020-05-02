// Clases
class User {
	constructor(uuid, name, geolocation, spotifyId, facebookId) {
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
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
			input: {
				search: $("#" + id + "-input-search")
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
			if (this.searchAndPlay(this.ui.input.search.val()) == 0) {
				this.ui.label.playing.text("Now playing: " + this.playing.title);
			}
		});
		
		this.ui.btn.resume.on("click", () => {
			if (this.resume() == 0) {
				this.ui.label.playing.text("Resuming: " + this.playing.title);
			}
		});
		
		this.ui.btn.pause.on("click", () => {
			if (this.pause() == 0) {
				this.ui.label.playing.text("Paused: " + this.playing.title);
			}
		});
	}
	
	onYoutubePlayerReady() {
		this.ready = true;
	}
	
	onYoutubePlayerError(e) {
		this.nextVideo();
	}
	
	onYoutubePlayerStateChange(e) {
		if (e.data == YT.PlayerState.ENDED && this.playing.title != null) {
			this.stop();
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
		this.ui.label.playing.text("Now playing: " + this.playing.title + " (" + this.playing.position + ")");
		return 0;
	}
	
	previousVideo() {
		if (!this.isReady()) return -1;
		this.player.previousVideo();
		this.playing.position--;
		return 0;
	}
}

// Global variables
var map, tile, user, statusModal, player, overlay;

// Load functions
function loadYoutubeIframeAPI() {
	let tag = document.createElement('script');
	tag.src = "https://www.youtube.com/iframe_api";
	let firstScriptTag = document.getElementsByTagName('script')[0];
	firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);
}

function getClientUserProfile() {	
	$.getJSON("/client/user/profile", (response) => {
		switch (response.status) {
		case "OK":
			onClientUserProfileSuccess(response.data);
			break;
		default:
			window.location.replace("/logout");
			break;
		}
	})
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
		zoom: 11,
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

function onClientUserProfileSuccess(data) {
	user = User.fromData(data.uuid, data.name, data.geolocation, data.spotifyId, data.facebookId);
	map.addLayer(user.marker);
	map.panTo(L.latLng(user.geolocation.lat, user.geolocation.lng));
	overlay.title.text(user.name);
	statusModal.hide();
}