(ns pasta
  (:require [coast]
            [components :refer [container container-index tc link-to table thead tbody td th tr
                                button-to text-muted mr2 dl dd dt submit input label textarea
                                button-link-to]]
            [clojure.edn :as edn]
            [clojure.java.io :as cljio])
  (:import [java.time Instant ZoneId]
           [java.time.format DateTimeFormatter FormatStyle]))

(def pasta-content-preview-len 70)
(def strings (-> "strings.edn" cljio/resource slurp edn/read-string))

(defn epoch-to-readable-date [epoch]
  (.. (Instant/ofEpochSecond epoch)
      (atZone (ZoneId/systemDefault))
      (format (DateTimeFormatter/ofPattern "dd/MM/yy kk:mm"))))

(defn make-pasta-heading [author epoch-date]
  (str (:submitted-by strings)
       author
       (:submitted-at strings)
       (epoch-to-readable-date epoch-date)))

(defn index [request]
  (let [rows (coast/q '[:select *
                        :from pasta
                        :order id
                        :limit 10
                        :where [:approved 1]])]
    (container-index
     {:mw 8}
     (when (not (empty? rows))
       (button-link-to (coast/url-for ::build) (:new-pasta strings)))

     (when (empty? rows)
       (tc
        (button-link-to (coast/url-for ::build) (:new-pasta strings))))

     (when (not (empty? rows))
       (table
        (thead
         (tr
          (th (:author strings))
          (th (:creation-date strings))
          (th (:peek strings))))
        (tbody
         (for [row rows]
           (tr
            (td (:pasta/author row))
            (td (-> row :pasta/created-at epoch-to-readable-date))
                            ;; (td (:pasta/content row))
            (td (if (< (-> row :pasta/content count) pasta-content-preview-len)
                  (:pasta/content row)
                  (str
                   (subs (:pasta/content row) 0 pasta-content-preview-len)
                   "...")))
            (td
             (link-to (coast/url-for ::view row) (:view-pasta strings)))))))))))

(defn view [request]
  (let [id (-> request :params :pasta-id)
        pasta (coast/fetch :pasta id)]
    (container {:mw 8}
               (link-to (coast/url-for ::index) (:back strings))
               [:p {:class "i"} (make-pasta-heading
                                 (:pasta/author pasta)
                                 (:pasta/created-at pasta))]
               [:p {:class "ml0 pasta-content"} (:pasta/content pasta)])))

(defn errors [m]
  [:div {:class "bg-red white pa2 mb4 br1"}
   [:h2 {:class "f4 f-subheadline"} "אייי יש בעיה... לכו אחורה ונסו שוב ♥"]
   [:dl {:dir "ltr"}
    (for [[k v] m]
      [:div {:class "mb3"}
       (dd v)])]])

(defn build [request]
  (container
   {:mw 6}
   (if (some? (:errors request))
     (errors (:errors request))
               ;; show form when no errors
     (coast/form-for
      ::create
      (label {:for "pasta/author"} (:author-form strings))
      (input {:type "text" :name "pasta/author" :value (-> request :params :pasta/author)})

      (label {:for "pasta/content"} (:content strings))
      (textarea {:type "text" :name "pasta/content" :value (-> request :params :pasta/content)})

      (submit (:submit strings))
      (link-to (coast/url-for ::index) (:cancel strings))))))

(defn create [request]
  (let [[_ errors] (-> (coast/validate (:params request) [[:required [:pasta/author :pasta/content]]])
                       (select-keys [:pasta/author :pasta/content])
                       (assoc :pasta/approved false)
                       (coast/insert)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to ::index)
      (build (merge request errors)))))
