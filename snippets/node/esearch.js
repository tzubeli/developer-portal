let searchParams = new kaltura.objects.ESearchEntryParams();
searchParams.searchOperator = new kaltura.objects.ESearchEntryOperator();
searchParams.searchOperator.searchItems = [];
searchParams.searchOperator.searchItems[0] = new kaltura.objects.ESearchUnifiedItem();
searchParams.searchOperator.searchItems[0].searchTerm = "kaltura logo";
searchParams.searchOperator.searchItems[0].itemType = kaltura.enums.ESearchItemType.EXACT_MATCH;
searchParams.searchOperator.searchItems[0].addHighlight = true;
let pager = new kaltura.objects.Pager();

kaltura.services.eSearch.searchEntry(searchParams, pager)
.execute(client)
.then(result => {
    console.log(result);
});