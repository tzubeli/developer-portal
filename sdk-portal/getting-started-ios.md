# Getting Started with SDK 3.0 iOS

This guide will walk you through the steps for adding a Kaltura video player to your iOS mobile application.

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

## Add a Basic Kaltura Player 

The code below will cover a few functions needed in order to get the bare bones of a kaltura player, as well as a few additional steps regarding plugins and UI. 
Note that the Kaltura Player does *not* include a UI, and the UI examples below are for instructional purposes only. 

### Setting Up 

In the Controller, import the relevant Kaltura Libraries 

```
import UIKit
import PlayKit
import PlayKitUtils
import PlayKitKava
import PlayKitProviders
```

Set your partner ID and entry ID 

```
let SERVER_BASE_URL = "https://cdnapisec.kaltura.com"
let PARTNER_ID = 0000000
let ENTRY_ID = "1_abc6st"
```

### Set Up the Player

You'll first declare the ks (kaltura session) and the player

```
var ks: String?
var player: Player! 
```

In the `viewDidLoad` function, load the player. We'll start without a pluginConfig, but we'll cover it in a couple of steps. 

 ```
  self.player = try! PlayKitManager.shared.loadPlayer(pluginConfig: nil)
 ```
Now create and call a new function called setupPlayer. 

```
self.setupPlayer()
```

In the setupPlayer function, you'll need the `SimpleSessionProvider` and `OVPMediaProvider` objects. 

```
let sessionProvider = SimpleSessionProvider(serverURL: SERVER_BASE_URL, partnerId: Int64(PARTNER_ID), ks: ks)
let mediaProvider: OVPMediaProvider = OVPMediaProvider(sessionProvider)
```

> **Including a Kaltura Session in the player** allows for monitoring and analytics on your video, as well as the ability to restrict content access. The Kaltura Session should always be created on the server side. If you don't include a KS, the video can be viewed by anyone, and the viewers will be recorded as anonymous. 

Now set your entry ID on the `mediaProvider`

```
mediaProvider.entryId = ENTRY_ID
```

Load that media by creating a `MediaConfig` with a video start time of zero seconds, and then passing that config to `player.prepare()`

```
mediaProvider.loadMedia { (mediaEntry, error) in
    if let me = mediaEntry, error == nil {
    
        let mediaConfig = MediaConfig(mediaEntry: me, startTime: 0.0)

        self.player.prepare(mediaConfig)
    }
}
```

The setupPlayer() function should now look like this: 

```
func setupPlayer() {

    let sessionProvider = SimpleSessionProvider(serverURL: SERVER_BASE_URL, partnerId: Int64(PARTNER_ID), ks: ks)

    let mediaProvider: OVPMediaProvider = OVPMediaProvider(sessionProvider)

    mediaProvider.entryId = ENTRY_ID

    mediaProvider.loadMedia { (mediaEntry, error) in
        if let me = mediaEntry, error == nil {

            let mediaConfig = MediaConfig(mediaEntry: me, startTime: 0.0)

            self.player.prepare(mediaConfig)

        }
    }
}
```

We're missing on last thing: the player container. Head over to the Storyboard. Create a new PlayerView, and drag to get your desired player size. Name it playerContainer. There should be a new outlet in the ViewController. 

At the beginning of the setupPlayer() function, set the player to equal the new playerContainer. 
```
self.player?.view = self.playerContainer
```
At this point, you should be able to successfully run the code and see your video player in the app. So far, your code should look like this: 

```
import UIKit
import PlayKit
import PlayKitUtils
import PlayKitKava
import PlayKitProviders

let SERVER_BASE_URL = "https://cdnapisec.kaltura.com"
let PARTNER_ID = 1424501
let ENTRY_ID = "1_djnefl4e"

class ViewController: UIViewController {
    
    var ks: String?
    var player: Player!
    
    @IBOutlet weak var playerContainer: PlayerView!
    
    override func viewDidLoad() {
        
        super.viewDidLoad()

        self.player = try! PlayKitManager.shared.loadPlayer(pluginConfig:nil)
        
        self.setupPlayer()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func setupPlayer() {
        
        self.player?.view = self.playerContainer
        
        let sessionProvider = SimpleSessionProvider(serverURL: SERVER_BASE_URL, partnerId: Int64(PARTNER_ID), ks: ks)
        
        let mediaProvider: OVPMediaProvider = OVPMediaProvider(sessionProvider)
        
        mediaProvider.entryId = ENTRY_ID
        
        mediaProvider.loadMedia { (mediaEntry, error) in
            if let me = mediaEntry, error == nil {
                
                let mediaConfig = MediaConfig(mediaEntry: me, startTime: 0.0)
    
                self.player.prepare(mediaConfig)
                
            }
        }
    }
}

```

### Add Buttons and Controls 

As you've noticed, you're unable to play or pause the video. This is where controls come in. 
