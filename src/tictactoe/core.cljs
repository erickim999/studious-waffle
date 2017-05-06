(ns tictactoe.core
  (:require [reagent.core :as reagent :refer [atom]]
            [tictactoe.rec :refer [rec]]))

(enable-console-print!)

(defn new-board [n]
  (vec (repeat n (vec (repeat n "B")))))

(def board-size 3)

(def win-length 3)

(defonce app-state
  (atom {:text "Welcome to tic tac toe!"
         :board (new-board board-size)
         :game-status :in-progress}))

; (add-watch app-state :check-game-status
;   (fn [_ _ old-state new-state]
;     ))

(defn computer-move [board]
  (let [remaining-spots (for [i (range board-size)
                              j (range board-size)
                              :when (= (get-in board [j i]) "B")]
                          [j i])
        move (when (seq remaining-spots)
            (rand-nth remaining-spots))]
    (if move
      (assoc-in board move "C")
      board)))

(defn straight [owner board [x y] [dx dy] n]
  (every? true?
      (for [i (range n)]
        (= (get-in board [(+ (* i dx) x) (+ (* i dy) y)])
          owner))))

(defn win? [owner board n]
  (some true?
      (for [i (range board-size)
            j (range board-size)
            dir [[1 0] [0 1] [1 1] [1 -1]]]
        (straight owner board [i j] dir n))))

(defn full? [board]
  (every? #{"P" "C"} (apply concat board)))

(defn game-status [board]
  (cond
    (win? "P" board win-length) :player-victory
    (win? "C" board win-length) :computer-victory
    (full? board) :draw
    :else :in-progress))

(defn update-status [state]
  (assoc state :game-status (game-status (:board state))))

(defn check-game-status [state]
  (-> state
      (update-in [:board] computer-move)
      (update-status)))

; threading macro
; (update-status (update-in state [:board] computer-move))

(defn blank [i j]
  [:rect {:width 0.9
          :height 0.9
          :fill "grey"
          :x (+ 0.05 i)
          :y (+ 0.05 j)
          :on-click
          (fn blank-click [e]
            (when (= (:game-status @app-state) :in-progress)
              (swap! app-state assoc-in [:board j i] "P")
              (if (win? "P" (:board @app-state) win-length)
                (swap! app-state assoc :game-status :player-victory)
                (swap! app-state check-game-status))))}])

(defn circle [i j]
  [:circle
    {:r 0.39
     :stroke "green"
     :stroke-width 0.12
     :fill "none"
     :cx (+ 0.5 i)
     :cy (+ 0.5 j)}])

(defn cross [i j]
  [:g {:stroke "darkred"
       :stroke-width 0.1
       :stroke-linecap "round"
       :transform
       (str "translate(" (+ 0.1 i) "," (+ 0.1 j) ")"
            "scale(0.8)")}
    [:line {:x1 0 :y1 0 :x2 1 :y2 1}]
    [:line {:x1 1 :y1 0 :x2 0 :y2 1}]])

(defn tictactoe []
  [:center
  [:h1 (:text @app-state)]
  [:h2
    (case (:game-status @app-state)
      :player-victory "You won!"
      :computer-victory "Computer wins."
      :draw "Draw."
      :in-progress "")]
  [:p
    [:button
     {:on-click
      (fn new-game-click [e]
        (swap! app-state assoc
                :board (new-board board-size)
                :game-status :in-progress))}
      "New Game"]]
   (into
     [:svg
      {:view-box (str "0 0 " board-size " " board-size)
       :width 500
       :height 500}]
      (for [i (range board-size)
            j (range board-size)]
        (case (get-in @app-state [:board j i])
          "B" [blank i j]
          "P" [circle i j]
          "C" [cross i j])))])

(reagent/render-component [tictactoe]
                          (. js/document (getElementById "app")))

(defn on-js-reload [])
