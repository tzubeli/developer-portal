# Kaltura Player SDK 3.0 (iOS) 

One of the key challenges in creating video applications is in delivering a great playback experience to the user through a video player. If you're thinking about building video into your mobile application, the Kaltura Mobile Video SDKs provide the framework and tools to help you easily embed the Kaltura Video Player into native environments in your applications.The Player  is fully native and offers excellent performance, ensuring that your users receive the best vieweing experience. 

The Player's architecture is designed to allow an easy and seamless integration experience with just a few lines of code, enabling you to connect multiple playback engines and platforms. The Kaltura Video Player wraps the playback engine with the same interface and events, thereby allowing the same plugin code to work across multiple platforms, including iOS, Android, and web.

The Kaltura Video Player wraps the playback engine with the same interface and events, thus allowing the same plugin code to work across multiple platforms, including iOS, Android, and web. Each platform supports different types of streaming capabilites and DRMs. The Kaltura Video Player’s technology determines the best streaming delivery method and DRM as needed. 

The Player API includes various plugins that can be easily integrated, such as the video analytics plugin, which supports sending notifications to the Player and listening to events, giving you better insighs on who is watching the videos and how they are being shared. 
The Kaltura Player SDK eliminates all of the headache around implementing video, giving you the time to focus on everything else involved in your mobile application. 

This guide will walk you through the steps for adding a Kaltura video player to your iOS mobile application. You'll learn how to import the SDK, find the necessary credentials, and load the player with your Entry ID of choice. **Because the player is focused on performance and giving you the simplest integration possible, it does not contain a UI.** That being said, this guide will show you how to listen to events in order to manage the player state, as well as examples for adding play/pause buttons and a slider to the player. 
Lastly, this guide will cover how to add plugins to the application, specifically the Kaltura Video Analytics plugin.

If you're looking for the Android guide, click here. 

## Before Your Begin

You'll need two things: 
1. Your Kaltura Partner ID, which can be found in the KMC under Settings>Integration Settings 
2. Any video entry, which can be found in the KMC as well. 

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
var player: Player?
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
                
                if let player = self.player {
                    player.prepare(mediaConfig)
                }
            }
        }
    }
}

```

### Add Buttons and Controls 

At this point you've probably noticed that we have no way of playing the video in the player. Start by creating a play/pause button, a slider (scrubber), a current position label, and a (reamaining) duration label. You can put the play/pause button to the left of the slider, where it generally goes, or directly on top of the player (or both!)

```
    @IBOutlet weak var playPauseButton: UIButton!
    @IBOutlet weak var playheadSlider: UISlider!
    @IBOutlet weak var positionLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
```

### Player State 

What we'll need now is to handle the state of what's happening in the player - whether it is idle, playing, paused, or ended. So we'll add an enum called state at the top of the class:
```
enum State {
    case idle, playing, paused, ended
}
```
as well as a Property Observer on that enum which switches on each state:

```
var state: State = .idle {
    didSet {
        let title: String
        switch state {
        case .idle:
            title = "|>"
        case .playing:
            title = "||"
        case .paused:
            title = "|>"
        case .ended:
            title = "<>"
        }
        playPauseButton.setTitle(title, for: .normal)
    }
}
```

What this does is listen for a change to the state variable, and set the title accordingly on the play/pause button. In a proper application, you'd set the SVG of choice for the play/pause/repeat buttons, but for the purpose of understanding this example we'll use text. 

At the beginning of the `viewDidLoad` function, set the state to `idle.`
`self.state = .idle` 

On the playPauseButton, add a new IBAction for a "Touch Up Inside" event and link it to a new `playerTouched` function that switches the state when the play/pause button is touched. 

```
    @IBAction func playTouched(_ sender: Any) {
        guard let player = self.player else {
            print("player is not set")
            return
        }
        
        switch state {
        case .playing:
            player.pause()
        case .idle:
            player.play()
        case .paused:
            player.play()
        case .ended:
            player.seek(to: 0)
            player.play()
        }
    }
```

### Player Slider 

The slider is made up of a few components: the playhead, the current time stamp, and the duration of the entry. All of this configuration happens in the `setupPlayer` function. Firstly, a formatter for displaying the number of seconds as `HH:MM:SS` 

```
let formatter = DateComponentsFormatter()
formatter.allowedUnits = [.hour, .minute, .second]
formatter.unitsStyle = .positional
formatter.zeroFormattingBehavior = .pad

