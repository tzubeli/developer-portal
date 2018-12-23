# Getting Started with SDK 3.0 iOS

This guide will walk you through the steps for adding a Kaltura video player to your iOS mobile application. If you are looking for OTT functionality, switch to this doc instead. 

## Before Your Begin

You'll need two things: 
1. Your Kaltura Partner ID, which can be found in the KMC under Settings>Integration Settings 
2. The entry ID of your video, which also can be found in the KMC, in your list of entries

### Pods 

You'll need to install a few Kaltura pods. Consider this sample Podfile 
```
source 'https://github.com/CocoaPods/Specs.git'

use_frameworks!

platform :ios, '9.0'

target 'OVPStarter' do
  pod 'PlayKit'
  pod 'PlayKitProviders'
  pod 'PlayKitKava'
end
```

## Adding a Basic Kaltura Player 

The code below all takes place in your View Controller. It will cover a few  functions needed in order to get the bare bones of a kaltura player, as well as a few additional steps regarding plugins and UI. 
Note that the Kaltura Player does *not* include a UI, and the UI examples below are for instructional purposes only. 

### Setting Up 

Import the relevant Kaltura Libraries 

```
import UIKit
import PlayKit
import PlayKitUtils
import PlayKitKava
import PlayKitProviders
```

Set your partner ID and entry ID 

```
fileprivate let SERVER_BASE_URL = "https://cdnapisec.kaltura.com"
fileprivate let PARTNER_ID = 0000000
fileprivate let ENTRY_ID = "1_abcjks"
```

## Adding a Kaltura Session to your video 

In most cases, your video will be viewed anonymously. But if you want to have content access settings on your videos, you'll need to pass a Kaltura Session, or KS, to the player. This KS should be created on the server side 

