# Cordova issues and examples

#### Software Architecture
Cordova hybrid app

#### Description

1. file-click-issue-ios about input-file control click issue on ios
2. take-photo-and-pick-picture is a demo about taking photos and picking pictures on ios and android.

		The input-file control does not support both of taking pictures and picking photos in webview, but it does in mobile browsers.

		Use ionic-webview plugin for input-file control click issue in ios webview, 
		 
		Use media-capture plugin to take photos in android webview.

		NOTES: you need change the URL_PREFIX to your web server for uploading files in index.js
