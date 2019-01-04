(ns zhongwen.dictionary
  (:require [clojure.string :as string]))

(defn read-tone [syllable]
  (if-let [tone (re-find #"[1-5]$" syllable)]
    (Integer. tone)
    nil))

(defn read-tones [pinyin]
  (map read-tone (string/split pinyin #" ")))

(defn read-entry [line]
  (let [matches (re-matches #"(\S+)\s(\S+)\s\[([^\]]+)\]\s\/(.+)\/" line)]
    (if (= (count matches) 5)
      {:traditional (nth matches 1)
       :simplified (nth matches 2)
       :pinyin (nth matches 3)
       :english (string/split (nth matches 4) #"/")
       :tones (read-tones (nth matches 3))}
      nil)))

(defn read-entries [lines]
  (filter some? (map read-entry lines)))

(defn parse [path]
  (with-open [reader (clojure.java.io/reader path)]
    (read-entries (doall (line-seq reader)))))

(defn match-traditional [query]
  #(string/includes? (string/lower-case (:traditional %)) query))

(defn match-simplified [query]
  #(string/includes? (string/lower-case (:simplified %)) query))

(defn match-pinyin [query]
  #(string/includes? (string/lower-case (:pinyin %)) query))

(defn match-english [query]
  (fn [entry]
    (some #(string/includes? (string/lower-case %) query) (entry :english))))

(defn search-all [query entries]
  (let [lower-query (string/lower-case query)]
    (filter (some-fn (match-traditional lower-query)
                     (match-simplified lower-query)
                     (match-pinyin lower-query)
                     (match-english lower-query))
            entries)))
