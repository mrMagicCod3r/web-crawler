#### About

Program outputs simple sitemap in a tree manner - meaning crawler descends only into children of given page based on url structure.
That of course omits some links but wanted to keep it simple - for instance links that are not "children" of given url and are not accessible from top navigation in any other way. 

Written in Java.
Crawler is multithreaded using classic thread pool approach.
Added tests for all important logic.


Possible optimisations:
* Async I/O - did some experiments with 2 async clients but resource utilisation was quite similar so for simplicity used classic approach (separate thread pool for processing downloaded pages would still be needed).  
* Url harvesting - used JSoup which builds whole DOM, possibly could be faster with some manual text parsing.

#### How to build and run
Only docker is required to build and run the application. 

To build run following command in project root:

~~~~
docker build -t web-crawler .
~~~~

Running the application:
~~~~
docker run -it web-crawler https://www.revolut.com
~~~~

Output more logs (visited pages, errors):
~~~~
docker run -it -e LOG_LEVEL=debug web-crawler https://www.revolut.com
~~~~