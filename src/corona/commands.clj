(ns corona.commands
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [corona.csv :as a]
            [corona.core :as c :refer [in?]]
            [corona.countries :as cc]
            [corona.messages :as msg]
            [morse.api :as morse]))

(defn world [{:keys [chat-id country-code] :as prm}]
  (morse/send-text
   c/token chat-id {:parse_mode "Markdown" :disable_web_page_preview true}
   (msg/info prm))
  (if (in? (a/affected-country-codes) country-code)
    (morse/send-photo c/token chat-id (msg/absolute-vals prm))))

(defn interpolate [{:keys [chat-id country] :as prm}]
  (morse/send-photo c/token chat-id (msg/interpolated-vals prm)))

(defn snapshot [{:keys [chat-id] :as prm}]
  (morse/send-text
   c/token chat-id {:parse_mode "Markdown" :disable_web_page_preview true}
   "I'm sending you ~40MB file. Patience please...")
  (morse/send-document
   c/token chat-id
   #_{:caption "https://github.com/CSSEGISandData/COVID-19/archive/master.zip"}
   (io/input-stream "resources/COVID-19/master.zip")))

(defn about [{:keys [chat-id] :as prm}]
  (morse/send-text
   c/token chat-id {:parse_mode "Markdown" :disable_web_page_preview true}
   (msg/about prm))
  (morse/send-text
   c/token chat-id {:disable_web_page_preview false}
   #_"https://www.who.int/gpsc/clean_hands_protection/en/"
   "https://www.who.int/gpsc/media/how_to_handwash_lge.gif")
  #_(morse/send-photo
     token chat-id (io/input-stream "resources/pics/how_to_handwash_lge.gif")))

(defn keepcalm [{:keys [chat-id]}]
  (morse/send-photo
   c/token chat-id (io/input-stream "resources/pics/keepcalm.jpg")))

(defn contributors [{:keys [chat-id] :as prm}]
  (morse/send-text
   c/token chat-id {:parse_mode "Markdown"
                    :disable_web_page_preview true}
   (msg/contributors prm)))

(def about msg/cmd-s-about)

(def cmd-names ["world" #_"interpolate" about "whattodo" "<country>"
                "contributors"])

#_(defn normalize
  "Country name w/o spaces: e.g. \"United States\" => \"UnitedStates\""
  [country-code]
  (-> (get c/is-3166-names country-code)
      (s/replace " " "")))

(defn cmds-country-code [country-code]

  (defn- normalize
    "Country name w/o spaces: e.g. \"United States\" => \"UnitedStates\""
    []
    (-> (get c/is-3166-names country-code)
        (s/replace " " "")))
  (->>
   [(fn [c] (->> c s/lower-case))  ;; /de
    (fn [c] (->> c s/upper-case))  ;; /DE
    (fn [c] (->> c s/capitalize))  ;; /De

    (fn [c] (->> c (get c/is-3166-abbrevs) s/lower-case)) ;; /deu
    (fn [c] (->> c (get c/is-3166-abbrevs) s/upper-case)) ;; /DEU
    (fn [c] (->> c (get c/is-3166-abbrevs) s/capitalize)) ;; /Deu

    (fn [c] (->> (normalize) s/lower-case))   ;; /unitedstates
    (fn [c] (->> (normalize) s/upper-case))   ;; /UNITEDSTATES
    (fn [c] (->> (normalize)))
    ]
   (mapv
    (fn [fun]
      {:name (fun country-code)
       :f
       (fn [chat-id]
         (world {:cmd-names cmd-names
                        :chat-id chat-id
                        :country-code country-code
                        :pred (fn [loc]
                                (condp = (s/upper-case country-code)
                                  c/worldwide-2-country-code
                                  true

                                  c/default-2-country-code
                                  ;; XX comes from the service
                                  (= "XX" (:country_code loc))

                                  (= country-code (:country_code loc))))}))}))))

(defn cmds-general []
  (let [prm {:cmd-names cmd-names
             :pred (fn [_] true)}
        prm-country-code {:country-code (cc/country_code "Worldwide")}]
    [
     {:name "contributors"
      :f (fn [chat-id] (contributors (assoc prm :chat-id chat-id)))
      :desc "Give credit where credit is due"}

     {:name "snapshot"
      :f (fn [chat-id] (snapshot (assoc prm :chat-id chat-id)))
      :desc
      "Get a snapshot of https://github.com/CSSEGISandData/COVID-19.git master branch"}
     {:name "world"
      :f (fn [chat-id] (world (-> (assoc prm :chat-id chat-id)
                                        (conj prm-country-code))))
      :desc "Start here"}
     {:name "start"
      :f (fn [chat-id] (world (-> (assoc prm :chat-id chat-id)
                                        (conj prm-country-code))))
      :desc "Start here"}
     #_
     {:name "interpolate"
      :f (fn [chat-id] (interpolate (-> (assoc prm :chat-id chat-id)
                                       (conj prm-country-code))))
      :desc "Smooth the data / leave out the noise"}
     {:name about
      :f (fn [chat-id] (about (assoc prm :chat-id chat-id)))
      :desc "Bot version & some additional info"}
     {:name "whattodo"
      :f (fn [chat-id] (keepcalm (assoc prm :chat-id chat-id)))
      :desc "Some personalized instructions"}]))

(defn cmds []
  (->> (c/all-country-codes)
       (mapv cmds-country-code)
       flatten
       (into (cmds-general))))

(defn bot-father-edit-cmds []
  (->> (cmds-general)
       (remove (fn [hm] (= "start" (:name hm))))
       (sort-by :name)
       (reverse)
       (map (fn [{:keys [name desc]}] (println name "-" desc)))
       doall))