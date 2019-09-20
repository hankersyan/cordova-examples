mkdir projects
cd projects
cordova create mediacapture01 io.hankers.mediacapture01 MediaCapture01
cd mediacapture01
cordova platform add android ios browser
cordova plugin add cordova-plugin-device
cordova plugin add cordova-plugin-camera
cordova plugin add cordova-plugin-file
cordova plugin add cordova-plugin-media-capture
cp -f ../../config.xml ./
cp -f ../../index.html ./www/
cp -f ../../index.js ./www/js/
cordova prepare
