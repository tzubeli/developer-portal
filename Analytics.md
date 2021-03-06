The Kaltura Analytics API reports provide you with the insight you need to manage your content, reach your audience, and optimize your video workflow. You can view a quick snapshot of high-level figures, or drill down to user-specific or video-specific information. Use the analytics reports to gain business insights and understand user trends. 

## Report Service

Kaltura analytics data is exposed via the [`Report Service`](https://developer.kaltura.com/console/service/report).   
A number of actions within this service will enable you to pull data for Content, User Engagement, Bandwith and Storage, and System. A specific set of filters will ensure you get the insights you need. 

You can follow the [Interactive Code Workflow,](https://developer.kaltura.com/workflows/Review_Media_Analytics/Analytics_Reports) or continue reading to learn about a few basic reports:

## Getting Started 

The Report API contains a couple different actions for returning data. For the purpose of this doc, we will use [`report.getTable`](https://developer.kaltura.com/console/service/report/action/getTable), which returns a JSON object of the insights data, as well as the respective column headers. Other actions include [`report.getGraphs`](https://developer.kaltura.com/console/service/report/action/getGraphs) which returns points for a graph UI, and [`report.getUrlForReportAsCsv`](https://developer.kaltura.com/console/service/report/action/getUrlForReportAsCsv), which produces a CSV report based on your given headers. 

For any `report.getTable` action, you need: 
- **reportType:** Object enum or integer (listed below) - determines the type of report to produce 
- **reportInputFilter:** Can be of type `KalturaReportInputFilter` or `KalturaEndUserReportInputFilter`. It is required, and allows you to set date range, specific categories, keywords, or custom variables. 
- **pager:** Unlike some other Kaltura APIs, a pager is always required for this action. It contains the `pageIndex` (starting with zero) and `pageSize` (number of results you'd like per page). 

Other optional parameters: 
- **objectIds:** For when you want to drill down on statistics for a specific entry or user. 
- **order:** Order by any column from the given results table

Let's look at a few available report types: 

## Top Content

So you want to basic insights on how much engagement your entries have been drawing in the last month, like how many times the video has been loaded and then actually played, and when users are likely to drop-off. In the example below, we set the `reportType` to Top Content (1), the date range to the entire month of October, and a page size of 20. The result contains a list of entries and their statistics.

```python
reportType = KalturaReportType.TOP_CONTENT
reportInputFilter = KalturaReportInputFilter()
reportInputFilter.fromDay = "20181001"
reportInputFilter.toDay = "20181031"
pager = KalturaFilterPager()
pager.pageIndex = 0
pager.pageSize = 20

result = client.report.getTable(reportType, reportInputFilter, pager, order);
```

The result header looks like this: 
```
object_id,entry_name,count_plays,sum_time_viewed,avg_time_viewed,count_loads,load_play_ratio,avg_view_drop_off
```

Maybe you only want insights for a particular *channel* in your media. In this case you'd set `categories` on the filter to the *full name* of your category, which can be found in the KMC by hovering over the category name, OR by calling `category.get` action with the category ID. The full name of the category usually includes its parent directories. 

```
reportInputFilter.categories = "Mediaspace>site>channels>podcasts"
```

If you set `objectIds` as one of the entries from the results, change the reportType to User Engagement (11), and set the filter to an End User filter, you'd get specific user engagement stats on that entry: 

```python 
reportType = KalturaReportType.USER_ENGAGEMENT
reportInputFilter = KalturaEndUserReportInputFilter()
reportInputFilter.categories = "Mediaspace>site>channels>podcasts"
reportInputFilter.fromDay = "20181001"
reportInputFilter.toDay = "20181031"
pager = KalturaFilterPager()
pager.pageIndex = 0
pager.pageSize = 20
order = ""
objectIds = "1_qwerty"
```

The result header looks like this: 
```
name,unique_videos,count_plays,sum_time_viewed,avg_time_viewed,avg_view_drop_off,count_loads,load_play_ratio
```

## User Reports 
Perhaps you're interested in which employees at the company have contributed the most videos. In the example below, we set the `reportType` to Top Contributors (5) and order by the total count. 

```
reportType = KalturaReportType.TOP_CONTRIBUTORS
reportInputFilter = KalturaReportInputFilter()
reportInputFilter.fromDay = "20181001"
reportInputFilter.toDay = "20181031"
pager = KalturaFilterPager()
pager.pageIndex = 0
pager.pageSize = 20
order = "count_total"
```

Result header: 
```
object_id,name,count_total,count_video,count_audio,count_image,count_mix
```

You can also get engagement insights by **geographic region**, by changing the `reportType` to Map Overlay (4). Setting the `objectIds` to one of the regions from the results will break down those insights by city. 

```
reportType = KalturaReportType.MAP_OVERLAY
reportInputFilter = KalturaReportInputFilter()
reportInputFilter.fromDay = "20181001"
reportInputFilter.toDay = "20181031"
pager = KalturaFilterPager()
pager.pageIndex = 1
pager.pageSize = 25
order = "count_plays"
objectIds = "Germany"
```

Result header: 
```
object_id,location_name,count_plays,count_plays_25,count_plays_50,count_plays_75,count_plays_100,play_through_ratio
```

As you can see, there are many ways to customize your report based on your business use cases. You can easily try them out using our [Interactive Console](https://developer.kaltura.com/console/service/report/action/getTable) or see what they look like in the [KMC](https://kmc.kaltura.com/index.php/kmc/kmc4#analytics|contentDashboard). 

## Integrated Analytics Partners

Already using an analytics or audience measurement tool? Leverage the Kaltura pre-integrated plugins for all major analytics providers and consolidate your data securely and reliably.

To learn how to setup player plugins read: [Configuring Analytics Plugins](https://knowledge.kaltura.com/universal-studio-information-guide#configuring_analytics).

* [Youbora](https://knowledge.kaltura.com/node/1675)
* [comScore](http://player.kaltura.com/docs/ComscoreAnalytics)
* [Nielsen](http://player.kaltura.com/docs/NielsenVideoCensus)
* [Google Analytics](https://knowledge.kaltura.com/node/1148#googleanalytics)
* [Chartbeat](http://support.chartbeat.com/docs/video.html#kaltura)

## Report Types by Number 

- Top Content (1)
- Content Dropoff (2)
- Content Interactions (3)
- Map Overlay (4)
- Top Contributors (5)
- Top Syndication (6)
- Content Contributions (7) 
- User Engagement (11)
- Specific User Engagement (12)
- User Top Content (13)
- User Content Dropoff (14)
- User Content Interactions (15)
- Applications (16)
- User Usage (17)
- Specific User Usage (18)
- Top Creators (20)
- Platforms (21)
- Operating System (22)
- Browsers (23)
- Live (24)
- Top Playback Context (25)
- VPaaS Usage (26)
- Entry Usage (27) 
- Reach Usage (28)
- Top Custom Var1 (29)
- Partner Usage (201)
