# Getting Started with Kaltura Recording Scheduler 

You may find yourself recording an event that repeats itself daily, or weekly, for example a math class. In this case, the Kaltura Recording Scheduler comes in handy. 
It allows you to set the time and date, and possibly recurrance, of a particular event, which will then be automatically ingested to Kaltura with associated metadata that you've pre-defined. 

**Note that this feature is not enabled by default. Speak with your Account Manager to have it set up.**

### Creating an event with MediaSpace

You can schedule an event easily using MediaSpace, by selecting the user dropdown at the top right, and choosing "My Schedule." There you have an option to create a new event, and view/edit all the existing ones. 

So what's happening behind the scenes when you create a new scheduled event?

First, a new entry is created, containing all the entry data you inputted (name, description, organizer). You'll be able to see in the KMC - just an entry with no media attached to it. 
That entry is essentially the "template entry",  which is used for the recordings that are uploaded once the event is complete. The title may differ based on your encoder's configurations, but for the most part, all scheduled event entries will look the same. 
That entry is used to create a Scheduled Event, which includes all information about the event: start time, duration, recurrance, relevant days of the week, etc. 
is created. That event is then linked with the recording resource that you've configured, and all of this information is sent to your events calendar. 

### Creating an event with the Kaltura API  

In order to automate the process, you might want to create events using the API directly. This is a multi-step process that works only if all steps are completed correctly. 

