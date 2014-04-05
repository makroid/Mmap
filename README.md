General
=======

Mmap is a simple android app that can display routes generated with [TrainingSchedule](https://github.com/makroid/TrainSchedule). Given you have prepared a route at home with some markers to  indicate the right directions, you can use this app during the tour to check where you are. 
As each marker has a rank, you can use this number to find it on the map.

Functionality
-------------
Basically everything is controlled by typing in commands:
* `load`: load a route file
* `select`: select a route (if several are in the route file)
* `bound`: bound current route
* `gps`, `gpsoff`: switch on/off gps to find your current position
* `goto <int>`, or just `<int>`: goto a maker number `<int>`
* `sat`, `map`: change map type to satellite/normal

Screenshot
----------
![Mmap screenshot](http://i.imgur.com/1sea4qi.jpg?1)


Requirement
-----------
An android device that can run google maps api v2

