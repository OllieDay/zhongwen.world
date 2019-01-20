(ns zhongwen.core
  (:require [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.route :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :refer [resource-response]]
            [zhongwen.dictionary :as dictionary])
  (:gen-class))

(def entries (dictionary/parse "/app/cedict_ts.u8"))
(def limit 100)

; Response should not contain nested :base and :norm so flatten map.
(defn create-response [entries]
  (map #(assoc (:base %) :tones (:tones %)) entries))

(defn search [query]
  {:status  200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body    (->> entries
                 (dictionary/search-all query)
                 (take limit)
                 create-response
                 json/write-str)})

(defroutes app
  (resources "/")
  (GET "/" [] (resource-response "/index.html" {:root "public"}))
  (GET "/api/search/:query" [query] (search query))
  (not-found (resource-response "/404.html" {:root "public"})))

(defn -main [& args]
  (run-server app {:port 80})
  (println "Server started on port 80"))
