if ('serviceWorker' in navigator) {
	navigator.serviceWorker
		.register('sw.js')
		.then((registration) => {
			console.log('SW Registered!');
			console.log(registration);
		})
		.catch((error) => {
			console.log('SW registration failed');
		});
}

//Asking for permission with the Notification API
if (typeof Notification !== typeof undefined) {
	//First check if the API is available in the browser
	Notification.requestPermission()
		.then(function(result) {
			//If accepted, then save subscriberinfo in database
			if (result === 'granted') {
				console.log('Browser: User accepted receiving notifications, save as subscriber data!');
				navigator.serviceWorker.ready.then(function(serviceworker) {
					//When the Service Worker is ready, generate the subscription with our Serice Worker's pushManager and save it to our list
					const VAPIDPublicKey =
						'BCFsb67jUOjW6xZTFQ_b4xsUzRSPC_cLR-5oS3YN2ialEju8kVr7UIkscNQh3O0y10BUOK1Cc6I3KZOC6xH4Omo'; // Fill in your VAPID publicKey here
					const options = { applicationServerKey: VAPIDPublicKey, userVisibleOnly: true }; //Option userVisibleOnly is neccesary for Chrome
					serviceworker.pushManager.subscribe(options).then((subscription) => {
						//POST the generated subscription to our saving script (this needs to happen server-side, (client-side) JavaScript can't write files or databases)
						let subscriberFormData = new FormData();
						subscriberFormData.append('json', JSON.stringify(subscription));
						fetch('data/saveSubscription.php', { method: 'POST', body: subscriberFormData });
					});
				});
			}
		})
		.catch((error) => {
			console.log(error);
		});
}
