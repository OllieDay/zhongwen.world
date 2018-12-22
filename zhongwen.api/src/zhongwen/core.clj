(ns zhongwen.core
  (:require [clojure.data.json :as json]
            [clojure.string :as string]
            [compojure.core :refer :all]
            [compojure.route :refer :all]
            [org.httpkit.server :refer [run-server]]
            [ring.util.response :refer [resource-response]]
            [zhongwen.dictionary :as dictionary]))

(def entries (dictionary/parse "/app/cedict_ts.u8"))
(def limit 100)

(defn search-traditional [query]
  #(string/includes? (:traditional %) query))

(defn search-simplified [query]
  #(string/includes? (:simplified %) query))

(defn search-pinyin [query]
  #(string/includes? (:pinyin %) query))

(defn search-english [query]
  (fn [entry]
    (some #(string/includes? % query) (entry :english))))

(defn search-all [query]
  (take limit (filter (some-fn (search-traditional query)
                               (search-simplified query)
                               (search-pinyin query)
                               (search-english query)
                               (search-english query))
                      entries)))

(defn search [query]
  {:status  200
   :headers {"Content-Type" "application/json; charset=utf-8"}
   :body    (json/write-str (search-all query))})

(defroutes app
  (resources "/")
  (GET "/" [] (resource-response "/index.html" {:root "public"}))
  (GET "/api/search/:query" [query] (search query))
  (not-found (resource-response "/404.html" {:root "public"})))

(defn -main [& args]
  (org.httpkit.server/run-server app {:port 80})
  (println "Server started on port 80"))
