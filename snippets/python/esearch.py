# 1 Params and Operator 
searchParams = KalturaESearchEntryParams()
searchParams.searchOperator = KalturaESearchEntryOperator()
searchParams.searchOperator.searchItems = []

# 2 Search Type 
searchParams.searchOperator.searchItems[0] = KalturaESearchUnifiedItem()

# 3 Search Term 
searchParams.searchOperator.searchItems[0].searchTerm = "kaltura logo"

# 4 Search Item Type 
searchParams.searchOperator.searchItems[0].itemType = KalturaESearchItemType.EXACT_MATCH

# 5 Add Highlight 
searchParams.searchOperator.searchItems[0].addHighlight = True

# 6 Search
result = client.elasticsearch.eSearch.searchEntry(searchParams);
print(result);