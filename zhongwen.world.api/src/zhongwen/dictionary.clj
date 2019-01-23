(ns zhongwen.dictionary
  (:require [clojure.java.io :as io]
            [clojure.string :as s]))

; A valid syllable is any string ending in a digit 1-5.
(defn read-tone [syllable]
  (when-let [tone (re-find #"[1-5]$" syllable)]
    (Integer. tone)))

(defn read-tones [matches]
  (map read-tone (s/split (nth matches 3) #" ")))

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
(defn matches->entry [matches]
  {:base (read-base-entry matches)
   :norm (read-norm-entry matches)
   :tones (read-tones matches)})

; A valid line is as follows:
;   traditional simplified [pinyin] /english/english/.../
; Example:
;   國 国 [guo2] /country/nation/state/national/CL:個|个[ge4]/"
(defn read-entry [line]
  (let [matches (re-matches #"(\S+)\s(\S+)\s\[([^\]]+)\]\s\/(.+)\/" line)]
    (when (= (count matches) 5)
      (matches->entry matches))))

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

(defn subsumes?
  [main sub]
    (some
      (partial = (seq sub))
      (partition (count sub) 1 main)))

(defn traditional-part? [query]
  #(s/includes? (-> % :norm :traditional) query))

(defn simplified-part? [query]
  #(s/includes? (-> % :norm :simplified) query))

(defn pinyin-part? [query]
  #(s/includes? (-> % :norm :pinyin) query))

(defn english-part? [query]
  (fn [entry]
    (->> entry :norm :english
         (some #(s/includes? % query)))))

(defn traditional-full? [query]
  #(= (-> % :norm :traditional) query))

(defn simplified-full? [query]
  #(= (-> % :norm :simplified) query))

(defn pinyin-full? [query]
  #(subsumes? (s/split (-> % :norm :pinyin) #" ") (s/split query #" ")))

(defn english-full? [query]
  (fn [entry]
    (->> entry :norm :english
         (some #(subsumes? (s/split % #" ") (s/split query #" "))))))

(defn match-part [query]
  (some-fn (traditional-part? query)
           (simplified-part? query)
           (pinyin-part? query)
           (english-part? query)))

(defn match-full [query]
  (some-fn (traditional-full? query)
           (simplified-full? query)
           (pinyin-full? query)
           (english-full? query)))

; Scoring is done on a scale of 0 to 1 based on percentage of text matched.
(defn score [query entry]
  (letfn [(score-value [value]
            (let [occurrences (count (re-seq (re-pattern query) value))]
              (float (/ (* (count query) occurrences) (count value)))))]
    (max (score-value (-> entry :norm :traditional))
         (score-value (-> entry :norm :simplified))
         (score-value (-> entry :norm :pinyin))
         (apply max (map score-value (-> entry :norm :english))))))

(defn search [pred query entries]
  (-> query s/lower-case pred (filter entries)))

(defn search-full [query entries]
  (search match-full query entries))

(defn search-part [query entries]
  (search match-part query entries))

; Searches full and partial matches, returning only full matches
; if any exist, otherwise returning partial matches. Full matches are
; preferred in order to prevent a search for "go" returning less-relevant
; results such as "goat" and "gone".
(defn search-all [query entries]
  (let [full-matches (search-full query entries)
        part-matches (search-part query entries)
        matches (or (seq full-matches) part-matches)]
    (reverse (sort-by (partial score (s/lower-case query)) matches))))

