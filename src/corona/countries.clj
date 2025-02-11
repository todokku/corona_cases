(ns corona.countries
  (:require
   [clojure.string :as s]
   [corona.core :as c :refer [in?]]
   [clojure.string :as s]))

(def continent-continent-code-hm
  {"Africa" "AF"
   "North America" "NA"
   "Oceania" "OC"
   "Antarctica" "AN"
   "Asia" "AS"
   "Europe" "EU"
   "South America" "SA"}
  ;; (clojure.set/map-invert continent-continent-code-hm)
  ;; {
  ;;  "AF" "Africa"
  ;;  "NA" "North America"
  ;;  "OC" "Oceania"
  ;;  "AN" "Antarctica"
  ;;  "AS" "Asia"
  ;;  "EU" "Europe"
  ;;  "SA" "South America"
  ;;  }
  )

(defn continent-code [continent]
  (get continent-continent-code-hm continent))
(defn continent-name [continent-code]
  (get (clojure.set/map-invert continent-continent-code-hm) continent-code))

(def country-country-code-hm
  "Mapping of country names to alpha-2 codes.
  https://en.wikipedia.org/wiki/ISO_3166-1#Officially_assigned_code_elements"
  (reduce
   into
   {
    "Afghanistan"                                  "AF"
    "Åland Islands"                                "AX"
    "Albania"                                      "AL"
    "Algeria"                                      "DZ"
    "American Samoa"                               "AS"
    "Andorra"                                      "AD"
    "Angola"                                       "AO"
    "Anguilla"                                     "AI"
    "Antarctica"                                   "AQ"
    "Antigua and Barbuda"                          "AG"
    "Argentina"                                    "AR"
    "Armenia"                                      "AM"
    "Aruba"                                        "AW"
    "Australia"                                    "AU"
    "Austria"                                      "AT"
    "Azerbaijan"                                   "AZ"
    "Bahamas"                                      "BS"
    "Bahrain"                                      "BH"
    "Bangladesh"                                   "BD"
    "Barbados"                                     "BB"
    "Belarus"                                      "BY"
    "Belgium"                                      "BE"
    "Belize"                                       "BZ"
    "Benin"                                        "BJ"
    "Bermuda"                                      "BM"
    "Bhutan"                                       "BT"
    "Bolivia, Plurinational State of"              "BO"
    "Bonaire, Sint Eustatius and Saba"             "BQ"
    "Bosnia and Herzegovina"                       "BA"
    "Botswana"                                     "BW"
    "Bouvet Island"                                "BV"
    "Brazil"                                       "BR"
    "British Indian Ocean Territory"               "IO"
    "Brunei Darussalam"                            "BN"
    "Bulgaria"                                     "BG"
    "Burkina Faso"                                 "BF"
    "Burundi"                                      "BI"
    "Cambodia"                                     "KH"
    "Cameroon"                                     "CM"
    "Canada"                                       "CA"
    "Cape Verde"                                   "CV"
    "Cayman Islands"                               "KY"
    "Central African Republic"                     "CF"
    "Chad"                                         "TD"
    "Chile"                                        "CL"
    "China"                                        "CN"
    "Christmas Island"                             "CX"
    "Cocos (Keeling) Islands"                      "CC"
    "Colombia"                                     "CO"
    "Comoros"                                      "KM"
    "Congo"                                        "CG"
    "Congo, the Democratic Republic of the"        "CD"
    "Cook Islands"                                 "CK"
    "Costa Rica"                                   "CR"
    "Côte d'Ivoire"                                "CI"
    "Croatia"                                      "HR"
    "Cuba"                                         "CU"
    "Curaçao"                                      "CW"
    "Cyprus"                                       "CY"
    "Czech Republic"                               "CZ"
    "Denmark"                                      "DK"
    "Djibouti"                                     "DJ"
    "Dominica"                                     "DM"
    "Dominican Republic"                           "DO"
    "Ecuador"                                      "EC"
    "Egypt"                                        "EG"
    "El Salvador"                                  "SV"
    "Equatorial Guinea"                            "GQ"
    "Eritrea"                                      "ER"
    "Estonia"                                      "EE"
    "Ethiopia"                                     "ET"
    "Falkland Islands (Malvinas)"                  "FK"
    "Faroe Islands"                                "FO"
    "Fiji"                                         "FJ"
    "Finland"                                      "FI"
    "France"                                       "FR"
    "French Guiana"                                "GF"
    "French Polynesia"                             "PF"
    "French Southern Territories"                  "TF"
    "Gabon"                                        "GA"
    "Gambia"                                       "GM"
    "Georgia"                                      "GE"
    "Germany"                                      "DE"
    "Ghana"                                        "GH"
    "Gibraltar"                                    "GI"
    "Greece"                                       "GR"
    "Greenland"                                    "GL"
    "Grenada"                                      "GD"
    "Guadeloupe"                                   "GP"
    "Guam"                                         "GU"
    "Guatemala"                                    "GT"
    "Guernsey"                                     "GG"
    "Guinea"                                       "GN"
    "Guinea-Bissau"                                "GW"
    "Guyana"                                       "GY"
    "Haiti"                                        "HT"
    "Heard Island and McDonald Islands"            "HM"
    "Holy See (Vatican City State)"                "VA"
    "Honduras"                                     "HN"
    "Hong Kong"                                    "HK"
    "Hungary"                                      "HU"
    "Iceland"                                      "IS"
    "India"                                        "IN"
    "Indonesia"                                    "ID"
    "Iran, Islamic Republic of"                    "IR"
    "Iraq"                                         "IQ"
    "Ireland"                                      "IE"
    "Isle of Man"                                  "IM"
    "Israel"                                       "IL"
    "Italy"                                        "IT"
    "Jamaica"                                      "JM"
    "Japan"                                        "JP"
    "Jersey"                                       "JE"
    "Jordan"                                       "JO"
    "Kazakhstan"                                   "KZ"
    "Kenya"                                        "KE"
    "Kiribati"                                     "KI"
    "Korea, Democratic People's Republic of"       "KP"
    "Korea, Republic of"                           "KR"
    "Kuwait"                                       "KW"
    "Kyrgyzstan"                                   "KG"
    "Lao People's Democratic Republic"             "LA"
    "Latvia"                                       "LV"
    "Lebanon"                                      "LB"
    "Lesotho"                                      "LS"
    "Liberia"                                      "LR"
    "Libya"                                        "LY"
    "Liechtenstein"                                "LI"
    "Lithuania"                                    "LT"
    "Luxembourg"                                   "LU"
    "Macao"                                        "MO"
    "North Macedonia"                              "MK"
    "Madagascar"                                   "MG"
    "Malawi"                                       "MW"
    "Malaysia"                                     "MY"
    "Maldives"                                     "MV"
    "Mali"                                         "ML"
    "Malta"                                        "MT"
    "Marshall Islands"                             "MH"
    "Martinique"                                   "MQ"
    "Mauritania"                                   "MR"
    "Mauritius"                                    "MU"
    "Mayotte"                                      "YT"
    "Mexico"                                       "MX"
    "Micronesia, Federated States of"              "FM"
    "Moldova, Republic of"                         "MD"
    "Monaco"                                       "MC"
    "Mongolia"                                     "MN"
    "Montenegro"                                   "ME"
    "Montserrat"                                   "MS"
    "Morocco"                                      "MA"
    "Mozambique"                                   "MZ"
    "Myanmar"                                      "MM"
    "Namibia"                                      "NA"
    "Nauru"                                        "NR"
    "Nepal"                                        "NP"
    "Netherlands"                                  "NL"
    "New Caledonia"                                "NC"
    "New Zealand"                                  "NZ"
    "Nicaragua"                                    "NI"
    "Niger"                                        "NE"
    "Nigeria"                                      "NG"
    "Niue"                                         "NU"
    "Norfolk Island"                               "NF"
    "Northern Mariana Islands"                     "MP"
    "Norway"                                       "NO"
    "Oman"                                         "OM"
    "Pakistan"                                     "PK"
    "Palau"                                        "PW"
    "Palestine, State of"                          "PS"
    "Panama"                                       "PA"
    "Papua New Guinea"                             "PG"
    "Paraguay"                                     "PY"
    "Peru"                                         "PE"
    "Philippines"                                  "PH"
    "Pitcairn"                                     "PN"
    "Poland"                                       "PL"
    "Portugal"                                     "PT"
    "Puerto Rico"                                  "PR"
    "Qatar"                                        "QA"
    "Réunion"                                      "RE"
    "Romania"                                      "RO"
    "Russian Federation"                           "RU"
    "Rwanda"                                       "RW"
    "Saint Barthélemy"                             "BL"
    "Saint Helena, Ascension and Tristan da Cunha" "SH"
    "Saint Kitts and Nevis"                        "KN"
    "Saint Lucia"                                  "LC"
    "Saint Martin (French part)"                   "MF"
    "Saint Pierre and Miquelon"                    "PM"
    "Saint Vincent and the Grenadines"             "VC"
    "Samoa"                                        "WS"
    "San Marino"                                   "SM"
    "Sao Tome and Principe"                        "ST"
    "Saudi Arabia"                                 "SA"
    "Senegal"                                      "SN"
    "Serbia"                                       "RS"
    "Seychelles"                                   "SC"
    "Sierra Leone"                                 "SL"
    "Singapore"                                    "SG"
    "Sint Maarten (Dutch part)"                    "SX"
    "Slovakia"                                     "SK"
    "Slovenia"                                     "SI"
    "Solomon Islands"                              "SB"
    "Somalia"                                      "SO"
    "South Africa"                                 "ZA"
    "South Georgia and the South Sandwich Islands" "GS"
    "South Sudan"                                  "SS"
    "Spain"                                        "ES"
    "Sri Lanka"                                    "LK"
    "Sudan"                                        "SD"
    "Suriname"                                     "SR"
    "Svalbard and Jan Mayen"                       "SJ"
    "Swaziland"                                    "SZ"
    "Sweden"                                       "SE"
    "Switzerland"                                  "CH"
    "Syrian Arab Republic"                         "SY"
    "Taiwan, Province of China"                    "TW"
    "Tajikistan"                                   "TJ"
    "Tanzania, United Republic of"                 "TZ"
    "Thailand"                                     "TH"
    "Timor-Leste"                                  "TL"
    "Togo"                                         "TG"
    "Tokelau"                                      "TK"
    "Tonga"                                        "TO"
    "Trinidad and Tobago"                          "TT"
    "Tunisia"                                      "TN"
    "Turkey"                                       "TR"
    "Turkmenistan"                                 "TM"
    "Turks and Caicos Islands"                     "TC"
    "Tuvalu"                                       "TV"
    "Uganda"                                       "UG"
    "Ukraine"                                      "UA"
    "United Arab Emirates"                         "AE"
    "United Kingdom"                               "GB"
    "United States"                                "US"
    "United States Minor Outlying Islands"         "UM"
    "Uruguay"                                      "UY"
    "Uzbekistan"                                   "UZ"
    "Vanuatu"                                      "VU"
    "Venezuela, Bolivarian Republic of"            "VE"
    "Viet Nam"                                     "VN"
    "Virgin Islands, British"                      "VG"
    "Virgin Islands, U.S."                         "VI"
    "Wallis and Futuna"                            "WF"
    "Western Sahara"                               "EH"
    "Yemen"                                        "YE"
    "Zambia"                                       "ZM"
    "Zimbabwe"                                     "ZW"
    }
   (map clojure.set/map-invert [c/country-code-others c/country-code-worldwide])))

