(ns zhongwen.dictionary
  (:require [clojure.java.io :as io]
            [clojure.string :as string]))

(defn read-tone
  "Read the tone in a `syllable`, returning 1-5 or `nil` if not valid."
  [syllable]
  (when-let [tone (re-find #"[1-5]$" syllable)]
    (Integer. tone)))

(defn read-tones
  "Read the tones for each word (separated by a space) in `pinyin`, returning a
  list of tones 1-5 or `nil` if not valid."
  [pinyin]
  (map read-tone (string/split pinyin #" ")))

(defn read-entry
  "Read the entry in `line`, returning `nil` if not valid.
  A valid entry is as follows:
  traditional simplified [pinyin] /english/english/.../
  For example:
  國 国 [guo2] /country/nation/state/national/CL:個|个[ge4]/"
  [line]
  (let [matches (re-matches #"(\S+)\s(\S+)\s\[([^\]]+)\]\s\/(.+)\/" line)]
    (when (= (count matches) 5)
      {:traditional (nth matches 1)
       :simplified  (nth matches 2)
       :pinyin      (nth matches 3)
       :english     (string/split (nth matches 4) #"/")
       :tones       (read-tones (nth matches 3))})))

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

(defn score
  "Score `query` against `entry` on a scale of 0 to 1 based on the percentage of
  matched text."
  [query entry]
  (letfn [(score-value [value]
            (let [lower-value (string/lower-case value)
                  occurrences (count (re-seq (re-pattern query) value))]
              (float (/ (* (count query) occurrences) (count value)))))]
    (max (score-value (:traditional entry))
         (score-value (:simplified entry))
         (score-value (:pinyin entry))
         (apply max (map score-value (:english entry))))))

(defn search-all [query entries]
  (let [results (-> query (string/lower-case) (match-any) (filter entries))]
        (reverse (sort-by (partial score (string/lower-case query)) results))))
