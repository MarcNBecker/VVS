"use strict";

var helpers = require("../../helpers/helpers");

exports["Antarctica/Casey"] = {
	"1968" : helpers.makeTestYear("Antarctica/Casey", [
		["1968-12-31T23:59:59+00:00", "23:59:59", "zzz", 0]
	]),

	"1969" : helpers.makeTestYear("Antarctica/Casey", [
		["1969-01-01T00:00:00+00:00", "08:00:00", "AWST", -480]
	]),

	"2009" : helpers.makeTestYear("Antarctica/Casey", [
		["2009-10-17T17:59:59+00:00", "01:59:59", "AWST", -480],
		["2009-10-17T18:00:00+00:00", "05:00:00", "CAST", -660]
	]),

	"2010" : helpers.makeTestYear("Antarctica/Casey", [
		["2010-03-04T14:59:59+00:00", "01:59:59", "CAST", -660],
		["2010-03-04T15:00:00+00:00", "23:00:00", "AWST", -480]
	]),

	"2011" : helpers.makeTestYear("Antarctica/Casey", [
		["2011-10-27T17:59:59+00:00", "01:59:59", "AWST", -480],
		["2011-10-27T18:00:00+00:00", "05:00:00", "CAST", -660]
	]),

	"2012" : helpers.makeTestYear("Antarctica/Casey", [
		["2012-02-21T16:59:59+00:00", "03:59:59", "CAST", -660],
		["2012-02-21T17:00:00+00:00", "01:00:00", "AWST", -480]
	])
};