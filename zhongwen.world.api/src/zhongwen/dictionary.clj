(ns zhongwen.dictionary
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

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
  (->> lines
       (map read-entry)
       (filter some?)))

(defn parse [path]
  (with-open [reader (io/reader path)]
    (-> reader
        (line-seq)
        (doall)
        (read-entries))))

(defn match-traditional [query]
  #(string/includes? (string/lower-case (:traditional %)) query))

(defn match-simplified [query]
  #(string/includes? (string/lower-case (:simplified %)) query))

(defn match-pinyin [query]
  #(string/includes? (string/lower-case (:pinyin %)) query))

(defn match-english [query]
  (fn [entry]
    (->> entry
         (:english)
         (some #(string/includes? (string/lower-case %) query)))))

(defn match-any [query]
  (some-fn (match-traditional query)
           (match-simplified query)
           (match-pinyin query)
           (match-english query)))

(defn search-all [query entries]
  (-> query
      (string/lower-case)
      (match-any)
      (filter entries)))
