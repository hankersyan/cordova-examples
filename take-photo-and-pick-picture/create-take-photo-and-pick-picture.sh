mkdir projects
cd projects
cordova create takeandpick01 io.hankers.takeandpick01 TakeAndPick01
cd takeandpick01
cordova platform add android ios browser
cordova plugin add cordova-plugin-device
cordova plugin add cordova-plugin-camera
cordova plugin add cordova-plugin-file
cordova plugin add cordova-plugin-file-transfer
cordova plugin add cordova-plugin-media-capture
cordova plugin add cordova-plugin-ionic-webview
cp -f ../../config.xml ./
cp -f ../../index.html ./www/
cp -f ../../index.js ./www/js/
cordova prepare
