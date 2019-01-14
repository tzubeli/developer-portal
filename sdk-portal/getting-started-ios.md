# Kaltura Player SDK 3.0 (iOS) 

This guide will walk you through the steps for adding a Kaltura video player to your iOS mobile application. You'll learn how to import the SDK, find the necessary credentials, and load the player with your Entry ID of choice. **Because the player is focused on performance and giving you the simplest integration possible, it does not contain a UI.** That being said, this guide will show you how to listen to events in order to manage the player state, as well as examples for adding play/pause buttons and a slider to the player. 
Lastly, this guide will cover how to add plugins to the application, specifically the Kaltura Video Analytics plugin.

If you're looking for the Android guide, click here. 

## Before Your Begin

You'll need two things: 
1. Your Kaltura Partner ID, which can be found in the KMC under Settings>Integration Settings 
2. Any video entry, which can be found in the KMC as well. 

### Getting Started with the Kaltura Cocoa Pods

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

- The [Playkit Pod](https://cocoapods.org/pods/PlayKit) is made up of the core Player infrastructure 
- The [PlatKitProviders Pod](https://cocoapods.org/pods/PlayKitProviders) adds the Media Entry Providers, which are responsible from bringing in media data from Kaltura 

If you don't have a Podfile already, create a file in your Project directory called Podfile (no extension). Paste the code from above and then run `pod install` from the command line (in that directory). You might need to close and reopen xcode. 

## Add a Basic Kaltura Player 

The code below will cover a few functions needed in order to get the bare bones of a kaltura player, as well as a few additional steps regarding plugins and UI. 
> Reminder that the Kaltura Player does *not* include a UI, and the UI examples below are for instructional purposes only. 

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

### Create a Kaltura Session 

The Kaltura Sesssion is an authorization string that identifies the user watching the video. Including a Kaltura Session (KS) in the player allows for monitoring and analytics of the video, as well as the ability to restrict content access. The KS would generally be created on the server side of the application, and passed to the controller. 

```
var ks: String?
```

However, for the purpose of this guide, we will demonstrate how to create a KS on the client side using the Application Token API. An Application Token is used in cases where different applications with varying permissions need access to your Kaltura account, without using your Admin Secret. The appToken is created and customized by the account administrator, and then used by the developers to generate Kaltura Sessions for their respective applications. 

You can create an appToken with the [appToken.add](https://developer.kaltura.com/console/service/appToken/action/add) action. Once you've created it, hold on to its token and ID as you'll need those to create the session. You can also see a list of all available appTokens by using [appToken.list](https://developer.kaltura.com/console/service/appToken/action/list). 

There are a few steps to creating a KS with an appToken.
1. **Generate a basic kaltura session:** because all calls to the API *must* include a Kaltura Session, we first use the session API to create what is called a **widget session**, which has limited functionality and is used in the following steps 
2. **Create a Token Hash** of the appToken token and the widget session, combined. 
3. **Call the appToken.startSession API** with the widget session, the appToken ID, and the hash string. 
You can see all these steps interactively with [this workflow](https://developer.kaltura.com/workflows/Generate_API_Sessions/App_Token_Authentication) but examples below are written for client-side swift code. 

Let's get started. If you're already creating a Kaltura Session on the server side, you can skip these steps. 

**Step 1: Generate a widget session** 
To get a basic KS, we need to construct a URL request to the [session.startWidgetSession](https://developer.kaltura.com/api-docs/service/session/action/startWidgetSession) service. It needs your widget ID, which is basically just your partnerID with an underscore prefix. 
So let's create a function called `generateWidgetSession()` and form that URL. 

```
func generateWidgetSession() -> String {
    let widgetPartnerId = "_\(PARTNER_ID)"
    let widgetKsURL = NSString(format:"https://www.kaltura.com/api_v3/service/session/action/startWidgetSession?widgetId=%@&format=1",widgetPartnerId)
```

Call the endpoint and extract the `ks` string from the response. Your code should obviously include more error handling than this example. The complete function looks something like this: 

```

func generateWidgetSession() -> String {
    let widgetPartnerId = "_\(PARTNER_ID)"

    let widgetKsURL = NSString(format:"https://www.kaltura.com/api_v3/service/session/action/startWidgetSession?widgetId=%@&format=1",widgetPartnerId)

    let widgetKsData = try! Data(contentsOf: URL(string: widgetKsURL as String)!)

    let widgetKsDict = try! JSONSerialization.jsonObject(with: widgetKsData, options: []) as! [String:Any]

    return (widgetKsDict["ks"] as! String)
    }
```

We will call this function from another new function called `generateSession()` where you should call the create new variables for the widgetSession, appToken, the appToken ID, and the userId - which can be any string that identifies the user creating the session. 

```
func generateSession() {
    let widgetKs: String = generateWidgetSession()
    let appToken = "<TOKEN_OF_APP_TOKEN>"
    let appTokenId = "<ID_OF_APP_TOKEN>"
    let userId = "user"
```

**Step 2: Create the token hash**

You'll need to install and import a library of your choice for creating the hash string. We chose a library called [Arcane](https://cocoapods.org/pods/Arcane) which is like the Obj-C CommonCrypto library. Concatenate the widget session with the appToken token, and create the hash string. *Note that you must use the same Hash Type that you used to create the appToken.*

```
let tokenHash: String = Hash.SHA256("\(widgetKs)\(appToken)")!
```

**Step 3: Get the Kaltura Session** 

Once you have that hash string, you can now form the URL, make the call to [appToken.startSession](https://developer.kaltura.com/console/service/appToken/action/startSession), and extract the KS from the response. Again, a proper application should include error handling when making calls to the API. 
```
let URLString = NSString(format:"https://www.kaltura.com/api_v3/service/apptoken/action/startsession?ks=%@&userId=%@&id=%@&tokenHash=%@&format=1",widgetKs,userId,appTokenId,tokenHash)

let ksData = try! Data(contentsOf: URL(string: URLString as String)!)

let ksDict = try! JSONSerialization.jsonObject(with: ksData, options: []) as! [String:Any]
```
Lastly, set the application's `ks` to the newly generated KS:
```
self.ks = (ksDict["ks"] as! String)
```
The complete `generateSession()` function looks like this: 
```
func generateSession() {
    let appToken = "c9883312395bf5ed4fd5c9a5d86c985c"
    let userId = "avital.tzubeli@kaltura.com"
    let appTokenId = "0_xeu31jy5"

    let widgetKs: String = generateWidgetSession()

    let tokenHash: String = Hash.SHA256("\(widgetKs)\(appToken)")!

    let URLString = NSString(format:"https://www.kaltura.com/api_v3/service/apptoken/action/startsession?ks=%@&userId=%@&id=%@&tokenHash=%@&format=1",widgetKs,userId,appTokenId,tokenHash)

    let ksData = try! Data(contentsOf: URL(string: URLString as String)!)
    let ksDict = try! JSONSerialization.jsonObject(with: ksData, options: []) as! [String:Any]

    self.ks = (ksDict["ks"] as! String)
}
```
> Note that if an appToken is deleted, it can no longer be used for session creation. 

### Create the Player

Inside the class, below the `ks` declaration, add a declaration for the Player. 

```
var player: Player?
```

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
At this point, you should be able to successfully run the code and see your video player in the app. Your code would look like this: 

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

At this point you've probably noticed that we have no way of playing the video in the player. Start by adding button images of your choice to the Assets catalogue of your project. Then, in the storyboard, create a play/pause button, a slider (scrubber), a current position label, and a (remaining) duration label. For the play/pause button, in the Attributes Inspector, click the dropdown for Title and select Attributed. Under Image, type in the name of the play button file (it should have autocomplete for existing files). 
Lastly, create the outlets for all your new objects in the Controller: 

```
@IBOutlet weak var playPauseButton: UIButton!
@IBOutlet weak var playheadSlider: UISlider!
@IBOutlet weak var positionLabel: UILabel!
@IBOutlet weak var durationLabel: UILabel!
```

### Player State 

Currently our play button is only set to show a play icon, and we'll want it to change for different scenarios. What we'll need is to handle the state of what's happening in the player - whether it is idle, playing, paused, or ended. Let's add an enum called state at the top of the class:
```
enum State {
    case idle, playing, paused, ended
}
```
And a Property Observer on that enum which switches on each state.

```
var state: State = .idle {
      didSet {
          switch state {
          case .idle:
              playPauseButton.setImage(UIImage(named: "btn_play"), for: .normal)
          case .playing:
              playPauseButton.setImage(UIImage(named: "btn_pause"), for: .normal)
          case .paused:
              playPauseButton.setImage(UIImage(named: "btn_play"), for: .normal)
          case .ended:
              playPauseButton.setImage(UIImage(named: "btn_refresh"), for: .normal)
          }
        }
    }
```

What this does is listen for a change to the state, and based on whether the player is currently idle, paused, or playing, the picture of the play/pause button is changed accordingly. 

At the beginning of the `viewDidLoad` function, set the state to idle. 

`self.state = .idle` 

On the playPauseButton, add a new IBAction for a "Touch Up Inside" event and link it to a new `playerTouched` function that calls play/pause on the player when the button is touched. Available Basic Player actions can be found [here](https://kaltura.github.io/playkit/api/ios/core/Protocols/BasicPlayer.html) 

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

The third observer listens for player events, and updates the State when the player begins playing, is paused, or has ended, which is what the switch case above is dependent on. Player events can be found [here](https://kaltura.github.io/playkit/api/ios/core/Classes/PlayerEvent.html#/s:7PlayKit11PlayerEventC12StateChangedC)

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
              switch state {
              case .idle:
                  playPauseButton.setImage(UIImage(named: "btn_play"), for: .normal)
              case .playing:
                  playPauseButton.setImage(UIImage(named: "btn_pause"), for: .normal)
              case .paused:
                  playPauseButton.setImage(UIImage(named: "btn_play"), for: .normal)
              case .ended:
                  playPauseButton.setImage(UIImage(named: "btn_refresh"), for: .normal)
              }
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

The Kaltura playkit offers various modules for iOS that can be added to the player. Adding plugins is easy and requires little configuration. You can find a full list of available plugins [here](https://kaltura.github.io/playkit/). 

### Kava Plugin 

Probably the most important plugin is the KAVA plugin - Kaltura Video Analytics. It provides real time analytics for live and on-demand video. With historical, raw, or summarized data, it is easy to determine how, when, and where content was seen and shared by viewers. 

The [KAVA plugin](https://github.com/kaltura/playkit-ios-kava) is available through CocoaPods as "PlayKitKava". It was included in the Podfile at the beginning of the guide. To use the plugin, we'll need to import it, then register and configure it. 

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
