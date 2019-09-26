# Cordova issues and examples

#### Software Architecture
Cordova hybrid app

#### Description

1. take-photo-and-pick-pictures-for-file-issue is a demo about taking photo and picking pictures for input-file issues on ios and android.

		Run "create-take-pick-vue.sh", and see the form page.

		Issue 1: In iOS 12.2-12.4 clicking on input-file control does not work, you need double click it.
		Issue 2: In android, webview does not support both of taking photo and picking pictures, but it does in mobile browsers.
		Issue 3: cordova-plugin-ionic-webview is not recommended because of some potential issues.
		So use cordova-plugin-media-capture to take photo and cordova-plugin-image-picker to pick pictures including resizing.

		NOTES: you need change the URL_PREFIX to your web server for demo uploading.
