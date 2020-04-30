class User {
	constructor(uuid, name, geolocation) {
		this.uuid = uuid;
		this.name = name;
		this.geolocation = geolocation;
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
	
	static fromData(uuid, name, geolocation) {
		return new User(uuid, name, geolocation);
	}
};

class Modal {
	constructor(id, options) {
		this.id = id;
		this.container = $("#" + id);
		this.container.modal(options);
		this.shown = (options.show == undefined) ? true : false;
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
}

var map, tile, user, statusModal;

$(document).ready(onDocumentReady);
$(window).resize(onWindowResize);

function onDocumentReady() {
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
	
	onWindowResize();
	loadUser();
}

function onWindowResize() {
	$("#map").width(window.innerWidth);
	$("#map").height(window.innerHeight);
	map.invalidateSize();
}

function loadUser() {	
	statusModal = Modal.from("modal-status", {
		show: true,
		backdrop: "static"
	});
	statusModal.title.text("Loading...");
	statusModal.body.html("<p>Some magic is happening!</p>");
	
	$.getJSON("client/getUserInfo", (response) => {
		switch (response.status) {
		case "OK":
			onUserLoaded(response.data);
			break;
		default:
			window.location.replace("/logout");
			break;
		}
	})
}

function onUserLoaded(data) {
	user = User.fromData(data.uuid, data.name, data.geolocation);
	statusModal.container.modal("hide");
	map.addLayer(user.marker);
	map.panTo(L.latLng(user.geolocation.lat, user.geolocation.lng));
}