(ns site-mapper.core
  (:require [itsy.core :as itsy]
          [clojure.pprint :refer [pprint]]
          [clj-yaml.core :as yaml]
          [site-mapper.visualize]))

(defn extract-all-local [base-url body]
  (filter (fn [s]
            (.startsWith s base-url))
          (itsy/extract-all base-url body)))

(defn my-handler [urls base-url]
  (fn [{:keys [url body]}]
    (swap! urls assoc url (itsy/extract-all base-url body))))

(defn draw-sitemap-for-url [url]
  (let [urls (atom {})
        crawl (itsy/crawl {
                          :url url
                          :handler (my-handler urls url)
                          :workers 10
                          :url-limit 100
                          :url-extractor itsy/extract-all
                          :http-opts {}
                          :host-limit true
                          :polite? true})]
    (itsy/stop-workers crawl)
    @urls))

(defn -main [url filename-prefix]
  (let [sitemap (draw-sitemap-for-url url)]
  ; Not sure if visual or text representation desired. Providing both.
  (site-mapper.visualize/draw sitemap filename-prefix)
  ; Can't represent everything visually, yet. Raw data output to file.
  (spit (str filename-prefix "-text.yml") (yaml/generate-string sitemap)))
  (shutdown-agents))
