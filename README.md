# A online programmer forum
## brief introduction
I want to build a forum that every programmer can share their knowledge and technologies.

In this forum, you can not only write blogs, but also you can read others' blogs and like them, share them, comment them. Even you can send private message to someone else.

## some tech
* Everyone can register to this website. Users can write blogs, like or comment on others’ blogs, and follow other users.
* This project is built on the Spring Boot & SSM framework, and unified state management, transaction management, exception handling.
* Redis is used to realize the like and follow functions, and a single machine can reach 5,000TPS; using HyperLogLog and Bitmap to realize the statistical function of UV and DAU respectively, only 40M memory space is needed for 1 million user data.
* Use Kafka to realize asynchronous installation notification, a single machine can reach 7,000TPS.
* Implemented full-text search function using ElasticSearch, which can accurately match search results and highlight keywords.
## how to use it
```
// pull the code to your computer
https://github.com/back2childhood/online-pragrammer-forum.git
// open the code with idea and run it
// open the browser and enter 
http://www.localhost:8080/myCommunity/index
// now, you can see the homepage of this project
```
## core functions and technologies

## TODO
* the unread notifications' hint can't be removed
* use Spring Security to implement the function of delete/perfect/top posts and the right control.
* use Elasticsearch to implement the function of search any posts and highlight them keywords.
* allow users to customize their nickname
* allow users to modify/delete their own posts
* 