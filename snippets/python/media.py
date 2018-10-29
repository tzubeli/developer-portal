# 1 Create an Upload Token
upload_token = KalturaUploadToken()
token = client.uploadToken.add(upload_token);

# 2 Upload the File Data 
upload_token_id = token.id
file_data =  open('Kaltura_Logo_Animation.flv', 'r')
resume = False
final_chunk = True	
resume_at = 0
result = client.uploadToken.upload(upload_token_id, file_data, resume, final_chunk, resume_at)

# 3 Create the Kaltura Media Entry 
media_entry = KalturaMediaEntry()
media_entry.name = "Kaltura Logo"
media_entry.description = "sample video of kaltura logo"
media_entry.mediaType = KalturaMediaType.VIDEO
entry = client.media.add(media_entry)

# 4 Attach The Video 
entry_id = entry.id
resource = KalturaUploadedFileTokenResource()
resource.token = upload_token_id

result = client.media.addContent(entry_id, resource)