(ns zhongwen.dictionary-test
  (:require [clojure.test :refer :all]
            [zhongwen.dictionary :refer :all]))

(deftest read-tone-with-no-number
  (testing "Syllable without number returns nil."
    (are [actual] (nil? (read-tone actual))
      ""
      "#"
      "x")))

(deftest read-tone-with-invalid-number
  (testing "Syllable with number not 1, 2, 3, 4, or 5 returns nil."
    (are [actual] (nil? (read-tone actual))
      "x0"
      "x6")))

(deftest read-tone-with-valid-number
  (testing "Syllable with number 1, 2, 3, 4, or 5 returns tone."
    (are [actual expected] (= (read-tone actual) expected)
      "x1" 1
      "x2" 2
      "x3" 3
      "x4" 4
      "x5" 5)))

(deftest read-entry-with-invalid-text
  (testing "Text not matching pattern returns nil."
    (are [actual] (nil? (read-entry actual))
      ""
      "# this is a comment.")))

(deftest read-entry-with-valid-text
  (testing "Text matching pattern returns entry."
    (let [entry (read-entry "中國 中国 [Zhong1 guo2] /China/")]
      (are [actual expected] (= actual expected)
        (-> entry :base :traditional) "中國"
        (-> entry :base :simplified) "中国"
        (-> entry :base :pinyin) "Zhong1 guo2"
        (-> entry :base :english) '("China")
        (-> entry :norm :traditional) "中國"
        (-> entry :norm :simplified) "中国"
        (-> entry :norm :pinyin) "zhong1 guo2"
        (-> entry :norm :english) '("china")
        (entry :tones) '(1 2)))))

(def entries (read-entries '("中國 中国 [Zhong1 guo2] /China/")))

(deftest traditional-part?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (is (nil? (some (traditional-part? "x") entries)))))

(deftest traditional-part?-with-valid-query
  (testing "Entries matching query returns true."
    (are [actual] (true? (some (traditional-part? actual) entries))
      "中"
      "國"
      "中國")))

(deftest simplified-part?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (is (nil? (some (simplified-part? "x") entries)))))

(deftest simplified-part?-with-valid-query
  (testing "Entries matching query returns true."
    (are [actual] (true? (some (simplified-part? actual) entries))
      "中"
      "国"
      "中国")))

(deftest pinyin-part?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (is (nil? (some (pinyin-part? "x") entries)))))

(deftest pinyin-part?-with-valid-query
  (testing "Entries matching query returns true."
    (are [actual] (true? (some (pinyin-part? actual) entries))
      "z"
      "g"
      "zhong1"
      "guo2"
      "zhong1 guo2")))

(deftest english-part?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (is (nil? (some (english-part? "x") entries)))))

(deftest english-part?-with-valid-query
  (testing "Entries matching query returns true."
    (are [actual] (true? (some (english-part? actual) entries))
      "c"
      "ch"
      "china")))

(deftest traditional-full?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (are [actual] (nil? (some (traditional-full? actual) entries))
      "x"
      "中"
      "國")))

(deftest traditional-full?-with-valid-query
  (testing "Entries matching query returns true."
    (is (true? (some (traditional-full? "中國") entries)))))

(deftest simplified-full?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (are [actual] (nil? (some (simplified-full? actual) entries))
      "x"
      "中"
      "国")))

(deftest simplified-full?-with-valid-query
  (testing "Entries matching query returns true."
    (is (true? (some (simplified-full? "中国") entries)))))

(deftest pinyin-full?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (are [actual] (nil? (some (pinyin-full? actual) entries))
      "x"
      "z"
      "g"
      "zhong"
      "guo"
      "zhong1 g")))

(deftest pinyin-full?-with-valid-query
  (testing "Entries matching query returns true."
    (are [actual] (true? (some (pinyin-full? actual) entries))
      "zhong1"
      "guo2"
      "zhong1 guo2")))

(deftest english-full?-with-invalid-query
  (testing "Entries not matching query returns nil."
    (are [actual] (nil? (some (english-full? actual) entries))
      "x"
      "c"
      "chin")))

(deftest english-full?-with-valid-query
  (testing "Entries matching query returns true."
    (is (true? (some (english-full? "china") entries)))))

(deftest score-
  (testing "Entry is correctly scored."
    (are [actual expected] (= (score actual (first entries)) (float expected))
      "" 0.0
      "x" 0.0
      "中" 0.5
      "國" 0.5
      "国" 0.5
      "中國" 1.0
      "z" 0.09090909
      "zh" 0.18181819
      "zhong1" 0.54545456
      "guo2" 0.36363637
      "zhong1 guo2" 1.0
      "c" 0.2
      "ch" 0.4
      "china" 1.0)))

