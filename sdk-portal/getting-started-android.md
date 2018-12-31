# Kaltura Player SDK 3.0 (Android) 

One of the key challenges in creating video applications is in delivering a great playback experience to the user through a video player. If you're thinking about building video into your mobile application, the Kaltura Mobile Video SDKs provide the framework and tools to help you easily embed the Kaltura Video Player into native environments in your applications.The Player  is fully native and offers excellent performance, ensuring that your users receive the best vieweing experience. 

The Player's architecture is designed to allow an easy and seamless integration experience with just a few lines of code, enabling you to connect multiple playback engines and platforms. The Kaltura Video Player wraps the playback engine with the same interface and events, thereby allowing the same plugin code to work across multiple platforms, including iOS, Android, and web.

The Kaltura Video Player wraps the playback engine with the same interface and events, thereby allowing the same plugin code to work across multiple platforms, including iOS, Android, and web. Each platform supports different types of streaming capabilites and DRMs. The Kaltura Video Player’s technology determines the best streaming delivery method and DRM as needed. 

The Player API includes various plugins that can be easily integrated, such as the video analytics plugin, which supports sending notifications to the Player and listening to events, giving you better insighs on who is watching the videos and how they are being shared. 
The Kaltura Player SDK eliminates all of the headache around implementing video, giving you the time to focus on everything else involved in your mobile application. 

This guide will walk you through the steps for adding a Kaltura video player to your iOS mobile application. You'll learn how to import the SDK, find the necessary credentials, and load the player with your Entry ID of choice. **Because the player is focused on performance and giving you the simplest integration possible, it does not contain a UI.** That being said, this guide will show you how to listen to events in order to manage the player state, as well as examples for adding play/pause buttons and a slider to the player. 
Lastly, this guide will cover how to add plugins to the application, specifically the Kaltura Video Analytics plugin.

If you're looking for the iOS guide, click here. 

## Before Your Begin

You'll need two things: 
1. Your Kaltura Partner ID, which can be found in the KMC under Settings>Integration Settings 
2. Any video entry, which can be found in the KMC as well. 