func format(_ time: TimeInterval) -> String {
    if let s = formatter.string(from: time) {
        return s.count > 7 ? s : "0" + s
    } else {
        return "00:00:00"
    }
}
```

Then, we'll add three observers. The first one checks on the media's progress in the task queue every fifth of a second and then updates the player slider accordingly, as well as the text of the current position label (using the time formatter)

```
self.player?.addPeriodicObserver(interval: 0.2, observeOn: DispatchQueue.main, using: { (pos) in
    self.playheadSlider.value = Float(pos)
    self.positionLabel.text = format(pos)
})
```
The second observer waits for Player event `durationChanged` which happens when the media loads and the duration of the video is known. This happens once per playback. It then sets the maximum value of the slider playhead, and the text of the duration label. 

```
self.player?.addObserver(self, events: [PlayerEvent.durationChanged], block: { (event) in
    if let e = event as? PlayerEvent.DurationChanged, let d = e.duration as? TimeInterval {
        self.playheadSlider.maximumValue = Float(d)
        self.durationLabel.text = format(d)
    }
})
```        

The third observer listens for player events, and updates the State when the player begins playing, is paused, or has ended. 

```
self.player?.addObserver(self, events: [PlayerEvent.play, PlayerEvent.ended, PlayerEvent.pause], block: { (event) in
    switch event {
    case is PlayerEvent.Play, is PlayerEvent.Playing:
        self.state = .playing

    case is PlayerEvent.Pause:
        self.state = .paused

    case is PlayerEvent.Ended:
        self.state = .ended

    default:
        break
    }
})
```

Lastly, in the Storyboard, set a new referencing action on the Playhead Slider for the "Value Changed" event. Call it `playheadValueChanged`. Add it after the `playTouched` function. It should set the current time position according to the value of the palyhead, and change the State to paused in the case of moving the slider back after the video has ended. 

```
@IBAction func playheadValueChanged(_ sender: Any) {
    guard let player = self.player else {
        print("player is not set")
        return
    }

    if state == .ended && playheadSlider.value < playheadSlider.maximumValue {
        state = .paused
    }
    player.currentTime = TimeInterval(playheadSlider.value)
}
```
You should be able to run the code, and use the basic player functions. Thec complete code should look like this: 

```
import UIKit
import PlayKit
import PlayKitUtils
import PlayKitProviders

fileprivate let SERVER_BASE_URL = "https://cdnapisec.kaltura.com"
fileprivate let PARTNER_ID = 1424501
fileprivate let ENTRY_ID = "1_djnefl4e"

class ViewController: UIViewController {

    enum State {
        case idle, playing, paused, ended
    }
    
    var entryId: String?
    var ks: String?
    var player: Player? // Created in viewDidLoad
    var state: State = .idle {
        didSet {
            let title: String
            switch state {
            case .idle:
                title = "|>"
            case .playing:
                title = "||"
            case .paused:
                title = "|>"
            case .ended:
                title = "<>"
            }
            playPauseButton.setTitle(title, for: .normal)
        }
    }
    
    @IBOutlet weak var playerContainer: PlayerView!
    @IBOutlet weak var playPauseButton: UIButton!
    @IBOutlet weak var playheadSlider: UISlider!
    @IBOutlet weak var positionLabel: UILabel!
    @IBOutlet weak var durationLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.state = .idle

        self.player = try! PlayKitManager.shared.loadPlayer(pluginConfig: nil)
        self.setupPlayer()
        
