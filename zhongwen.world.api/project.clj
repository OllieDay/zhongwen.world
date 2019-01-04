(defproject zhongwen "0.1.0"
  :author "Ollie Day"
  :description "zhongwen.world"
  :url "https://github.com/OllieDay/zhongwen.world"
  :license {:name "Affero General Public License"
            :url "https://github.com/OllieDay/zhongwen.world/blob/master/LICENSE"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [ring "1.7.0"]
                 [http-kit "2.3.0"]
                 [compojure "1.6.1"]]
  :main zhongwen.core
  :profiles {:uberjar {:aot :all}})
