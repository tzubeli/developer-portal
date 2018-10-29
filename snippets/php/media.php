<?php

# 1 Create an Upload Token
$uploadToken = new KalturaUploadToken();
$token = $client->uploadToken->add($uploadToken);

# 2 Upload the File Data 

$uploadTokenId = $token->id;
$fileData = "/path/to/file";
$resume = false;
$finalChunk = true;
$resumeAt = -1;

$result = $client->uploadToken->upload($uploadTokenId, $fileData, $resume, $finalChunk, $resumeAt);

# 3 Create the Kaltura Media Entry 

$mediaEntry = new KalturaMediaEntry();
$mediaEntry->name = "Kaltura Logo";
$mediaEntry->description = "sample video of kaltura logo";
$mediaEntry->mediaType = KalturaMediaType::VIDEO;

$entry = $client->media->add($mediaEntry);

# 4 Attach The Video 

$entryId = $entry->id;
$resource = new KalturaUploadedFileTokenResource();
$resource->token = $uploadTokenId;

$result = $client->media->addContent($entryId, $resource);