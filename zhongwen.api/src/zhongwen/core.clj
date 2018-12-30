(ns zhongwen.core
  (:require [clojure.data.json :as json]
            [compojure.core :refer :all]
            [compojure.route :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :refer [resource-response]]
            [zhongwen.dictionary :as dictionary]))

(def entries (dictionary/parse "/app/cedict_ts.u8"))
(def limit 100)

(defn search [query]
  {:status  200
   :body    (json/write-str (take limit (dictionary/search-all query entries)))})

(defroutes app
  (resources "/")
  (GET "/" [] (resource-response "/index.html" {:root "public"}))
  (GET "/api/search/:query" [query] (search query))
  (not-found (resource-response "/404.html" {:root "public"})))

(defn -main [& args]
  (org.httpkit.server/run-server app {:port 80})
  (println "Server started on port 80"))
