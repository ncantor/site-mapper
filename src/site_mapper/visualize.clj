(ns site-mapper.visualize
  (:require [rhizome.viz :refer [save-graph]]))

(defn draw [m filename]
  (save-graph (keys m) m
              :node->descriptor (fn [n] {:label n
                                          :shape :circle})
              :filename (str filename ".png")))
