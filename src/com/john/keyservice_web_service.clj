(ns com.john.keyservice-web-service
  (:require [clojure.tools.logging :as log]
            [compojure.core :as compojure]
            [com.john.keyservice-web-core :as core]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.trapperkeeper.services :as tk-services]))

(trapperkeeper/defservice key-web-service
  [[:ConfigService get-in-config]
   [:WebroutingService add-ring-handler get-route]
   ChannelService]
  (init [this context]
    (log/info "Initializing key webservice")
    (let [url-prefix (get-route this)]
      (add-ring-handler
        this
        (compojure/context url-prefix []
          (core/app (tk-services/get-service this :ChannelService))))
      (assoc context :url-prefix url-prefix)))

  (start [this context]
         (let [host (get-in-config [:webserver :host])
               port (get-in-config [:webserver :port])
               url-prefix (get-route this)]
              (log/infof "Hello web service started; visit http://%s:%s%s/encrypt/keys to check it out!"
                         host port url-prefix))
         context))
