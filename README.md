# Coronavirus Cases - COVID-19

Few numbers about the Coronavirus Disease 2019 (COVID-19) presented by the
Telegram Chatbot: @corona_cases_bot

## Running locally

```clojure
(require 'coronavirus.web)
(def server (coronavirus.web/-main))

(require '[coronavirus.telegram])
(coronavirus.telegram/-main)
```

See [localhost:5000](http://localhost:5000/).

## Deploy to Heroku

See [deploy.sh](./deploy.sh).