(def aliases-hm
  "Mapping of alternative names, spelling, typos to the names of countries used by
  the ISO 3166-1 norm.

  Conjoin \"North Ireland\" on \"United Kingdom\".

  https://en.wikipedia.org/wiki/Channel_Islands
  \"Guernsey\" and \"Jersey\" form \"Channel Islands\". Conjoin \"Guernsey\" on \"Jersey\".
  \"Jersey\" has higher population.

  \"Others\" has no mapping.

  TODO \"Macau\" is probably just misspelled \"Macao\". Report it to CSSEGISandData/COVID-19.
  "
  {
   "World"           (->> c/country-code-worldwide vals first)
   "Czechia"          "Czech Republic"
   "Mainland China"   "China"
   "South Korea"      "Korea, Republic of"

   "Taiwan"                         "Taiwan, Province of China"
   "Taiwan*"                        "Taiwan, Province of China"
   "Taipei and environs"            "Taiwan, Province of China"

   "US"               "United States"

   ;; TODO Macau is probably just misspelled Macao. Report it to CSSEGISandData/COVID-19
   "Macau"            "Macao"

   "Vietnam"          "Viet Nam"
   "UK"               "United Kingdom"
   "Russia"           "Russian Federation"
   "Iran"             "Iran, Islamic Republic of"
   "Saint Barthelemy" "Saint Barthélemy"

   "Palestine"          "Palestine, State of"
   "State of Palestine" "Palestine, State of"

   "Vatican City"     "Holy See (Vatican City State)"

   "DR Congo"                         "Congo, the Democratic Republic of the"
   "Congo (Kinshasa)"                 "Congo, the Democratic Republic of the"
   "Democratic Republic of the Congo" "Congo, the Democratic Republic of the"

   "Tanzania"                 "Tanzania, United Republic of"
   "Venezuela"                "Venezuela, Bolivarian Republic of"
   "North Korea"              "Korea, Democratic People's Republic of"
   "Syria"                    "Syrian Arab Republic"
   "Bolivia"                  "Bolivia, Plurinational State of"
   "Laos"                     "Lao People's Democratic Republic"
   "Moldova"                  "Moldova, Republic of"
   "Republic of Moldova"      "Moldova, Republic of"
   "Eswatini"                 "Swaziland"
   "Cabo Verde"               "Cape Verde"
   "Brunei"                   "Brunei Darussalam"
   "Sao Tome & Principe"      "Sao Tome and Principe"

   "Micronesia"                     "Micronesia, Federated States of"
   "F.S. Micronesia"                "Micronesia, Federated States of"
   "Federated States of Micronesia" "Micronesia, Federated States of"

   "St. Vincent & Grenadines" "Saint Vincent and the Grenadines"
   "U.S. Virgin Islands"      "Virgin Islands, U.S."
   "Saint Kitts & Nevis"      "Saint Kitts and Nevis"
   "Faeroe Islands"           "Faroe Islands"
   "Sint Maarten"             "Sint Maarten (Dutch part)"
   "Turks and Caicos"         "Turks and Caicos Islands"
   "Saint Martin"             "Saint Martin (French part)"
   "British Virgin Islands"   "Virgin Islands, British"
   "Wallis & Futuna"          "Wallis and Futuna"
   "Saint Helena"             "Saint Helena, Ascension and Tristan da Cunha"
   "Saint Pierre & Miquelon"  "Saint Pierre and Miquelon"
   "Falkland Islands"         "Falkland Islands (Malvinas)"
   "Holy See"                 "Holy See (Vatican City State)"
   "Republic of Ireland"      "Ireland"
   "Ivory Coast"              "Côte d'Ivoire"

   " Azerbaijan"              "Azerbaijan"

   ;; Conjoin North Ireland on United Kingdom
   "North Ireland"            "United Kingdom"

   "East Timor"               "Timor-Leste"
   "São Tomé and Príncipe"    "Sao Tome and Principe"

   ;; Guernsey and Jersey form Channel Islands. Conjoin Guernsey on Jersey.
   ;; Jersey has higher population.
   ;; https://en.wikipedia.org/wiki/Channel_Islands
   "Guernsey and Jersey"      "Jersey"
   "Channel Islands"          "Jersey"

   "Caribbean Netherlands"    "Bonaire, Sint Eustatius and Saba"

   "Emirates"                 "United Arab Emirates"
   ;; "Bosnia–Herzegovina"       "Bosnia and Herzegovina"
   "Bosnia"                   "Bosnia and Herzegovina"
   "Dominican Rep"            "Dominican Republic"
   "Macedonia"                "North Macedonia"

   "occupied Palestinian territory" "Palestine, State of"
   "Korea, South"                   "Korea, Republic of"
   "Republic of Korea"              "Korea, Republic of"
   "Macao SAR"                      "Macao"
   "Iran (Islamic Republic of)"     "Iran, Islamic Republic of"
   "Cote d'Ivoire"                  "Côte d'Ivoire"
   "St. Martin"                     "Saint Martin (French part)"
   "Hong Kong SAR"                  "Hong Kong"
   "Reunion"                        "Réunion"

   ;; "Others" has no mapping
   ;; "Cruise Ship" is mapped to the default val

   })

