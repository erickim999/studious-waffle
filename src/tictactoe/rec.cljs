(ns tictactoe.rec
  (:refer-clojure :exclude [read])
  (:require [clojure.string :as string]
            [cognitect.transit :as t])
  (:require-macros [tictactoe.rec :refer [slurp-dep]]))

(def w (t/writer :json))
(def r (t/reader :json))

(def read (partial t/read r))
(def write (partial t/write w))


(defn hydrate-recording [file]
  (let [chunks (-> file string/trim-newline string/split-lines)]
    (mapcat t/read chunks)))


(def rec (hydrate-recording (slurp-dep "resources/data/rJZfdFLcg.mtf")))


