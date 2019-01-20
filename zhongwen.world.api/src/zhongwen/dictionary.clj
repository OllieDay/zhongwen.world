(ns zhongwen.dictionary
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

; A valid syllable is any string ending in a digit 1-5.
(defn read-tone [syllable]
  (when-let [tone (re-find #"[1-5]$" syllable)]
    (Integer. tone)))

(defn read-base-traditional [matches]
  (nth matches 1))

(defn read-norm-traditional [matches]
  (-> matches read-base-traditional s/lower-case))

(defn read-base-simplified [matches]
  (nth matches 2))

(defn read-norm-simplified [matches]
  (-> matches read-base-simplified s/lower-case))

(defn read-base-pinyin [matches]
  (nth matches 3))

(defn read-norm-pinyin [matches]
  (-> matches read-base-pinyin s/lower-case))

(defn read-base-english [matches]
  (s/split (nth matches 4) #"/"))

(defn read-norm-english [matches]
  (->> matches read-base-english (map s/lower-case)))

(defn read-tones [matches]
  (map read-tone (s/split (nth matches 3) #" ")))

(defn read-base-entry [matches]
  {:traditional (read-base-traditional matches)
   :simplified (read-base-simplified matches)
   :pinyin (read-base-pinyin matches)
   :english (read-base-english matches)})

(defn read-norm-entry [matches]
  {:traditional (read-norm-traditional matches)
   :simplified (read-norm-simplified matches)
   :pinyin (read-norm-pinyin matches)
   :english (read-norm-english matches)})

; Entries are split into two sections:
;   :base
;     The original, unmodified text from cedict_ts.u8.
;     This is displayed to the user.
;   :norm
;     Normalized entry with lower-case text.
;     This is used to speed up case-insensitive searches.
(defn matches-to-entry [matches]
  {:base (read-base-entry matches)
   :norm (read-norm-entry matches)
   :tones (read-tones matches)})

; A valid line is as follows:
;   traditional simplified [pinyin] /english/english/.../
;
; Example:
;   國 国 [guo2] /country/nation/state/national/CL:個|个[ge4]/"
(defn read-entry [line]
  (let [matches (re-matches #"(\S+)\s(\S+)\s\[([^\]]+)\]\s\/(.+)\/" line)]
    (when (= (count matches) 5)
      (matches-to-entry matches))))

(defn read-entries [lines]
  (->> lines
       (map read-entry)
       (filter some?)))

(defn parse [path]
  (with-open [reader (io/reader path)]
    (-> reader
        line-seq
        doall
        read-entries)))

; Matches are always case-insensitive so compare with :norm

(defn match-traditional [query]
  #(s/includes? (get-in % [:norm :traditional]) query))

(defn match-simplified [query]
  #(s/includes? (get-in % [:norm :simplified]) query))

(defn match-pinyin [query]
  #(s/includes? (get-in % [:norm :pinyin]) query))

(defn match-english [query]
  (fn [entry]
    (some #(s/includes? % query) (get-in entry [:norm :english]))))

(defn match-any [query]
  (some-fn (match-traditional query)
           (match-simplified query)
           (match-pinyin query)
           (match-english query)))

; Scoring is done on a scale of 0 to 1 based on percentage of text matched.
(defn score [query entry]
  (letfn [(score-value [value]
            (let [occurrences (count (re-seq (re-pattern query) value))]
              (float (/ (* (count query) occurrences) (count value)))))]
    (max (score-value (get-in entry [:norm :traditional]))
         (score-value (get-in entry [:norm :simplified]))
         (score-value (get-in entry [:norm :pinyin]))
         (apply max (map score-value (get-in entry [:norm :english]))))))

(defn search-all [query entries]
  (let [results (-> query (s/lower-case) (match-any) (filter entries))]
        (reverse (sort-by (partial score (s/lower-case query)) results))))