(defn country-alias
  "Get a country alias or the normal name if an alias doesn't exist"
  [cc]
  (let [country (c/country-name (s/upper-case cc))]
    (get (conj (clojure.set/map-invert aliases-hm)
               ;; select desired aliases
               {"Moldova, Republic of"                  "Moldova"
                "Congo, the Democratic Republic of the" "DR Congo"
                "Palestine, State of"                   "State of Palestine"
                "Micronesia, Federated States of"       "Micronesia"
                "Taiwan, Province of China"             "Taiwan"})
         country country)))

(defn lower-case [hm]
  (->> hm
       (map (fn [[k v]] [(s/lower-case k) v]))
       (into {})))

(defn country_code
  "Return two letter country code (Alpha-2) according to
  https://en.wikipedia.org/wiki/ISO_3166-1
  Defaults to `c/default-2-country-code`."
  [country-name]
  (let [country (s/lower-case country-name)
        lcases-countries (lower-case country-country-code-hm)]
    (if-let [cc (get lcases-countries country)]
      cc
      (if-let [country-alias (get (lower-case aliases-hm) country)]
        (get country-country-code-hm country-alias)
        (do
          (println (format
                    "No country code found for \"%s\". Using \"%s\""
                    country-name
                    c/default-2-country-code
                    ))
          c/default-2-country-code)))))
