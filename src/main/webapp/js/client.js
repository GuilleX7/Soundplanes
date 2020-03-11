const mobile = {
	dom: {
		modal: {
			changeOrientationModal: undefined
		}
	},
	
	setup: () => {
		mobile.dom.modal.changeOrientationModal = $("#changeOrientationModal");
	},
		
	check: {
		isMobile: () => {
			return window.matchMedia("(max-width: 768px)").matches;
		},
		
		isLandscape: () => {
			return window.matchMedia("(orientation: landscape)").matches;
		}
	},
}

$(document).ready(function() {
	mobile.setup();
	
	if (mobile.check.isMobile()) {
		if (!mobile.check.isLandscape()) {
			mobile.dom.modal.changeOrientationModal.modal('show');
		}
		
	    $(window).on('resize', function(event) {
	    	if (!mobile.check.isLandscape()) {
	    		mobile.dom.modal.changeOrientationModal.modal('show');
			} else {
				mobile.dom.modal.changeOrientationModal.modal('hide');
			}
	    });
	}
});