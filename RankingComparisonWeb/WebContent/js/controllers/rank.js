/**
 * Controller for main page
 */
var myApp = angular.module("MyApp", [ "ngAnimate" ]);
// myApp.controller(MainController);

function MainController($scope, $http) {
	"use strict";

	$scope.tfidf = {
		results : {},
		loader : false,
		showresults : false,
		seemorelink : false,
		moreResults : false,
		accuracy : 0
	};
	$scope.pagerank = {
		results : [],
		loader : false,
		showresults : false,
		accuracy: 0
	};
	$scope.hybrid = {
		results : [],
		loader : false,
		showresults : false,
		accuracy: 0
	};
	$scope.lucene = {
		results : [],
		loader : false,
		showresults : false
	};
	$scope.searchResultCount = 50;

	$scope.submitSearchForm = function() {
		$scope.tfidf.results = {};
		$scope.tfidf.seemorelink = false;
		$scope.pagerank.seemorelink = false;
		$scope.tfidf.timeTaken = "";
		$scope.lucene.results = [];

		console.log($scope.searchResultCount);

		if ($scope.queryStr) {
			if ($scope.tfidf.showresults || $scope.pagerank.showresults || $scope.hybrid.showresults) {
				
				$scope.lucene.loader = true;
				$scope.tfidf.accuracy = 0;
				if ($scope.tfidf.showresults) {
					$scope.tfidf.loader = true;
				}
				if ($scope.pagerank.showresults) {
					$scope.pagerank.loader = true;
				}
				var normalizeQuery = !!$scope.normalize;
				var queryURL = 'rest/query/' + $scope.queryStr + "?resultCount=" + $scope.searchResultCount + "&normalize=" + normalizeQuery + "&tfidf=" + $scope.tfidf.showresults + "&pagerank=" + $scope.pagerank.showresults + "&hybrid=" + $scope.hybrid.showresults;
				$http.get(queryURL).success(function(data) {

					$scope.lucene.loader = false;
					
					// TF-IDF data
					var tfidfData = data['results']['tfidf'];
					if ($scope.tfidf.showresults) {
						$scope.tfidf.timeTaken = tfidfData['time'];
						$scope.tfidf.loader = false;
						$scope.tfidf.seemorelink = true;
						$scope.tfidf.accuracy = tfidfData['accuracy'];

						var results = tfidfData['result'];//angular.fromJson(data)['results'];
						var max = Math.max.apply(Math, results.map(function(o) {
							return o.score;
						}));
						results.forEach(function(res) {
							res.score = (res.score / max) * 100;
							res.progressBarClass = "progress-bar-success";
							if (res.score < 100) {
								res.progressBarClass = "progress-bar-warning";
							}
							if (res.score < 95) {
								res.progressBarClass = "progress-bar-danger";
							}
							// res.rowClass = (res.matching) ? " bg-success" : "";
						});
						$scope.tfidf.results.list1 = results.slice(0, 10);
						$scope.tfidf.results.list2 = results.slice(10);
					}
					
					// Pagerank
					var pagerankData = data['results']['pagerank'];
					if ($scope.pagerank.showresults) {
						$scope.pagerank.timeTaken = pagerankData['time'];
						$scope.pagerank.loader = false;
						$scope.pagerank.seemorelink = true;
						$scope.pagerank.accuracy = pagerankData['accuracy'];

						var results = pagerankData['result'];//angular.fromJson(data)['results'];
						var max = Math.max.apply(Math, results.map(function(o) {
							return o.score;
						}));
						results.forEach(function(res) {
							res.score = (res.score / max) * 100;
							res.progressBarClass = "progress-bar-success";
							if (res.score < 100) {
								res.progressBarClass = "progress-bar-warning";
							}
							if (res.score < 95) {
								res.progressBarClass = "progress-bar-danger";
							}
							// res.rowClass = (res.matching) ? " bg-success" : "";
						});
						$scope.pagerank.results.list1 = results.slice(0, 10);
						$scope.pagerank.results.list2 = results.slice(10);
					}

					// Show lucene results
					$scope.lucene.results = data['luceneResults'];
					$('.analysis-chart-modal').modal('show');
					createAnalysisGraph($scope.queryStr, [$scope.tfidf.accuracy, $scope.pagerank.accuracy, $scope.hybrid.accuracy]);

				}).error(function(error) {
					$scope.tfidf.loader = false;
					$scope.lucene.loader = false;
					$scope.pagerank.loader = false;
					$scope.hybrid.loader = false;
				});
			}
		}

	};

	$scope.clearSearchForm = function() {
		$scope.queryStr = "";
		$scope.tfidf.results = {};
		$scope.tfidf.showresults = false;
		$scope.pagerank.showresults = false;
		$scope.hybrid.showresults = false;
		$scope.tfidf.loader = false;
		$scope.tfidf.accuracy = 0;
		
		$scope.pagerank.loader = false;
		$scope.hybrid.loader = false;

		$scope.lucene.results = [];
		$scope.searchResultCount = 50;
	};

	$scope.showLuceneButtonText = "Show Lucene Results";
	$scope.showHideLuceneResults = function() {
		$scope.lucene.showresults = !$scope.lucene.showresults;
		$scope.showLuceneButtonText = ($scope.lucene.showresults) ? "Hide Lucene Results"
				: "Show Lucene Results";
	};

	function createAnalysisGraph(qstr, accuracyArr) {
		$('#graphcontainer').highcharts(
				{
					chart : {
						type : 'bar'
					},
					title : {
						text : 'Comparison study of Ranking Algorithms'
					},
					subtitle : {
						text : 'By - Team 11 (Brij, Neel & Prachish)'
					},
					xAxis : {
						categories : [ 'Lucene', 'TF-IDF', 'PageRank', 'Hybrid' ],
						title : {
							text : null
						}
					},
					yAxis : {
						min : 0,
						title : {
							text : 'Accuracy (%age)',
							align : 'high'
						},
						labels : {
							overflow : 'justify'
						},
						max: 100
					},
					tooltip : {
						valueSuffix : ' %'
					},
					plotOptions : {
						bar : {
							dataLabels : {
								enabled : true
							}
						}
					},
					legend : {
						layout : 'vertical',
						align : 'right',
						verticalAlign : 'top',
						x : -40,
						y : 103,
						floating : true,
						borderWidth : 1,
						backgroundColor : '#FFFFFF',
						shadow : true
					},
					credits : {
						enabled : false
					},
					series : [ {
						name : 'Query: ' + qstr,
						data : [ 100 ].concat(accuracyArr)
					}]
				});
	}
}