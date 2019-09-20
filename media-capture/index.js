/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
var app = {
    // Application Constructor
    initialize: function() {
        document.addEventListener('deviceready', this.onDeviceReady.bind(this), false);
    },

    // deviceready Event Handler
    //
    // Bind any cordova events here. Common events are:
    // 'pause', 'resume', etc.
    onDeviceReady: function() {
        this.receivedEvent('deviceready');
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
        
        this.takePhoto();
    },
    
    takePhoto: function() {
			// capture callback
			var captureSuccess = function(mediaFiles) {
			    var i, path, len;
			    var parentElement = document.getElementById('deviceready');
			    for (i = 0, len = mediaFiles.length; i < len; i += 1) {
			    		console.log(mediaFiles[i]);
			        path = mediaFiles[i].fullPath;
			        // do something interesting with the file
			        var img = document.createElement("IMG");
			        img.src = mediaFiles[i].localURL;
			        parentElement.appendChild(img);
			    }
          console.log(parentElement.innerHTML);
			};
			
			// capture error callback
			var captureError = function(error) {
			    console.log('Error code: ' + error.code, null, 'Capture Error');
			};
			
			// start image capture
			navigator.device.capture.captureImage(captureSuccess, captureError, {limit:1});
    }
};

app.initialize();