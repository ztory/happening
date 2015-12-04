# Happening
Happening is an event hub with a focus on "namespacing" events to groups.

### Sender / Listener
A regular use case would be to listen on some global event, for example ONLINE_STATUS that would send events with `payload` equal to `true` or `false`. The code for implementing that would look something like this:
#### Sender
```java
//insert logic that will determine when to send this event
boolean isOnline = getOnlineStatus();
Happening.sendEvent(
    Happening.GROUP_ID_GLOBAL,
    "ONLINE_STATUS",
    isOnline
);
```
#### Listener
```java
HappeningListener globalListener = new HappeningListener(
        new RunObject() {
            @Override
            public Object r(Object o) {
                Boolean isOnline = (Boolean) o;
                //insert logic that will react on the isOnline Boolean
                return null;//optional return, its up to the sendEvent() caller to consume it
            }
        },
        "ONLINE_STATUS",
        Happening.GROUP_ID_GLOBAL//var arg below to specify groupIds that releases listener
).startListening();//chaining call to startListening immediately
```

### GroupIds
Each event is delivered to all listeners of that eventName AND that eventGroup. With the exception of listeners with `listenGroupId==Happening.GROUP_ID_GLOBAL` that will listen to ALL events with a matching eventName.
The beauty of groupIds are that if they are implemented correctly you should not have to care about releasing the listeners (the GC sends its thanks).

For example if you register this listener:
```java
MyActivityThatHasUniqueIntId activity = getMyActivity();//yes this is made up code
new HappeningListener(
        new RunObject() {
            @Override
            public Object r(Object o) {
                //what do you want to do when activity.onPause() is called?
                return null;
            }
        },
        "ON_PAUSE",//if you send a "ON_PAUSE" event from activity when onPause() is called
        activity.getHappeningGroupId()//here we are getting the int-id that is unique to activity
).startListening();
```

Then you dont have to care about releasing that listener, or any other listener that registers to release on `activity.getHappeningGroupId()` (if you only specify one int value in the HappeningListener constructor then that is used both for listening and releasing) as long as you release that groupId in the activity.onDestroy() like this:
```java
@Override
protected void onDestroy() {
    super.onDestroy();
    Happening.removeListeners(this.getHappeningGroupId());
}
```

This can be used in a similar way for all functionality that has a START/END lifecycle, for static usage then you can just register to listen on `Happening.GROUP_ID_GLOBAL` and be done with it! ;)

To obtain unique ids to be used with `Happening` you would call either `Happening.getUniqueActivityId()` or `Happening.getUniqueCustomGroupId()`, they will never clash, so you could use either.

If you have any problems or questions just create one of those [GitHub Issues](https://github.com/ztory/happening/issues)!

Happy coding! =]
