;; core.logic relation for square-pyramid slope geometry
;; slope is the face angle above the base plane (in degrees)
(ns langdev.pyramid-logic
  (:require [clojure.core.logic :as l]))

(def ^:const epsilon 1.0e-9)

(defn approx=
  "Return true when a and b are within epsilon."
  [a b]
  (<= (Math/abs (- (double a) (double b))) epsilon))

(defn slope-deg
  "Given height and base width, return slope in degrees."
  [height base]
  (Math/toDegrees
   (Math/atan (/ (double height) (/ (double base) 2.0)))))

(defn height-from-base-slope
  "Given base width and slope in degrees, return height."
  [base slope]
  (* (/ (double base) 2.0)
     (Math/tan (Math/toRadians (double slope)))))

(defn base-from-height-slope
  "Given height and slope in degrees, return base width."
  [height slope]
  (* 2.0
     (/ (double height)
        (Math/tan (Math/toRadians (double slope))))))

(defn pyramid-slopeo
  "Relate height, base width, and slope (degrees).
  Provide any two numeric values to solve for the third."
  [height base slope]
  (l/project [height base slope]
    (cond
      (and (number? height) (number? base) (l/lvar? slope))
      (l/== slope (slope-deg height base))

      (and (number? height) (number? slope) (l/lvar? base))
      (l/== base (base-from-height-slope height slope))

      (and (number? base) (number? slope) (l/lvar? height))
      (l/== height (height-from-base-slope base slope))

      (and (number? height) (number? base) (number? slope))
      (if (approx= slope (slope-deg height base))
        (l/== 1 1)
        (l/== 1 2))

      :else
      (l/== 1 2))))

(comment
  ;; solve for slope
  (l/run* [q]
    (pyramid-slopeo 1.75 1.0 q))
  ;; => (74.05460409907715)

  ;; solve for base
  (l/run* [q]
    (pyramid-slopeo 1.75 q 74.05460409907715))
  ;; => (1.0)

  ;; solve for height
  (l/run* [q]
    (pyramid-slopeo q 1.0 74.05460409907715))
  ;; => (1.75)
  )
