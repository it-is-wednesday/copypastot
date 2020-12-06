(ns pasta
  (:require [coast]
            [components :refer [link-to dd submit strings]]
            [clojure.set])
  (:import [java.time Instant ZoneId]
           [java.time.format DateTimeFormatter FormatStyle]))

(def pasta-content-preview-len 60)

(defn epoch-to-readable-date [epoch]
  (.. (Instant/ofEpochSecond epoch)
      (atZone (ZoneId/systemDefault))
      (format (DateTimeFormatter/ofPattern "dd/MM/yy kk:mm"))))

(defn make-pasta-heading [author epoch-date]
  (str (:submitted-by strings)
       author
       (:submitted-at strings)
       (epoch-to-readable-date epoch-date)))

(defn search-bar
  ([] (search-bar ""))
  ([current-query]
   [:div.pastot-search-container
    (coast/form-for
     ::search
     {:method :get}
     ;; "ltr" is used to make sure the submit button shows up on the right of the search bar itself
     [:div.input-group {:dir "ltr"}
      ;; submit button
      [:div.input-group-prepend
       [:input.btn.btn-outline-danger.pastot-primary-color
        {:type "submit" :value (:search strings)}]]
      ;; textbox
      [:input.form-control {:type "search" :name "query" :dir "rtl" :value current-query}]])]))

(defn pastas-table [rows]
  [:table.table
   [:thead
    [:tr
     [:th (:author strings)]
     [:th (:creation-date strings)]
     [:th (:peek strings)]
     [:th]]]
   [:tbody
    (for [row rows]
      [:tr
       ;; author
       [:td.min (:pasta/author row)]
       ;; creation date
       [:td.min (-> row :pasta/created-at epoch-to-readable-date)]
       ;; content peek
       [:td (if (< (-> row :pasta/content count) pasta-content-preview-len)
              (:pasta/content row)
              (str
               (subs (:pasta/content row) 0 pasta-content-preview-len)
               "..."))]
       ;; show more
       [:td
        (-> row keys println)
        (link-to (coast/url-for ::view row) (:view-pasta strings))]])]])

(defn index [request]
  (let [rows (coast/q '[:select *
                        :from pasta
                        :where [:approved 1]
                        :order created-at desc])]
    [:div.flex-container
     [:a.btn.btn-danger.pastot-new-pasta-button
      {:href (coast/url-for ::build)}
      (:new-pasta strings)]

     (search-bar)

     (when (not (empty? rows))
       (pastas-table rows))]))

(defn view [request]
  (let [id (-> request :params :pasta-id)
        pasta (coast/fetch :pasta id)]
    [:div
     (search-bar)
     [:div.pastot-pasta-header-container
      [:p (link-to (coast/url-for ::index) (:back strings))]
      [:button#btn-copy.btn.btn-danger {:onclick "copyCurrentlyViewedPasta()"} (:copy strings)]
      [:p.pastot-pasta-author  (make-pasta-heading
                                (:pasta/author pasta)
                                (:pasta/created-at pasta))]]
     [:p {:class "pasta-content"} (:pasta/content pasta)]]))

(defn errors [m]
  [:div
   [:h2  "אייי יש בעיה... לכו אחורה ונסו שוב ♥"]
   [:dl {:dir "ltr"}
    (for [[k v] m]
      [:div
       (dd v)])]])

(defn build [request]
  (if (some? (:errors request))
    (errors (:errors request))

    ;; show form when no errors
    (coast/form-for
     ::create

     ;; author input
     [:div.form-group.text-right
      [:label {:for "pasta/author"} (:author-form strings)]
      [:input.form-control
       {:type "text" :name "pasta/author" :value (-> request :params :pasta/author)
        :required true}]]

     ;; content input
     [:div.form-group.text-right
      [:label {:for "pasta/content"} (:content strings)]
      [:textarea.pastot-new-pasta-content-textarea.form-control
       {:type "text" :name "pasta/content" :value (-> request :params :pasta/content)
        :required true}]]

     [:div.pastot-new-pasta-buttons-container
      ;; cancel button
      (link-to (coast/url-for ::index) (:cancel strings))

      ;; submit button
      [:input.btn.btn-outline-danger.pastot-primary-color.pastot-new-pasta-submit-button
       {:type "submit" :value (:submit strings)}]])))

(defn create [request]
  (let [[_ errors] (-> (coast/validate (:params request) [[:required [:pasta/author :pasta/content]]])
                       (select-keys [:pasta/author :pasta/content])
                       (assoc :pasta/approved false)
                       (coast/insert)
                       (coast/rescue))]
    (if (nil? errors)
      (coast/redirect-to ::index)
      (build (merge request errors)))))

(defn search [request]
  (let [plain-query (-> request :params :query)
        like-query (str "%" plain-query "%")
        ;; sorry for using raw sql I just couldn't get the DSL to work with the ? thingy I swear
        rows (coast/q ["SELECT * FROM pasta WHERE content LIKE ? AND approved = 1" like-query])]
    [:div.flex-container
     [:h3.pastot-primary-color (:search-results strings)]
     (search-bar plain-query)
     (pastas-table
      ;; renaming because the raw sql query doesn't do it for us (unlike the DSL query)
      (mapv #(clojure.set/rename-keys % {:author :pasta/author
                                         :approved :pasta/approved
                                         :id :pasta/id
                                         :updated_at :pasta/updated-at
                                         :created_at :pasta/created-at
                                         :content :pasta/content})
            rows))]))
