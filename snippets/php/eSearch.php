<?php
# 1 Params and Operator 
$elasticSearchPlugin = KalturaElasticSearchClientPlugin::get($client);
$searchParams = new KalturaESearchEntryParams();
$searchParams->searchOperator = new KalturaESearchEntryOperator();
$searchParams->searchOperator->searchItems = [];

# 2 Search Type 
$searchParams->searchOperator->searchItems[0] = new KalturaESearchUnifiedItem();

# 3 Search Term 
$searchParams->searchOperator->searchItems[0]->searchTerm = "kaltura logo";

# 4 Search Item Type 
$searchParams->searchOperator->searchItems[0]->itemType = KalturaESearchItemType::EXACT_MATCH;

# 5 Add Highlight 
$searchParams->searchOperator->searchItems[0]->addHighlight = true;

# 6 Search
$result = $elasticSearchPlugin->eSearch->searchEntry($searchParams);

