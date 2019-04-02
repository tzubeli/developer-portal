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

#### Template Entry 

The template entry should contain all metadata you want included in entries that are created for each event recording. Using [entry.add](https://developer.kaltura.com/console/service/media/action/add), you have the ability to set things like categories, creator ID, or tags:

```php
$entry = new KalturaMediaEntry();
$entry->mediaType = KalturaMediaType::VIDEO;
$entry->name = "MATH-123";
$entry->tags = "mathematics, kaltura";
$entry->categoriesIds = "86105441";

$result = $client->media->add($entry);
```

Once you've created that entry, hold on to its ID. You can always look it up again with [media.list](https://developer.kaltura.com/console/service/media/action/list)

#### Schedule Event 

You'll create a new scheduled recording event of with the ID of the template entry. This ensures that all entries associated with this specific event recording contain all information set on the entry. 

This is also where you set the actual scheduling of the specific event. You can set Start Time and Duration, or Start Time and End Time if that's easier. You can also set recurrence on the event, for events that happen daily or weekly, or select specific days of the week and then the amount of times the event will happen. 
For example, a math class that takes place on Mondays, Wednesdays, and Fridays, and happens forty times during a semester that starts on a Wednesday.



```php
$schedulePlugin = KalturaScheduleClientPlugin::get($client);
$scheduleEvent = new KalturaRecordScheduleEvent();
$scheduleEvent->templateEntryId = "1_erl8rgx8";
$scheduleEvent->classificationType = KalturaScheduleEventClassificationType::PUBLIC_EVENT;
$scheduleEvent->recurrenceType = KalturaScheduleEventRecurrenceType::RECURRING;
$scheduleEvent->recurrence = new KalturaScheduleEventRecurrence();
$scheduleEvent->recurrence->byDay = "MO, WE, FR";
$scheduleEvent->recurrence->frequency = KalturaScheduleEventRecurrenceFrequency::DAILY;
$scheduleEvent->recurrence->count = 40;
$scheduleEvent->recurrence->weekStartDay = KalturaScheduleEventRecurrenceDay::WEDNESDAY;
  
$result = $schedulePlugin->scheduleEvent->add($scheduleEvent);
```

Other details you can add for personal reference are `location` or `entryIds` which is for any related entries or similar recordings that might exist (like the math class that happens on Tuesdays and Thursdays, for example). 

#### Schedule Resource 

The schedule resource is a reference object for the camera/encoder object. Creating the resource will produce an ID, which is how the encoder checks if there are any upcoming events to record. 

```php 
$schedulePlugin = KalturaScheduleClientPlugin::get($client);
$scheduleResource = new KalturaCameraScheduleResource();
$scheduleResource->description = "Camera in Classroom #702";
$scheduleResource->name = "EXTRON-100-ABC";
  
$result = $schedulePlugin->scheduleResource->add($scheduleResource);
```

Note that if you've already added the camera for this location in Mediaspace, you dont need to do it again, but you will need the resource ID.

#### Schedule Event Resource 

This is where you pair the event with the recording resource so that the correct camera is used. 

scheduleEventResource to connect the event to the resource, using the scheduleEvent ID (which you can find by listing events) and the resource ID.
Lastly, the encoder needs to determine which events are scheduled for it, which you can do by calling scheduleEvent.list and filtering by resource ID. Which means the encoder needs to know its own ID.
 
