(ns corona.commands
  (:require [clojure.java.io :as io]
            [clojure.string :as s]
            [corona.core :as c :refer [in?]]
            [corona.countries :as cc]
            [corona.api :as data]
            [corona.messages :as msg]
            [morse.api :as morse]
            [corona.countries :as co]))

(defn world [{:keys [chat-id country-code] :as prm}]
  (morse/send-text
   c/token chat-id msg/options
   (msg/info prm))
  (if (in? (data/all-affected-country-codes) country-code)
    (morse/send-photo c/token chat-id (msg/absolute-vals prm))))

(defn partition-in-chunks [col]
  (partition-all (/ (count col) 5) col))

(defn list-countries [{:keys [chat-id] :as prm}]
  (->> (msg/affected-country--name-code prm)
       (sort-by :i <)
       partition-in-chunks
       (map (fn [chunk]
              (morse/send-text
               c/token chat-id (select-keys (assoc prm :parse_mode "HTML")
                                            (keys msg/options))
               (msg/list-countries (assoc prm
                                          :data chunk
                                          :parse_mode "HTML")))))
       doall))

(defn list-continents [{:keys [chat-id] :as prm}]
  (let [prm (assoc prm :parse_mode "HTML")]
    (morse/send-text
     c/token chat-id (select-keys prm (keys msg/options))
     (msg/list-continents prm))))

(defn interpolate [{:keys [chat-id country] :as prm}]
  (morse/send-photo c/token chat-id (msg/interpolated-vals prm)))

(defn snapshot [{:keys [chat-id] :as prm}]
  (morse/send-text
   c/token chat-id msg/options
   "I'm sending you ~40MB file. Patience please...")
  (morse/send-document
   c/token chat-id
   (io/input-stream "resources/COVID-19/master.zip")))

(defn about [{:keys [chat-id] :as prm}]
  #_(morse/send-photo c/token chat-id
                      (io/input-stream "resources/pics/keepcalm.jpg"))
  (morse/send-text c/token chat-id msg/options (msg/about prm))
  (morse/send-text c/token chat-id
                   #_msg/options
                   (-> msg/options
                       (dissoc :parse_mode)
                       (assoc :disable_web_page_preview false))
                   (msg/remember-20-seconds prm))
  #_(morse/send-photo
   c/token chat-id (io/input-stream "resources/pics/how_to_handwash_lge.gif")))

(defn feedback [{:keys [chat-id] :as prm}]
  (morse/send-text c/token chat-id msg/options (msg/feedback prm)))

(defn references [{:keys [chat-id] :as prm}]
  (morse/send-text c/token chat-id msg/options (msg/references prm)))

(defn contributors [{:keys [chat-id] :as prm}]
  (morse/send-text
   c/token chat-id msg/options
   (msg/contributors prm)))

#_(defn normalize
  "Country name w/o spaces: e.g. \"United States\" => \"UnitedStates\""
  [country-code]
  (-> (c/country-name country-code)
      (s/replace " " "")))

(defn cmds-country-code [country-code]

  (defn- normalize
    "Country name w/o spaces: e.g. \"United States\" => \"UnitedStates\""
    []
    (-> (c/country-name country-code)
        (s/replace " " "")))

  (->>
   [(fn [c] (->> c s/lower-case))  ;; /de
    (fn [c] (->> c s/upper-case))  ;; /DE
    (fn [c] (->> c s/capitalize))  ;; /De

    (fn [c] (->> c c/country-code-3-letter s/lower-case)) ;; /deu
    (fn [c] (->> c c/country-code-3-letter s/upper-case)) ;; /DEU
    (fn [c] (->> c c/country-code-3-letter s/capitalize)) ;; /Deu

    (fn [c] (->> (normalize) s/lower-case))   ;; /unitedstates
    (fn [c] (->> (normalize) s/upper-case))   ;; /UNITEDSTATES
    (fn [c] (->> (normalize)))]
   (mapv
    (fn [fun]
      {:name (fun country-code)
       :f
       (fn [chat-id]
         (world {:cmd-names msg/cmd-names
                 :chat-id chat-id
                 :country-code country-code
                 :pred-csv (fn [loc]
                             (condp = country-code
                               c/worldwide-2-country-code
                               true

                               c/default-2-country-code
                               ;; XX comes from the service
                               (= "XX" (cc/country_code loc))

                               (= country-code (cc/country_code loc))))
                 :pred (fn [loc]
                         ;; TODO s/upper-case is probably not needed
                         (condp = (s/upper-case country-code)
                           c/worldwide-2-country-code
                           true

                           c/default-2-country-code
                           ;; XX comes from the service
                           (= "XX" (:country_code loc))

                           (= country-code (:country_code loc))))}))}))))

(defn cmds-general []
  (let [prm
        (conj
         {:cmd-names msg/cmd-names
          :pred-csv (fn [_] true)
          :pred (fn [_] true)}
         msg/options)

        prm-country-code {:country-code (cc/country_code "Worldwide")}]
    [{:name msg/s-contributors
      :f (fn [chat-id] (contributors (assoc prm :chat-id chat-id)))
      :desc "Give credit where credit is due"}
     {:name msg/cmd-s-snapshot
      :f (fn [chat-id] (snapshot (assoc prm :chat-id chat-id)))
      :desc
      "Get a snapshot of https://github.com/CSSEGISandData/COVID-19.git master branch"}
     {:name msg/s-world
      :f (fn [chat-id] (world (-> (assoc prm :chat-id chat-id)
                                 (conj prm-country-code))))
      :desc msg/s-world-desc}

     {:name msg/s-list
      :f (fn [chat-id] (list-countries (-> (assoc prm :chat-id chat-id)
                                          (conj prm-country-code))))
      :desc msg/s-list-desc}
     {:name msg/s-start
      :f (fn [chat-id] (world (-> (assoc prm :chat-id chat-id)
                                 (conj prm-country-code))))
      :desc msg/s-world-desc}
     #_
     {:name "interpolate"
      :f (fn [chat-id] (interpolate (-> (assoc prm :chat-id chat-id)
                                       (conj prm-country-code))))
      :desc "Smooth the data / leave out the noise"}
     {:name msg/s-about
      :f (fn [chat-id] (about (assoc prm :chat-id chat-id)))
      :desc "Bot version & some additional info"}
     {:name msg/s-feedback
      :f (fn [chat-id] (feedback (assoc prm :chat-id chat-id)))
      :desc "Talk to the bot-creator"}
     {:name msg/s-references
      :f (fn [chat-id] (references (assoc prm :chat-id chat-id)))
      :desc "Knowledge is power - educate yourself"}]))

(defn cmds []
  (->> (c/all-country-codes)
       (mapv cmds-country-code)
       flatten
       (into (cmds-general))))

(defn bot-father-edit-cmds []
  (->> (cmds-general)
       (remove (fn [hm]
                 (in? [msg/s-start
                       ;; Need to save space it the mobile app. Sorry guys.
                       msg/s-contributors] (:name hm))))
       (sort-by :name)
       (reverse)
       (map (fn [{:keys [name desc]}] (println name "-" desc)))
       doall))
