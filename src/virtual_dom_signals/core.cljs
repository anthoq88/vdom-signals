(ns ^:figwheel-always virtual-dom-signals.core
  (:refer-clojure :exclude [map merge])
  (:require [cljs.core.async :as async :refer [>! <!]]
            [virtual-dom-signals.dom :as dom])
  (:require-macros [cljs.core.async.macros :refer [go-loop]]))

(enable-console-print!)

;; macro defsig -> automatically called?
;; TODO transducers

(defn animation-frames
  "Returns a signal of animation frames."
  []
  (let [ch (async/chan)]
    (dom/animate (fn tick [t]
                   (async/put! ch t)
                   (dom/animate tick)))
    ch))

(defn sample-on [sample-ch value-ch]
  (let [ch (async/chan)]
    (go-loop 
      []
      (let [sample (<! sample-ch)
            value (<! value-ch)]
        (>! ch value)
        (recur)))
    ch))

(defn map [f signal]
  (let [ch (async/chan)] ;;TODO core async map
    (go-loop 
      []
      (let [value (<! signal)]
        (>! ch (f value)))
      (recur))
    ch))

;;TODO move to unit tests folde
(dom/render! (map (fn [t] [:div {} (str "hello!:Wooo!OooWOo " t)]) (animation-frames)) (.getElementById js/document "app"))
