(ns clojure.test-clojure.select-destructuring
  (:use clojure.test))

(deftest test-select
  (let [m {:a 1 :b 2 :c 3 :d 4 :e 5 :f 6
           'sa 10 'sb 11 'sc 12 'sd 13
           "ha" 20 "hb" 21 "hc" 22 "hd" 23
           :foo/x 30 :foo/y 31 :foo/z 32
           :only-in-map 999}]
    (let [{:keys [a b & c]
           :keys! [d]
           :syms [sa sb & sc]
           :syms! [sd]
           :strs [ha hb & hc]
           :strs! ["hb"]
           fx :foo/x
           x :e
           y :f
           :or {not-in-select 42 another-missing 99}
           :as whole
           :select sel}
          m]
      (is (= a 1))
      (is (= b 2))
      (is (= d 4))
      (is (= sa 10))
      (is (= sb 11))
      (is (= sd 13))
      (is (= ha "ha"))
      (is (= hb "hb"))
      (is (= fx 30))
      (is (= x 5))
      (is (= y 5))
      (is (= whole m))
      (is (= sel {:a 1 :b 2 :c 3 :d 4 :e 5 :f 6
                   'sa 10 'sb 11 'sc 12 'sd 13
                   "ha" 20 "hb" 21 "hc" 22
                   :foo/x 30}))
      (is (not (contains? sel :not-in-select)))
      (is (not (contains? sel :another-missing)))
      (is (not (contains? sel :only-in-map)))
      (is (not (contains? sel "hd")))
      (is (not (contains? sel :foo/y)))
      (is (not (contains? sel :foo/z))))

    (are [expected result]
         (= expected result)
         {:a 1 :b 2 :c 3}
         (let [{:keys [a b & c] :select s} m] s)

         {:a 1 :b 2 :c 3}
         (let [{:keys! [a b & c] :select s} m] s)

         {'sa 10 'sb 11 'sc 12}
         (let [{:syms [sa sb & sc] :select s} m] s)

         {'sa 10 'sb 11 'sc 12}
         (let [{:syms! [sa sb & sc] :select s} m] s)

         {"ha" 20 "hb" 21 "hc" 22}
         (let [{:strs [ha hb & hc] :select s} m] s)

         {"ha" 20 "hb" 21 "hc" 22}
         (let [{:strs! [ha hb & hc] :select s} m] s)

         {:e 5 :f 6}
         (let [{x :e y :f :select s} m] s)

         {:a 1}
         (let [{:keys [a] :or {missing 42} :select s} m] s)

         {:foo/x 30 :foo/y 31}
         (let [{:foo/keys [x & y] :select s} m] s)

         {:a 1 :b 2 :c 3 :d 4}
         (let [{:keys [a b & c] :select s} {:a 1 :b 2 :c 3 :d 4}] s))))

    (is (= {:a 1 :b 2 :c 3}
           (let [{:keys [a b & c] :select sbinds}
                 {:a 1 :b 2 :c 3 :d 4}]
             sbinds)))

    (testing "keys after & are collected but not bound"
      (is (= {:a 1 :b 2 :c 3}
             (let [{:keys [a & b] :select s} m] s)))
      (is (= 1 (let [{:keys [a & b] :select s} m] a))))

    (testing "mentioned keys absent from the source map are omitted from :select"
      (is (= {:a 1}
             (let [{:keys [a z] :select s} {:a 1}] s))))))
