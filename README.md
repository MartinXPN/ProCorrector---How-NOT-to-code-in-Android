# ProCorrector - How NOT to code in Android

New and better version of this project is available here: https://github.com/MartinXPN/SpellNote.

ProCorrector was my first android project with a terribly written codebse and several mistakes that a lot of people do while
just starting to learn programming in Java and Android. This project can be a guide for someone who just starts to learn 
Android and wants to know what type of issues he may face and what common mistakes he shouldn't do while designing the project.
Here are listed several issues present in the project:

### 1. Think the Java way while coding in Java
Before this "monster" I was mostly writing code in
C++ for solving competitive-programming problems like in [Spoj](http://www.spoj.com/) or [Codeforces](http://codeforces.com/), 
and having a C++ background I didn't have enough experience of working with Java and the codebase of ProCorrector can be 
described as a C++ logic written in Java. There are a lot of static helper functions, methods that were already present in java
but because of not knowing about their existance I implemented them once again (like string splitting or string 
[matching](https://github.com/MartinXPN/ProCorrector---How-NOT-to-code-in-Android/blob/master/app/src/main/java/com/ProCorrector/XPN/procorrector/Language.java#L341)).
This increases the size of code without any good consequence on performance or structure making it less maintainable.


### 2. Choose some paradigm for a big project
The code of ProCorrector lacks any kind of design pattern or a paradigm. One of the bast paradigms known to me for app 
development are [MVC](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) or 
[MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel). 
Those come handy especially when the project gets big pretty quickly and you need some good
way of organizing your logic. Also MVC and MVVM design patterns make your code more testable by decoupling views from 
the logic that controls them.


### 3. Decouple logic, services, views (avoid treating Activity as a God object)
A lot of people while writing their first Android apps put a huge amount of logic inside activity classes. 
I wasn't an exception. One of the activity [classes](https://github.com/MartinXPN/ProCorrector---How-NOT-to-code-in-Android/blob/master/app/src/main/java/com/ProCorrector/XPN/procorrector/EditCorrectText.java)
in ProCorrector contained more than 1000 lines of code. This kind of approach makes it almost impossible to change the code
quickly without breaking the whole thing. Any minor change could lead to epic failures because almost everything was interconnected.

The solution is to keep views (Activities, Fragments, etc) separate from services(classes that interact with network requests
or databases) and Controllers/ViewModels (classes glue logic and views together).


### 4. Use ORM while working with a database
That was my first time working with an SQL database, therefore not having enough information about current best practices
I made several mistakes. First of all, I was keeping data that was queried quite a lot without any kind of index. That 
tremedously slowed down time for a simple query. Secondly,
I was generating queries as strings right inside the code. That made my code not very readable while increasing the risk of 
messing up something while working with strings (for performance reasons I was using StringBuilders that are not thread-safe 
while working with threads).

A better way would be to choose a good ORM library that can satisfy all your needs and prceed with it. OrmLite, SugarORM, and
Realm are currently considered as one of the bests. I've used Realm in my other project.

#
Despite having this many issues this project was downloaded more than 100.000 times while being in play store for about a year.
