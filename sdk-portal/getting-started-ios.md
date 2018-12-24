# Getting Started with SDK 3.0 iOS

This guide will walk you through the steps for adding a Kaltura video player to your iOS mobile application.

## Before Your Begin

You'll need two things: 
1. Your Kaltura Partner ID, which can be found in the KMC under Settings>Integration Settings 
2. A video entry ID, which also can be found in the KMC, in your list of entries

### Pods 

You'll need to install a few Kaltura pods. Consider this sample Podfile 
```
source 'https://github.com/CocoaPods/Specs.git'

use_frameworks!

platform :ios, '9.0'

target 'OVPStarter' do
  pod 'PlayKit'
  pod 'PlayKitProviders'
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

### Create the Player

Inside the class, declare the kaltura session (ks) and the player. 

```
var ks: String?
var player: Player! 
```

> **Including a Kaltura Session in the player** allows for monitoring and analytics on your video, as well as the ability to restrict content access. The Kaltura Session should always be created on the server side. If you don't include a KS, the video can be viewed by anyone, and the viewers will be recorded as anonymous. 

Now lets create our video player. Head over to the Storyboard and create a new PlayerView of the desired size. Add a referencing outlet to your ViewController named playerContainer. Create a new function called `playerSetup` and set the player variable to equal the new playerContainer 

```
func setupPlayer() {
    self.player?.view = self.playerContainer
}
```

Now in the `viewDidLoad` function, load the player. We'll start without a pluginConfig, but we will cover adding plugins later in this guide. 

 ```
  self.player = try! PlayKitManager.shared.loadPlayer(pluginConfig: nil)
 ```
Next, call the newly created setupPlayer function. 
```
self.setupPlayer()
```

Now create and call a new function called `loadMedia`. 

```
self.loadMedia()
```

In the loadMedia function, you'll use the `SimpleSessionProvider` and `OVPMediaProvider` objects. 

```
let sessionProvider = SimpleSessionProvider(serverURL: SERVER_BASE_URL, partnerId: Int64(PARTNER_ID), ks: ks)
let mediaProvider: OVPMediaProvider = OVPMediaProvider(sessionProvider)
```

Now set your entry ID on that `mediaProvider`

```
mediaProvider.entryId = ENTRY_ID
```

Load the media by creating a `MediaConfig` with a video start time of zero seconds, and then passing that config to `player.prepare()`

```
mediaProvider.loadMedia { (mediaEntry, error) in
    if let me = mediaEntry, error == nil {
    
        let mediaConfig = MediaConfig(mediaEntry: me, startTime: 0.0)

        self.player.prepare(mediaConfig)
    }
}
```

The `loadMedia` function should now look like this: 

```
func loadMedia() {

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
At this point, you should be able to successfully run the code and see your video player in the app. So far, your code should look like this: 

```
import UIKit
import PlayKit
import PlayKitUtils
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

        self.player = try! PlayKitManager.shared.loadPlayer(pluginConfig: createPluginConfig())
        
        self.setupPlayer()
        self.loadMedia()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    func setupPlayer() {
        self.player?.view = self.playerContainer
    }
    
    func loadMedia() {
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

At this point you've probably noticed that we have no way of playing the video in the player. Start by creating a play/pause button, a slider (scrubber), a current position label, and a (reamaining) duration label. 

```
    @IBOutlet weak var playPauseButton: UIButton!
    @IBOutlet weak var playheadSlider: UISlider!
    @IBOutlet weak var positionLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
```

What we'll need is to handle the state of what's happening in the player. 

### Analytics Plugin 

There is a wide variety of plugins available for the player, which can be found here. We will implement the KAVA plugin, which stands for Kaltura Video Analytics. 
