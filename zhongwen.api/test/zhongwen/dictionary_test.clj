(ns zhongwen.dictionary-test
  (:require [clojure.test :refer :all]
            [zhongwen.dictionary :refer :all]))

(deftest read-tone-with-no-number
  (testing "Syllable without number returns nil."
    (are [actual] (nil? actual)
      (read-tone "")
      (read-tone "#")
      (read-tone "x"))))

(deftest read-tone-with-invalid-number
  (testing "Syllable with number not 1, 2, 3, 4, or 5 returns nil."
    (are [actual] (nil? actual)
      (read-tone "x0")
      (read-tone "x6"))))

(deftest read-tone-with-valid-number
  (testing "Syllable with number 1, 2, 3, 4, or 5 returns tone."
    (are [expected actual] (= expected actual)
      1 (read-tone "x1")
      2 (read-tone "x2")
      3 (read-tone "x3")
      4 (read-tone "x4")
      5 (read-tone "x5"))))

(deftest read-tones-with-no-numbers
  (testing "Text with no numbers returns list of nil."
    (are [expected actual] (= expected actual)
      '(nil) (read-tones "")
      '(nil) (read-tones "#")
      '(nil nil nil) (read-tones "x x x"))))

(deftest read-tones-with-invalid-numbers
  (testing "Text with invalid numbers returns list of nil."
    (is (= '(nil nil) (read-tones "x0 x6")))))

(deftest read-tones-with-valid-numbers
  (testing "Text with valid numbers returns list of tones."
    (is (= '(1 2 3 4 5) (read-tones "x1 x2 x3 x4 x5")))))

(deftest read-entry-with-invalid-text
  (testing "Text not matching pattern returns nil."
    (are [actual] (nil? actual)
      (read-entry "")
      (read-entry "# this is a comment."))))

(deftest read-entry-with-valid-text
  (testing "Text matching pattern returns entry."
    (let [entry (read-entry "中國 中国 [Zhong1 guo2] /China/")]
      (is (= (entry :traditional) "中國"))
      (is (= (entry :simplified) "中国"))
      (is (= (entry :pinyin) "Zhong1 guo2"))
      (is (= (entry :english) '("China"))))))
