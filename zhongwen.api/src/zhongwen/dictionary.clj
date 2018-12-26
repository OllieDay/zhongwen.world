(ns zhongwen.dictionary)

(defn- read-tone [syllable]
  (if-let [tone (re-find #"[1-5]$" syllable)]
    (Integer. tone)
    nil))

(defn- read-tones [pinyin]
  (map read-tone (clojure.string/split pinyin #" ")))

(defn- read-entry [line]
  (let [matches (re-matches #"(\S+)\s(\S+)\s\[([^\]]+)\]\s\/(.+)\/" line)]
    (if (= (count matches) 5)
      {:traditional (nth matches 1)
       :simplified (nth matches 2)
       :pinyin (nth matches 3)
       :english (clojure.string/split (nth matches 4) #"/")
       :tones (read-tones (nth matches 3))}
      nil)))

(defn- read-entries [lines]
  (filter some? (map read-entry lines)))

(defn parse [path]
  (with-open [reader (clojure.java.io/reader path)]
    (read-entries (doall (line-seq reader)))))
