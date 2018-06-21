# xwingdata

View Xwing 1.0 upgrade cards
Card data requested using taoensso/sente over websocket, sente runs a keepalive ping

## Overview

FIXME: Write a paragraph about the library/project and highlight its goals.

## Setup

Set up a repl, run the server and start figwheel monitoring

    lein repl
    (ns user)
    (reset)
    (fig-start)
    
open your browser at [localhost:9009](http://localhost:9009/).

To clean all compiled files:

    lein clean

To create a production build run:

    lein do clean, cljsbuild once min

And open your browser in `resources/public/index.html`. You will not
get live reloading, nor a REPL. 

## License

Copyright © 2014 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