        entryId = ENTRY_ID
        self.loadMedia()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    override var preferredStatusBarStyle : UIStatusBarStyle {
        return UIStatusBarStyle.lightContent
    }
    
/************************/
// MARK: - Player Setup
/***********************/
    func setupPlayer() {
        
        self.player?.view = self.playerContainer
        
        let formatter = DateComponentsFormatter()
        formatter.allowedUnits = [.hour, .minute, .second]
        formatter.unitsStyle = .positional
        formatter.zeroFormattingBehavior = .pad
        
        func format(_ time: TimeInterval) -> String {
            if let s = formatter.string(from: time) {
                return s.count > 7 ? s : "0" + s
            } else {
                return "00:00:00"
            }
        }

        // Observe media progress
        self.player?.addPeriodicObserver(interval: 0.2, observeOn: DispatchQueue.main, using: { (pos) in
            self.playheadSlider.value = Float(pos)
            self.positionLabel.text = format(pos)
        })
        
        // Observe duration
        self.player?.addObserver(self, events: [PlayerEvent.durationChanged], block: { (event) in
            if let e = event as? PlayerEvent.DurationChanged, let d = e.duration as? TimeInterval {
                self.playheadSlider.maximumValue = Float(d)
                self.durationLabel.text = format(d)
            }
        })

        // Observe play/pause
        self.player?.addObserver(self, events: [PlayerEvent.play, PlayerEvent.ended, PlayerEvent.pause], block: { (event) in
            switch event {
            case is PlayerEvent.Play, is PlayerEvent.Playing:
                self.state = .playing
                
            case is PlayerEvent.Pause:
                self.state = .paused
                
            case is PlayerEvent.Ended:
                self.state = .ended
                
            default:
                break
            }
        })
    }

    func loadMedia() {
        let sessionProvider = SimpleSessionProvider(serverURL: SERVER_BASE_URL, partnerId: Int64(PARTNER_ID), ks: ks)
        let mediaProvider: OVPMediaProvider = OVPMediaProvider(sessionProvider)
        mediaProvider.entryId = entryId
        mediaProvider.loadMedia { (mediaEntry, error) in
            if let me = mediaEntry, error == nil {
                let mediaConfig = MediaConfig(mediaEntry: me, startTime: 0.0)
                if let player = self.player {
                    player.prepare(mediaConfig)
                }
            }
        }
    }
    
/************************/
// MARK: - Actions
/***********************/
    
    @IBAction func playTouched(_ sender: Any) {
        guard let player = self.player else {
            print("player is not set")
            return
        }
        
        switch state {
        case .playing:
            player.pause()
        case .idle:
            player.play()
        case .paused:
            player.play()
        case .ended:
            player.seek(to: 0)
            player.play()
        }
    }
    
    @IBAction func playheadValueChanged(_ sender: Any) {
        guard let player = self.player else {
            print("player is not set")
            return
        }
        
        if state == .ended && playheadSlider.value < playheadSlider.maximumValue {
            state = .paused
        }
        player.currentTime = TimeInterval(playheadSlider.value)
    }
}

```

## Plugins 

The Kaltura playkit offers various modules for iOS that can be added to the player. Adding plugins is easy and requires little configuration. You can find a full list of available plugins here. 

### Kava Plugin 

Probably the most important plugin is the KAVA plugin - Kaltura Video Analytics. It provides real time analytics for live and on-demand video. With historical, raw, or summarized data, it is easy to determine how, when, and where content was seen and shared by viewers. 

The KAVA plugin is available through CocoaPods as "PlayKitKava". It was included in the Podfile at the beginning of the guide. To use the plugin, we'll need to import it, then register and configure it. 

```
import PlayKitKava
```

Begin with a function that creates the KAVA plugin. It requires the Partner ID, the entry ID, and the KS, which is what identifies the user. The rest of the arguments are optional. Full documentation can be found here. 

```
func createKavaConfig() -> KavaPluginConfig {
    return KavaPluginConfig(partnerId: PARTNER_ID, entryId: entryId, ks: ks, playbackContext: nil, referrer: nil, applicationVersion: nil, playlistId: nil, customVar1: nil, customVar2: nil, customVar3: nil)
}
```

Add that plugin to the player in the `loadMedia` function by calling `player.updatePluginConfig`. This should be included before the `player.prepare`.

```
player.updatePluginConfig(pluginName: KavaPlugin.pluginName, config: self.createKavaConfig())
```

Next, you need a function that manages all the plugins you might want to add to the player. In our case, it will return the function we just created. 

```
func createPluginConfig() -> PluginConfig? {
    return PluginConfig(config: [KavaPlugin.pluginName: createKavaConfig()])
}
```

Lastly, we'll pass that function instead of `nil` to the loadPlayer call in `viewDidLoad`:

```
self.player = try! PlayKitManager.shared.loadPlayer(pluginConfig: createPluginConfig())
```

The KAVA plugin is now included in the player, and all data about plays and shares can be viewed in the KMC or retrieved using the Kaltura Reporting API. 
