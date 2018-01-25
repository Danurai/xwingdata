(defproject xwingdata "0.1.0-SNAPSHOT"

	:description	"An XWing Data Viewer written with Clojure and Clojurescript."
	:url "https://danurai.github.io/xwingdata"
	:license {:name "GNU General Public License"
				:url "http://www.gnu.org/licenses/gpl.html"}
            
	:main xwingdata.system
   :min-lein-version	"2.7.0"
   :jar-name "xwingdata.jar"
   :uberjar-name "xwingdata-standalone.jar"
   
	:dependencies	[[org.clojure/clojure "1.8.0"]
                [org.clojure/clojurescript "1.9.946"]
                [environ "1.1.0"]
				    [http-kit "2.2.0"]
                [com.stuartsierra/component "0.3.2"]
					 [compojure "1.6.0"]
					 [reagent "0.7.0"]
					 [jarohen/chord "0.8.1"]
					 [org.clojure/core.async "0.3.465"]]
                
   :figwheel {:css-dirs ["resources/public/css"]} ;; watch and update CSS    
   
	:profiles {:uberjar {:aot :all}}
             :dev    {:plugins [[lein-cljsbuild "1.1.7"]
                              [lein-figwheel "0.5.14"]
                              [lein-autoexpect "1.9.0"]]
                      :dependencies [[reloaded.repl "0.2.4"]
                                   [expectations "2.2.0-rc3"]]
                      :source-paths ["dev"]
                      :env         {:port 9009}}}
                   
   :cljsbuild {:builds [{:id "dev"
                       :source-paths ["src" "dev"]
                       :figwheel true
                       :compiler {:output-to "target/classes/public/xwingapp.js"
                                 :output-dir "target/classes/public/out"
                                 :main "xwingdata.client"
                                 :asset-path "/out"
                                 :optimizations :none
                                 :recompile-dependents true
                                 :source-map true}}
                      {:id "min"
                       :source-paths ["src"]
                       :compiler {:output-to "resources/public/xwingapp.js"
                                 :main "xwingdata.client"
                                 :optimizations :advanced
                                 :pretty-print false}}]})
