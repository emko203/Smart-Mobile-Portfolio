self.addEventListener('install', (e) => {
	e.waitUntil(
		caches.open('static').then((cache) => {
			return cache.addAll([ './', './src/master.css', './images/logo192.png' ]);
		})
	);
	console.log('Install');
});

self.addEventListener('fetch', (e) => {
	e.respondWith(
		caches.match(e.request.url).then((response) => {
			return response || fetch(e.request);
		})
	);
	console.log(`Intercepting fetch request for: ${e.request.url}`);
});

self.addEventListener('push', (e) => {
	if (e.data) {
		pushdata = JSON.parse(e.data.text());
		console.log('Service Worker: I received this:', pushdata);
		if (pushdata['title'] != '' && pushdata['message'] != '') {
			const options = { body: pushdata['message'] };
			self.registration.showNotification(pushdata['title'], options);
			console.log('Service Worker: I made a notification for the user');
		} else {
			console.log("Service Worker: I didn't make a notification for the user, not all the info was there :(");
		}
	}
});

self.addEventListener('notificationclick', function(clicking) {
	const pageToOpen = '/';
	const promiseChain = clients.openWindow(pageToOpen);
	event.waitUntil(promiseChain);
});
