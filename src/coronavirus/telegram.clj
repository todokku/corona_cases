(ns coronavirus.telegram
  (:gen-class)
  (:require [clj-time-ext.core :as te]
            [clojure.core.async :as async :refer [<!!]]
            [clojure.string :as str]
            [clojure.tools.logging :as log]
            [coronavirus.messages :as m]
            [environ.core :refer [env]]
            [morse.handlers :as h]
            [morse.polling :as p]
            [morse.polling-patch :as p-patch]))

#_(log/info "Telegram Chatbot:" bot)

(def chats (atom #{}))

(defn register-cmd [cmd cmd-fn]
  (h/command-fn
   cmd
   (fn [{{chat-id :id :as chat} :chat}]
     (when (= cmd "start")
       (swap! chats clojure.set/union #{chat})
       (->> @chats
            prn-str
            (spit "chats.edn")
            ))
     (let [tbeg (te/tnow)]
       (println (str "[" tbeg "           " " " m/bot-ver " /" cmd "]") chat)
       (cmd-fn chat-id)
       (println (str "[" tbeg ":" (te/tnow) " " m/bot-ver " /" cmd "]") chat)))))

(def cmds
  [
   {:name "start"       :f (fn [chat-id] (m/refresh-cmd-fn     chat-id))}
   {:name "refresh"     :f (fn [chat-id] (m/refresh-cmd-fn     chat-id))}
   {:name "interpolate" :f (fn [chat-id] (m/interpolate-cmd-fn chat-id))}
   {:name "about"       :f (fn [chat-id] (m/about-cmd-fn       chat-id))}
   {:name "keepcalm"    :f (fn [chat-id] (m/keepcalm-cmd-fn    chat-id))}
   ])

;; long polling
;; (as-> ...) creates
;; (h/defhandler handlerx
;;   (register-cmd "start"   (fn [chat-id] ...))
;;   (register-cmd "refresh" (fn [chat-id] ...))
;;   ...)
(as-> cmds $
  (map (fn [{:keys [name f]}] (list 'register-cmd name f)) $)
  (conj $ 'handler 'h/defhandler)
  (eval $))

(defn start-polling
  "Starts long-polling process.
  Handler is supposed to process immediately, as it will
  be called in a blocking manner."
  ([token handler] (start-polling token handler {}))
  ([token handler opts]
   (let [running (async/chan)
         updates (p-patch/create-producer-with-handle
                  running token opts (fn []
                                       (when (= m/bot-type "PROD")
                                         (System/exit 2))))]
     (p/create-consumer updates handler)
     running)))

(defn -main
  [& args]
  (log/info (str "[" (te/tnow) " " m/bot-ver "]") "Starting Telegram Chatbot...")
  (let [blank-prms (filter #(-> % env str/blank?) [:telegram-token])]
    (when (not-empty blank-prms)
      (log/fatal (str "Undef environment var(s): " blank-prms))
      (System/exit 1)))
  (<!! (start-polling m/token handler)))

;; For interactive development:
(def test-obj (atom nil))
(defn start   [] (swap! test-obj (fn [_] (start-polling m/token handler))))
(defn stop    [] (p/stop @test-obj))
(defn restart [] (if @test-obj (stop)) (start))
