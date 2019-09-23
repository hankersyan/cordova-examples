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
const URL_PREFIX = "http://YOUR/WEB/SERVER/";

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
        
        document.getElementById('take_picture').addEventListener('click', this.takePicture.bind(this), false);
        document.getElementById('pick_photo').addEventListener('change', this.pickPhoto.bind(this), false);
    },

    // Update DOM on a Received Event
    receivedEvent: function(id) {
        var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');

        console.log('Received Event: ' + id);
    },
    
    takePicture: function() {
    	const self = this;
			// capture callback
			var captureSuccess = function(mediaFiles) {
			    var i, path, len;
			    var parentElement = document.getElementById('deviceready');
			    for (i = 0, len = mediaFiles.length; i < len; i += 1) {
			    		console.log(mediaFiles[i]);
			        path = mediaFiles[i].fullPath;
			        // do something interesting with the file
			        self.uploadFile(mediaFiles[i].localURL);
			    }
          console.log(parentElement.innerHTML);
			};
			
			// capture error callback
			var captureError = function(error) {
			    console.log('Error code: ' + error.code, null, 'Capture Error');
			};
			
			// start image capture
			navigator.device.capture.captureImage(captureSuccess, captureError, {limit:1});
    },
    
    pickPhoto: function(e) {
        const self = this;

        var fileName = e.target.files[0].name;
    		console.log('did pickPhoto, ' + fileName);

        var formData = new FormData();
        formData.append("thefile", e.target.files[0]);

        const url = URL_PREFIX + "/saveUpload.aspx";

        const xhr = new XMLHttpRequest();
        xhr.onreadystatechange = (e) => {
          console.log(xhr.responseText);
          if (xhr.readyState === XMLHttpRequest.DONE) {
				    self.appendImg(URL_PREFIX + '/temp/' + fileName);
          }
        }
        xhr.open("POST", url);

        xhr.send(formData);
    },
    
    appendImg: function(imgUrl) {
			// do something interesting with the file
			var img = document.createElement("IMG");
			img.style.width = "100px";
			img.src = imgUrl;
			document.getElementById('deviceready').appendChild(img);
    },

    uploadFile: function(fileURL) {
			const self = this;
			var win = function (r) {
			    console.log("Code = " + r.responseCode);
			    console.log("Response = " + r.response);
			    console.log("Sent = " + r.bytesSent);
			    var filename = fileURL.substring(fileURL.lastIndexOf('/') + 1);
			    console.log(filename);
			    self.appendImg(URL_PREFIX + '/temp/' + filename);
			}
			
			var fail = function (error) {
			    alert("An error has occurred: Code = " + error.code);
			    console.log("upload error source " + error.source);
			    console.log("upload error target " + error.target);
			}
			
			var options = new FileUploadOptions();
			options.fileKey = "file";
			options.mimeType = "text/plain";
    	options.fileName = fileURL.substr(fileURL.lastIndexOf('/') + 1);

			var params = {};
			params.value1 = "test";
			params.value2 = "param";
			
			options.params = params;
			
			var ft = new FileTransfer();
			ft.upload(fileURL, encodeURI(URL_PREFIX + "/saveUpload.aspx"), win, fail, options);
    }
};

app.initialize();