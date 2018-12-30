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
  #(string/includes? (:traditional %) query))

(defn match-simplified [query]
  #(string/includes? (:simplified %) query))

(defn match-pinyin [query]
  #(string/includes? (:pinyin %) query))

(defn match-english [query]
  (fn [entry]
    (some #(string/includes? % query) (entry :english))))

(defn search-all [query entries]
  (filter (some-fn (match-traditional query)
                   (match-simplified query)
                   (match-pinyin query)
                   (match-english query))
          entries))
