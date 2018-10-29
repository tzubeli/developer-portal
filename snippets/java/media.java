//1 Create an Upload Token

UploadToken uploadToken = new UploadToken();

AddUploadTokenBuilder requestBuilder = UploadTokenService.add(uploadToken)
.setCompletion(new OnCompletion<Response<UploadToken>>() {
    @Override
    public void onComplete(Response<UploadToken> token) {
        System.out.println(token);
    }
});

// 2 Upload the file data

UploadToken uploadToken = new UploadToken();

AddUploadTokenBuilder requestBuilder = UploadTokenService.add(uploadToken)
.setCompletion(new OnCompletion<Response<UploadToken>>() {
    @Override
    public void onComplete(Response<UploadToken> token) {
        String uploadTokenId = token.id;
		File fileData = new FileInputStream("/path/to/file");
		boolean resume = false;
		boolean finalChunk = true;
		int resumeAt = -1;

		UploadUploadTokenBuilder requestBuilder = UploadTokenService.upload(uploadTokenId, fileData, resume, finalChunk, resumeAt)
	    .setCompletion(new OnCompletion<Response<UploadToken>>() {
	        @Override
	        public void onComplete(Response<UploadToken> result) {
	            System.out.println(result);
	        }
	    });
    }
});

// 3 Create the Kaltura Media Entry 

UploadToken uploadToken = new UploadToken();

AddUploadTokenBuilder requestBuilder = UploadTokenService.add(uploadToken)
.setCompletion(new OnCompletion<Response<UploadToken>>() {
    @Override
    public void onComplete(Response<UploadToken> token) {
        String uploadTokenId = token.id;
		File fileData = new FileInputStream("/path/to/file");
		boolean resume = false;
		boolean finalChunk = true;
		int resumeAt = -1;

		UploadUploadTokenBuilder requestBuilder = UploadTokenService.upload(uploadTokenId, fileData, resume, finalChunk, resumeAt)
	    .setCompletion(new OnCompletion<Response<UploadToken>>() {
	        @Override
	        public void onComplete(Response<UploadToken> result) {
	            MediaEntry mediaEntry = new MediaEntry();
	            AddMediaBuilder requestBuilder = MediaService.add(mediaEntry)
    			.setCompletion(new OnCompletion<Response<MediaEntry>>() {
	        		@Override
	        		public void onComplete(Response<MediaEntry> entry) {
	            		System.out.println(entry);
	        		}
    			});
    		}
	    });
    }
});

// 4 Attach the Video

UploadToken uploadToken = new UploadToken();

AddUploadTokenBuilder requestBuilder = UploadTokenService.add(uploadToken)
.setCompletion(new OnCompletion<Response<UploadToken>>() {
    @Override
    public void onComplete(Response<UploadToken> token) {
        String uploadTokenId = token.id;
		File fileData = new FileInputStream("/path/to/file");
		boolean resume = false;
		boolean finalChunk = true;
		int resumeAt = -1;

		UploadUploadTokenBuilder requestBuilder = UploadTokenService.upload(uploadTokenId, fileData, resume, finalChunk, resumeAt)
	    .setCompletion(new OnCompletion<Response<UploadToken>>() {
	        @Override
	        public void onComplete(Response<UploadToken> result) {
	            MediaEntry mediaEntry = new MediaEntry();
	            AddMediaBuilder requestBuilder = MediaService.add(mediaEntry)
			    .setCompletion(new OnCompletion<Response<MediaEntry>>() {
			        @Override
			        public void onComplete(Response<MediaEntry> entry) {
			            String entryId = entry.id;
						UploadedFileTokenResource resource = new UploadedFileTokenResource();

						AddContentMediaBuilder requestBuilder = MediaService.addContent(entryId, resource)
    					.setCompletion(new OnCompletion<Response<MediaEntry>>() {
					        @Override
					        public void onComplete(Response<MediaEntry> result) {
					            System.out.println(result);
					        }
    					});
        			}
    			});
        	}
    	});
    }
});