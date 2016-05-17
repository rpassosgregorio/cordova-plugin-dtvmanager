///*
// *
// * Licensed to the Apache Software Foundation (ASF) under one
// * or more contributor license agreements.  See the NOTICE file
// * distributed with this work for additional information
// * regarding copyright ownership.  The ASF licenses this file
// * to you under the Apache License, Version 2.0 (the
// * "License"); you may not use this file except in compliance
// * with the License.  You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing,
// * software distributed under the License is distributed on an
// * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// * KIND, either express or implied.  See the License for the
// * specific language governing permissions and limitations
// * under the License.
// *
//*/

var exec = require('cordova/exec');

var DTVManager = {
	
	resultBandWidth: 0,
	
	resultExec: false,

	channelIDToPlay: "",

	dayOfWeekIndex: "",

    initDTVACPIManager: function (callback) {
       	exec(function(winParam){
       		    callback(null, winParam);
       	    }, function(error){
       			callback(error, null);
       	    }, "DTVManager", "initDTVACPIManager", []
       	);
    },

    getAllPrograms: function (callback) {
		//this.resultBandWidth = 20;
		exec(function(winParam){
				callback(null, winParam);
			}, function(error){
				callback(error, null);
			}, "DTVManager", "getAllPrograms", []
		);
    },

    scanDTVChannels: function (callback) {
    	exec(function(winParam){
    		    callback(null, winParam);
    	    }, function(error){
    			callback(error, null);
    	    }, "DTVManager", "scanDTVChannels", []
    	);
    },

    stopScanDTVChannels: function (callback) {
        exec(function(winParam){
                callback(null, winParam);
            }, function(error){
                callback(error, null);
            }, "DTVManager", "stopScanDTVChannels", []
        );
    },

    deleteAllPrograms: function (callback) {
        exec(function(winParam){
                callback(null, winParam);
            }, function(error){
                callback(error, null);
            }, "DTVManager", "deleteAllPrograms", []
        );
    },

    getNumberOfChannels: function (callback) {
        exec(function(winParam){
                callback(null, winParam);
            }, function(error){
                callback(error, null);
            }, "DTVManager", "getNumberOfChannels", []
        );
    },

    showAllChannels: function (callback) {
       exec(function(winParam){
               callback(null, winParam);
           }, function(error){
               callback(error, null);
           }, "DTVManager", "showAllChannels", []
       );
    },
	
	getAllChannelsID: function (callback) {
		alert("here");
       exec(function(winParam){
               callback(null, winParam);
           }, function(error){
               callback(error, null);
           }, "DTVManager", "getAllChannelsID", []
       );
    },

    getCurrProgram: function (callback) {
        exec(function(winParam){
                callback(null, winParam);
            }, function(error){
                callback(error, null);
            }, "DTVManager", "getCurrProgram", []
        );
    },

    getSearchedFreqList: function (callback) {
      exec(function(winParam){
              callback(null, winParam);
          }, function(error){
              callback(error, null);
          }, "DTVManager", "getSearchedFreqList", []
      );
    },

    playChannelByID: function (callback) {
      exec(function(winParam){
              callback(null, winParam);
          }, function(error){
              callback(error, null);
          }, "DTVManager", "playChannelByID", [this.channelIDToPlay]
      );
    },

    stopDTVACPIManager: function (callback) {
      exec(function(winParam){
              callback(null, winParam);
          }, function(error){
              callback(error, null);
          }, "DTVManager", "stopDTVACPIManager", []
      );
    },

    getEPGCurrNext: function (callback) {
      exec(function(winParam){
              callback(null, winParam);
          }, function(error){
              callback(error, null);
          }, "DTVManager", "getEPGCurrNext", []
      );
    },

    getEPGByDayOfTheWeekIndex: function (callback) {
      exec(function(winParam){
              callback(null, winParam);
          }, function(error){
              callback(error, null);
          }, "DTVManager", "getEPGByDayOfTheWeekIndex", [this.dayOfWeekIndex]
      );
    }
};

module.exports = DTVManager;