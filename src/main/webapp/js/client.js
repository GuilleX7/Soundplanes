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
    player = new YT.Player('player', {
    	height: "320",
        playerVars: {
        	autoplay: 0,
        	controls: 0,
        	disablekb: 1,
        	rel: 0,
        	showinfo: 0
        },
        events: {
            "onReady": onYoutubePlayerIsReady
        }
    });
};

function onYoutubePlayerIsReady() {
	// DEBUG
	player.loadPlaylist({
		"list": "Avicii - Levels",
		"listType": "search"
	})
}

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