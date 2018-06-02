(ns my-exercise.search
  (:require [hiccup.page :refer [html5]]
            [clj-http.client :as client]
            [clojure.edn :as edn]))

; Constants for OCD-ID API Lookup.
(def ^:const ocdAPI "https://api.turbovote.org/elections/upcoming")
(def ^:cost districtparam "district-divisions")
(def ^:const country "ocd-division/country:us")
(def ^:const state "/state:")
(def ^:const place "/place:")

(defn header [_]
  [:head
   [:meta {:charset "UTF-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1.0, maximum-scale=1.0"}]
   [:title "Your next election results"]
   [:link {:rel "stylesheet" :href "default.css"}]])


(defrecord Address [street street-2 city state zip])

; Generate an Address record from the submitted form values.
(defn formParse [request]
  (let [{{s :street-field} :params} 
  	{{s2 :street-2-field} :params} 
  	{{c :city-field} :params} 
  	{{st :state-field} :params} 
  	{{z :zip-field} :params}]
  	-> Address. s s2 c st z))

; Query the API for the election results and return to user.
(defn query [Address]
	(let [state (concat country state (clojure.string/lower-case (:state Address)))])
	(let [place (concat state place (clojure.string/lower-case (:place Address)))])
	(client/get ocdAPI {:query-params {districtparam state, place}}))

; Format and return the response.
(defn formatResponse [response]
	(let [body (:body response)])
	[:h1 "Your upcoming elections"]
	[:p "The following elections are upcoming in your area:"]
	[:p body]

(defn results [request]
  (html5
   (header request)
   (formParse request)
   (query Address)
   (formatResponse response)))