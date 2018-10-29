// 1 Params and Operator 
ESearchEntryParams searchParams = new ESearchEntryParams();
searchParams.setSearchOperator(new ESearchEntryOperator());
searchParams.getSearchOperator().setSearchItems(new ArrayList<ESearchEntryBaseItem>(1));

// 2 Search Type 
searchParams.getSearchOperator().getSearchItems().set(0, new ESearchUnifiedItem());

// 3 Search Term
searchParams.getSearchOperator().getSearchItems().get(0).setSearchTerm("kaltura logo");

// 4 Search Item Type 
searchParams.getSearchOperator().getSearchItems().get(0).setItemType(ESearchItemType.EXACT_MATCH.getValue());

// 5 Add Highlight 
searchParams.getSearchOperator().getSearchItems().get(0).setAddHighlight(true);

// 6 Search 
SearchEntryESearchBuilder requestBuilder = ESearchService.searchEntry(searchParams)
    .setCompletion(new OnCompletion<Response<ESearchEntryResponse>>() {
        @Override
        public void onComplete(Response<ESearchEntryResponse> result) {
            System.out.println(result);
        }
    });