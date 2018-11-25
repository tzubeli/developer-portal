# Application Session Management 

An Application Token is useful in cases where different applications with varying permissions need access to your Kaltura account, without using your Admin Secret. 
The appToken is created and customized by the account administrator, and then used by the developers to generate Kaltura Sessions for their respective applications. This allows access to the API to be revoked at any time with the deletion of the appToken. 

## Before You Start

Before you create an appToken, you need to decide whether to create a "blank" appToken, or one preconfigured with permissions. If your only concern is giving access without sharing your Admin secret, a basic appToken is sufficient. But if you want to always limit the permissions of a specific application, you'll need to create the appToken with pre-configured [privileges](https://developer.kaltura.com/api-docs/VPaaS-API-Getting-Started/Kaltura_API_Authentication_and_Security.html). Similarly, it is also possible to limit the appToken to a particular user ID should your implementation call for it. 

> Note: Any configurations (privileges or user ID) included in the creation of the appToken ([`appToken.add`](https://developer.kaltura.com/console/service/appToken/action/add)) *cannot* be overridden when the session is created with that appToken ([`appToken.startSession`](https://developer.kaltura.com/console/service/appToken/action/startSession)). 

The **privileges string** that could be included in the appToken is made up of `key:value` pairs that determine the actions available to this Kaltura Session. The following are common privileges for limiting your appTokens access: 

- `setrole`: When assigning App Tokens to your apps, the easiest way to configure the permitted actions is with User Roles. Roles are created [in the KMC](https://kmc.kaltura.com/index.php/kmcng/administration/roles/list), and give you the option of adding and removing specific actions available to the app. The ID of the Role is then mapped to the `setrole` privilege key in the permissions string. This allows you to easily manage the permitted actions by editing the role at any time after.
- `privacycontext`: If you want to limit the app to the content of a specific category, you could [set entitlements](https://kmc.kaltura.com/index.php/kmcng/settings/integrationSettings) on that category and map it to the `privacycontext` key (examples below). Keep in mind however, that if you set the category's Content Privacy to Private, all end users who will need to access the content in this category must be added as members of the category.

> Note: While a user ID *can* be added to an App Token during session generation (if no user ID was specified in the App Token creation), privileges can NOT be added during session generation. 


## Creating the App Token 

We will cover App Token creation with and without pre-configured privileges. Notice that the App Token has a sessionType. If set to type ADMIN (2), any session created with it will be a ADMIN session. If set to USER (0), however, various actions including `baseEntry.list`, will not be available. A USER App Token would be useful in cases where the application is only uploading media but not viewing it afterwards. Furthermore, we recommend using hash of type [`SHA256`](https://en.wikipedia.org/wiki/SHA-2), but whichever you use, make sure to be consistent in the session creation. 

### Basic App Token 

We'll start with an App Token without privileges, without a user, and without an expiry date, using [`appToken.add`](https://developer.kaltura.com/console/service/appToken/action/add):

```
appToken = KalturaAppToken()
appToken.description = "Basic App Token"
appToken.hashType = KalturaAppTokenHashType.SHA256
appToken.sessionType = KalturaSessionType.ADMIN

result = client.appToken.add(appToken);
print(result);
```

In the result you'll see an `id` as well as a `token`. Hold on to those as you'll need them for session creation. You can also view all your App Tokens with the [`appToken.list`](https://developer.kaltura.com/console/service/appToken/action/list) action. 

### Set a User Role

The easy way to create a User Role is [in the KMC](https://kmc.kaltura.com/index.php/kmcng/administration/roles/list). You'll have options to name and describe the new role (make it specific) and then select permitted actions. You'll see that for each category, there is the option to allow all permissions, or to select specific permissions. For example, under Content Moderation, you may allow this User Role to perform all actions except for deleting. You can also switch off a specific category altogether. Hit save and you should now see your new User Role in the list. 

Alternatively, if you know exactly which actions you'd like to include in your User Role, you can use the [`userRole.add`](https://developer.kaltura.com/console/service/userRole/action/add) API action to create a new role. You can see all of the available permission names and descriptions by listing them with [`permission.list`](https://developer.kaltura.com/console/service/permission/action/list). Be sure to set the status of your role to Active (1).

> Note: You will not be able to see in the KMC any roles that are created outside the KMC.

You can get a list of all your existing roles, with the [`userRole.list`](https://developer.kaltura.com/console/service/userRole/action/list) action. Make note of the `id` of your new User Role as you'll be needing it for your App Token, where you can set the role like this: 

```
appToken.sessionPrivileges = "setrole:1234567"
```

### Add a Privacy Context 

Adding a privacy context will limit the session to the contents of a particular category.  
To enable entitlements on the category, select Add Entitlements in the Integration Settings in [KMC](https://kmc.kaltura.com/index.php/kmcng/settings/integrationSettings). Then select a category and give it a Privacy Context Label. That is the name that should be used in the Privileges String when adding the `privacycontext` key. 

```
appToken.sessionPrivileges = "setrole:1234567,privacycontext:application"
```

### Add a User to the Category

If you set the Content Privacy setting of that category to Private, for users to access this category, they will also need to be members of the category, which can be done in the Entitlements tab in the Category Settings. You can also use the [`categoryUser.add`](https://developer.kaltura.com/console/service/categoryUser/action/add) action, where you'll need the category ID and the user ID, which can be any string identifying that user. 

```
categoryUser = KalturaCategoryUser()
categoryUser.categoryId = 123456789
categoryUser.permissionLevel = KalturaCategoryUserPermissionLevel.MEMBER
categoryUser.userId = "JaneDoe"

result = client.categoryUser.add(categoryUser);
print(result);
```

### Add a User to the App Token 

In cases where you'd like to use App Tokens to grant access to particular users, you can include the user ID during the creation of the App Token (`appToken.add`). When including a user ID in the App Token object, that user ID can not be overridden when calling `appToken.startSession`. This can be useful when wanting to grant particular users with API access and ensure they can not mask their ID as someone else while carrying API actions.

Let's bring it all together. We have a user. We have a User Role, and its ID. We will use hash of type `SHA256` and give the session a duration of one day. 

```
appToken = KalturaAppToken()
appToken.description = "App Token with User and Privileges"
appToken.hashType = KalturaAppTokenHashType.SHA256
appToken.sessionDuration = 86400
appToken.sessionPrivileges = "setrole:1234567"
appToken.sessionType = KalturaSessionType.ADMIN
appToken.sessionUserId = "dummyuser@kaltura.com"

result = client.appToken.add(appToken);
print(result);
```

> Reminder: You can get a list of all your App Tokens with the [`appToken.list`](https://developer.kaltura.com/console/service/appToken/action/list) action. 

## Generate Kaltura Sessions with the App Token 

The Kaltura Session generated with the App Token will have the content and action permissions that were configured in the App Token. 

### Step 1: Create a Kaltura Session 

Because a Kaltura Session is required for every call to the API, we'll need to create an unprivileged session before being able to create the App Token session. We use the [`session.startWidgetSession`](https://developer.kaltura.com/console/service/appToken/action/startSession) action with the widget ID, which is your partner ID with an underscore prefix. 

```
widgetId = "_1234569"
expiry = 86400

result = client.session.startWidgetSession(widgetId, expiry);
ks = result.ks 
```

The result will contain that unprivileged KS which you need for the next step.

### Step 2: Compute the Token Hash

We'll create a hash of the App Token `token` value together with the unprivileged KS, using a hash function in the language of your choice. 

> Important Note: Make sure to use the same hash type as the one used for creating the App Token.

```
hashString = hashlib.sha256(result.ks.encode('ascii') + appTokenValue.encode()).hexdigest()
```

The resulting string is the tokenHash which you'll use in the next step. 

### Step 3: Generate the Session 

We'll use the [`App Token.startSession`](https://developer.kaltura.com/console/service/appToken/action/startSession) action with the unprivileged KS, the hashToken, and the token `ID`: 

> Note: If you created an App Token with a user ID, it will override any user ID value used in `appToken.startSession`. 

```
id = "<token ID>"
tokenHash = "<token hash>"
userId = "enduser"
type = KalturaSessionType.ADMIN
expiry = 3600
sessionPrivileges = ""

result = client.appToken.startSession(id, tokenHash, userId, type, expiry, sessionPrivileges);
print(result);

```

You'll notice that the response contains any existing configurations from the App Token creation, regardless of what was passed in during the startSession. The expiry is set to an hour (although you can change this), meaning that after that time has passed, a new session will need to be generated. So if you wish to change permissions on this App Token, you can make those changes to the Role, User, or Privacy Context associated with the App Token. 

Congrats - now let's build an app! Get started [here](https://developer.kaltura.com/api-docs/VPaaS-API-Getting-Started/Getting-Started-VPaaS-API.html/). 

