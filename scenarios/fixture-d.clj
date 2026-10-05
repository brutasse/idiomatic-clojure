(ns app.profile
  (:require [app.store :as store]
            [clojure.string :as str]))

(defn merged-view
  "Base profile with request overrides applied."
  [base overrides]
  (->> base (merge overrides)))

(defn normalize-phone [user]
  (when-let [country (:country user)]
    (when-let [phone (:phone user)]
      (when-let [formatted (store/format-phone country phone)]
        (when (pos? (count formatted))
          (.substring formatted 1))))))

(defn apply-tags [user tags premium?]
  (let [user (assoc user :tags tags)]
    (let [user (if premium? (assoc user :tier :gold) user)]
      (if (seq (:email user))
        (update user :email str/lower-case)
        user))))

(defn display-name [user]
  (str/upper-case
   (:display (store/enrich
              (first (filter :active? (store/find-users {:org (:org user)})))))))

(defn active-balance-report [org]
  (into (sorted-map)
        (map (fn [u] [(:name u) (:balance u)])
             (remove (comp zero? :balance)
                     (filter :active? (store/find-users {:org org}))))))
