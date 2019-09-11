mkdir projects
cd projects
cordova create simplefile01 io.hankers.simplefile01 SimpleFile01
cd simplefile01
cordova platform add android ios browser
cp -f ../../index.html ./www/
cp -f ../../index.js ./www/js/
cordova prepare
