# Getting Started 
 
This guide will enable you to quickly and easily get started with building your own video experiences and exploring the platform’s basic capabilities.
 
## Before You begin
 
You will need your Kaltura account credentials. If you don’t have them yet, start a [free trial((https://vpaas.kaltura.com/register).
If you’ve signed in, you can click on your account info at the top right of this page to view your credentials.
You can also find them at any time in the KMC's (Kaltura Management Console) by clicking the [Integration Settings tab](https://kmc.kaltura.com/index.php/kmcng/settings/integrationSettings).
 
The simplest way to make requests to the Kaltura REST API is by using one of the [Kaltura API Client Libraries](https://developer.kaltura.com/api-docs/Client_Libraries/). We don’t recommend making REST API requests directly, as your URL requests might get really long and tricky. 
 
Once you’ve downloaded the client library, you'll need to import the library and instantiate a KalturaClient object with which you'll make calls to the API. 
Setup looks like this:

```python
from KalturaClient import *

config = KalturaConfiguration()
client = KalturaClient(config)
```
 
## Kaltura Session
 
Because the Kaltura API is stateless, every request made to the API requires an authentication session to be passed along with the request. With the client library, it’s easy to set it once using the [`session.start`](https://developer.kaltura.com/console/service/session/action/start) API action, like this:

```python
ks = client.session.start(
      <"ADMIN SECRET">,
      "vpaas@kaltura.com",
      KalturaSessionType.ADMIN,
      <PARTNER ID>) 
client.setKs(ks)
```
Try it interactively [with this workflow](https://developer.kaltura.com/workflows/Generate_API_Sessions/Authentication) or [read here](https://developer.kaltura.com/api-docs/VPaaS-API-Getting-Started/Kaltura_API_Authentication_and_Security.html/) about other ways to create a Kaltura Session.

 
## Uploading Media Files
If you're working in a web environment, we highly recommend using the [jQuery Chunked File Upload Library](https://github.com/kaltura/chunked-file-upload-jquery). This library handles chunking files in Javascript, automatically determining the optimal chunk size and number of parallel uploaded chunks, as well as handle pause-and-resume and retry in case of temporary network failures. Otherwise, follow the steps below: 
 
**Step 1: Create an Upload Token**

You’ll use [`uploadToken.add`](https://developer.kaltura.com/console/service/uploadToken/action/add) to create an uploadToken for your new video.
```
uploadToken = KalturaUploadToken()
token = client.uploadToken.add(uploadToken);
```
**Step 2: Upload the Entry Data**

We’ll call [`uploadToken.upload`](https://developer.kaltura.com/console/service/uploadToken/action/upload) to upload a new video file using the newly created token. If you're working in JavaScript, you can simply use the jQuery File Upload widget.
Kaltura supports uploading big media files in chunks. Chunks can be uploaded in parallel and they will be appended according to their resumeAt position.
If you do not intend to upload the file in chunks, set resume to `false` and finalChunk to `true`.
If you don't have a video file handy, you can right-click [this link](http://cfvod.kaltura.com/pd/p/811441/sp/81144100/serveFlavor/entryId/1_2bjlk7qb/v/2/flavorId/1_d1ft34uv/fileName/Kaltura_Logo_Animation.flv/name/a.flv) to save a sample video of Kaltura's logo.
```
uploadTokenId = token.id
fileData =  open('Kaltura_Logo_Animation.flv', 'r')
resume = False
finalChunk = True	
resumeAt = 0
result = client.uploadToken.upload(uploadTokenId, fileData, resume, finalChunk, resumeAt);
```

**Step 3: Create a Media Entry**

Here’s where you’ll set your video’s name and description use [`media.add`](https://developer.kaltura.com/console/service/media/action/add) to create the entry.
```
entry = KalturaMediaEntry()
entry.name = "Kaltura Logo"
entry.description = "sample video of kaltura logo"
entry.mediaType = KalturaMediaType.VIDEO
entry = client.media.add(entry);
```
**Step 4: Attach the Video**

Now that you have your entry, you need to associate it with the uploaded video token using [`media.addContent`](https://developer.kaltura.com/console/service/media/action/addContent). 
```
resource = KalturaUploadedFileTokenResource()
resource.token = uploadTokenId
mediaEntry = client.media.addContent(entry.id, resource);
```

## Searching Entries 
To retrieve that newly uploaded entry, we'll use the [Kaltura Search API](https://developer.kaltura.com/console/service/eSearch/action/searchEntry). 
**Step 1: Params and Operator**

If you have multiple search conditions, you would set an `AND` or `OR` to your operator, but in this case we’ll only be searching for one item. However, you still need to add a searchItems array to the operator. 
```
searchParams = KalturaESearchEntryParams()
searchParams.searchOperator = KalturaESearchEntryOperator()
searchParams.searchOperator.searchItems = [] 
```
**Step 2: Search Type**

We'll be using the Unified search, which searches through all entry data, such as metadata and captions. Other options are `KalturaESearchEntryMetadataItem` or `KalturaESearchEntryCuePointItem`. We'll add that search item to the first index of the search operator.
```
searchParams.searchOperator.searchItems[0] = KalturaESearchEntryUnifiedItem()
```
**Step 3: Search Term**

We'll search for the kaltura logo sample video, which we named accordingly.
```
searchParams.searchOperator.searchItems[0].searchTerm = "kaltura logo"
```
**Step 4: Search Item Type**

In this case, we want an exact match of the text in our search term. Other options are `partial` or `startsWith`. 
```
searchParams.searchOperator.searchItems[0].itemType = KalturaESearchItemType.EXACT_MATCH
```
**Step 5: Search**
```
result = client.elasticsearch.eSearch.searchEntry(searchParams)
```

Success! The result will return as a list of  `KalturaMediaEntry` objects. 

## Embedding Your Video Player 
You have your entry ID, so you’re just about ready to embed the kaltura player, but first you’ll need a `UI Conf ID`, which is basically the ID of the player in which the video is shown. 
For this you’ll need to log into the KMC and click on the [Studio](https://kmc.kaltura.com/index.php/kmcng/studio/v2) tab. 

Notice that there are two studio options: TV and Universal. 
The Universal player (or mwEmbed as we call it) offers legacy support - such as for enterprise customers using the old internet explorer - and also features interactivity options, like the dual player or In-Video Quizzes. 
The TV player (or playkit) is built on modern javascript and focuses more on performance. Both players are totally responsive. 

1. Create a new player, give it a name, and check out the various player options.
2. Save the player and go back to players list; you should now see it the top of the player list. Notice that player ID - that is your `UI Conf ID`. 
3. Click back to Contents, and select Options>Share & Embed for the entry you'd like to embed, and you can copy the simple version of the embed code for the entry. It includes your Partner ID and the Ui Conf ID mentioned above, and is made up of these elements:

**The script that loads the Player Library**
```
<script src="https://cdnapisec.kaltura.com/p/2365491/sp/236549100/embedIframeJs/uiconf_id/42929331/partner_id/2365491"></script>
```
**The div in the HTML with the ID of choice**
```
<div id="kaltura_player" style="width: 560px; height: 395px;"></div>

```
**And finally, the script that loads the player**
```
<script>
kWidget.embed({
  "targetId": "kaltura_player",
  "wid": "_0000000",
  "uiconf_id": 42929331,
  "flashvars": {},
  "cache_st": 1538910608,
  "entry_id": "1_op0wfo31"
});
</script>
```
Using the code above will quickly embed your video in your webpage. 
However, we recommend that a Kaltura Session be included in the player script, like so: 
```
<script type="text/javascript">
		var kalturaPlayer = KalturaPlayer.setup({
			targetId: "kalturaplayer",
			provider: {
				partnerId: PARTER_ID,
				uiConfId: UI_CONF_ID
			},
			playback: {
				autoplay: true
			}
		});
		var mediaInfo = {
			entryId: ENTRY_ID,
			ks: KS
		};
		kalturaPlayer.loadMedia(mediaInfo);
	</script>
```
## Wrapping Up 
Including a kaltura session allows you to keep track of user analytics for each entry and set permissions and privileges. Notice that in this case, the KS is created on the server side of the app. 

**Congrats! You’ve learned how to:**
- Create a kaltura session 
- Upload media to your Kaltura account 
- Search for your media
- Show your media in a Kaltura Player 

**Next steps:** 
- Read the eSearch [blog post](https://corp.kaltura.com/blog/introducing-esearch-the-new-kaltura-search-api/)
- Learn how to create and handle [thumbnails](https://developer.kaltura.com/api-docs/Engage_and_Publish/kaltura-thumbnail-api.html/)
- Analyze Engagement [Analytics](https://developer.kaltura.com/api-docs/Video-Analytics-and-Insights/media-analytics.html)

You can learn more about these steps in our [docs](https://developer.kaltura.com/api-docs/), play around in the [console](https://developer.kaltura.com/console), or enjoy full interactive experiences with our [workflows](https://developer.kaltura.com/workflows). 

And of course, feel free to reach out at vpaas@kaltura.com if you have any questions.

